import { test, expect } from '@playwright/test';

const API = 'http://localhost:8080';

async function injectToken(page: any) {
  await page.addInitScript(() => {
    localStorage.setItem('sonora_token', 'fake-jwt-token');
    localStorage.setItem('sonora_user', JSON.stringify({ email: 'user@sonora.dev', role: 'USER' }));
  });
}

async function mockLoginSuccess(page: any) {
  await page.route(`${API}/api/auth/login`, route =>
    route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({ token: 'fake-jwt-token', email: 'user@sonora.dev', role: 'USER' }),
    })
  );
}

async function mockLoginFailure(page: any) {
  await page.route(`${API}/api/auth/login`, route =>
    route.fulfill({
      status: 401,
      contentType: 'application/json',
      body: JSON.stringify({ message: 'Identifiants invalides' }),
    })
  );
}

async function mockRegisterSuccess(page: any) {
  await page.route(`${API}/api/auth/register`, route =>
    route.fulfill({
      status: 201,
      contentType: 'application/json',
      body: JSON.stringify({ token: 'fake-jwt-token', email: 'nouveau@sonora.dev', role: 'USER' }),
    })
  );
}

async function mockProtectedRoutes(page: any) {
  for (const path of ['playlists', 'tracks', 'albums', 'artists', 'stats', 'favorites']) {
    await page.route(`${API}/api/${path}**`, route =>
      route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify([]) })
    );
  }
}

test.describe('Page de connexion', () => {

  test('affiche le formulaire de connexion', async ({ page }) => {
    await page.goto('/login');
    await expect(page.locator('input[formControlName="email"]')).toBeVisible();
    await expect(page.locator('input[formControlName="password"]')).toBeVisible();
    await expect(page.locator('button[type="submit"]')).toBeVisible();
  });

  test('affiche une erreur si les champs sont vides', async ({ page }) => {
    await page.goto('/login');
    await page.locator('button[type="submit"]').click({ force: true });
    await expect(page.getByText("L'email est requis")).toBeVisible();
    await expect(page.getByText('Le mot de passe est requis')).toBeVisible();
  });

  test('affiche une erreur pour un email invalide', async ({ page }) => {
    await page.goto('/login');
    await page.locator('input[formControlName="email"]').fill('pas-un-email');
    await page.locator('input[formControlName="password"]').fill('motdepasse');
    await page.locator('button[type="submit"]').click({ force: true });
    await expect(page.getByText('Email invalide')).toBeVisible();
  });

  test("affiche le message d'erreur API en cas d'échec", async ({ page }) => {
    await mockLoginFailure(page);
    await page.goto('/login');
    await page.locator('input[formControlName="email"]').fill('mauvais@sonora.dev');
    await page.locator('input[formControlName="password"]').fill('mauvaismdp');
    await page.locator('button[type="submit"]').click();
    await expect(page.getByText('Identifiants invalides')).toBeVisible();
  });

  test('redirige vers /playlists après une connexion réussie', async ({ page }) => {
    await mockLoginSuccess(page);
    await mockProtectedRoutes(page);
    await page.goto('/login');
    await page.locator('input[formControlName="email"]').fill('user@sonora.dev');
    await page.locator('input[formControlName="password"]').fill('password123');
    await page.locator('button[type="submit"]').click();
    await page.waitForURL('**/playlists', { timeout: 10000 });
    await expect(page).toHaveURL('/playlists');
  });

  test("contient un lien vers la page d'inscription", async ({ page }) => {
    await page.goto('/login');
    await expect(
      page.locator('a[href="/register"]').first()
    ).toBeVisible();
  });

});

test.describe("Page d'inscription", () => {

  test("affiche le formulaire d'inscription", async ({ page }) => {
    await page.goto('/register');
    await expect(page.locator('input[formControlName="email"]')).toBeVisible();
    await expect(page.locator('input[formControlName="password"]')).toBeVisible();
    await expect(page.locator('input[formControlName="confirmPassword"]')).toBeVisible();
  });

  test('affiche une erreur si les mots de passe ne correspondent pas', async ({ page }) => {
    await page.goto('/register');
    await page.locator('input[formControlName="email"]').fill('test@sonora.dev');
    await page.locator('input[formControlName="password"]').fill('Password1!');
    await page.locator('input[formControlName="confirmPassword"]').fill('AutrePassword1!');
    await page.locator('button[type="submit"]').click({ force: true });
    await expect(page.getByText('Les mots de passe ne correspondent pas')).toBeVisible();
  });

  test('redirige après une inscription réussie', async ({ page }) => {
    await mockRegisterSuccess(page);
    await mockProtectedRoutes(page);
    await page.goto('/register');
    await page.locator('input[formControlName="email"]').fill('nouveau@sonora.dev');
    await page.locator('input[formControlName="password"]').fill('Password1!');
    await page.locator('input[formControlName="confirmPassword"]').fill('Password1!');
    await page.locator('button[type="submit"]').click();
    await expect(page).not.toHaveURL('/register');
  });

});

test.describe("Garde d'authentification", () => {

  test('redirige vers /login si non connecté — /catalogue', async ({ page }) => {
    await page.goto('/catalogue');
    await expect(page).toHaveURL('/login');
  });

  test('redirige vers /login si non connecté — /profile', async ({ page }) => {
    await page.goto('/profile');
    await expect(page).toHaveURL('/login');
  });

  test('redirige vers /login si non connecté — /playlists', async ({ page }) => {
    await page.goto('/playlists');
    await expect(page).toHaveURL('/login');
  });

  test('accès au catalogue autorisé après connexion', async ({ page }) => {
    await injectToken(page);
    await mockProtectedRoutes(page);
    await page.goto('/catalogue');
    await expect(page).toHaveURL('/catalogue');
  });

});
