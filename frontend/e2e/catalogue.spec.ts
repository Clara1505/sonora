import { test, expect } from '@playwright/test';

const API = 'http://localhost:8080';

const MOCK_TRACKS = [
  {
    id: 1, title: 'One More Time', duration: 320, genre: 'ELECTRONIC', mood: 'HYPE',
    filePath: '/tracks/one-more-time.mp3',
    artist: { id: 1, name: 'Daft Punk' },
    album: { id: 1, title: 'Discovery' },
  },
  {
    id: 2, title: 'Harder Better Faster Stronger', duration: 224, genre: 'ELECTRONIC', mood: 'FOCUS',
    filePath: '/tracks/harder.mp3',
    artist: { id: 1, name: 'Daft Punk' },
    album: null,
  },
];

async function injectToken(page: any) {
  await page.addInitScript(() => {
    localStorage.setItem('sonora_token', 'fake-jwt-token');
    localStorage.setItem('sonora_user', JSON.stringify({ email: 'user@sonora.dev', role: 'USER' }));
  });
}

async function mockAllApis(page: any, tracks = MOCK_TRACKS) {
  await page.route(`${API}/api/tracks**`, route =>
    route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify(tracks) })
  );
  for (const path of ['albums', 'artists', 'playlists', 'favorites']) {
    await page.route(`${API}/api/${path}**`, route =>
      route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify([]) })
    );
  }
  await page.route(`${API}/api/stats**`, route =>
    route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify({}) })
  );
}

test.describe('Catalogue', () => {

  test.beforeEach(async ({ page }) => {
    await injectToken(page);
    await mockAllApis(page);
  });

  test('affiche les pistes du catalogue', async ({ page }) => {
    await page.goto('/catalogue');
    await expect(page.getByText('One More Time')).toBeVisible();
    await expect(page.getByText('Harder Better Faster Stronger')).toBeVisible();
  });

  test("affiche le nom de l'artiste pour chaque piste", async ({ page }) => {
    await page.goto('/catalogue');
    await expect(page.getByText('Daft Punk').first()).toBeVisible();
  });

  test('la page catalogue se charge correctement', async ({ page }) => {
    await page.goto('/catalogue');
    await expect(page).toHaveURL('/catalogue');
    await expect(page.locator('body')).not.toContainText('Erreur');
  });

});

test.describe("Page d'accueil", () => {

  test("affiche la page d'accueil sans être connecté", async ({ page }) => {
    await page.goto('/');
    await expect(page).toHaveURL('/');
  });

  test('contient un lien de connexion', async ({ page }) => {
    await page.goto('/');
    await expect(page.locator('a[href="/login"]').first()).toBeVisible();
  });

  test("contient un lien d'inscription", async ({ page }) => {
    await page.goto('/');
    await expect(page.locator('a[href="/register"]').first()).toBeVisible();
  });

});
