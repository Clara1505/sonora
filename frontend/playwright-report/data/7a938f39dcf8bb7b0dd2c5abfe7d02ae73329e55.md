# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: auth.spec.ts >> Page de connexion >> affiche une erreur si les champs sont vides
- Location: e2e/auth.spec.ts:59:7

# Error details

```
Error: expect(locator).toBeVisible() failed

Locator: getByText('L\'email est requis')
Expected: visible
Timeout: 5000ms
Error: element(s) not found

Call log:
  - Expect "toBeVisible" with timeout 5000ms
  - waiting for getByText('L\'email est requis')

```

# Page snapshot

```yaml
- generic [ref=e2]:
  - generic [ref=e3]:
    - link "Sonora" [ref=e4] [cursor=pointer]:
      - /url: /
      - img [ref=e5]: music_note
      - generic [ref=e6]: Sonora
    - link "Se connecter" [ref=e7] [cursor=pointer]:
      - /url: /login
      - generic [ref=e8]: Se connecter
    - link "S'inscrire" [ref=e11] [cursor=pointer]:
      - /url: /register
      - generic [ref=e12]: S'inscrire
  - main [ref=e15]:
    - generic [ref=e18]:
      - generic [ref=e21]:
        - heading "🎵 Sonora" [level=1] [ref=e22]
        - paragraph [ref=e23]: Connexion
      - generic [ref=e25]:
        - generic [ref=e28]:
          - generic [ref=e29]:
            - text: Email
            - generic [ref=e30]: "*"
          - textbox "Email" [ref=e32]:
            - /placeholder: exemple@email.com
          - img [ref=e34]: email
        - generic [ref=e38]:
          - generic [ref=e39]:
            - text: Mot de passe
            - generic [ref=e40]: "*"
          - textbox "Mot de passe" [ref=e42]
          - button [ref=e44] [cursor=pointer]:
            - img [ref=e45]: visibility_off
        - button "Se connecter" [disabled]:
          - generic: Se connecter
      - paragraph [ref=e50]:
        - text: Pas encore de compte ?
        - link "S'inscrire" [ref=e51] [cursor=pointer]:
          - /url: /register
```

# Test source

```ts
  1   | import { test, expect } from '@playwright/test';
  2   | 
  3   | const API = 'http://localhost:8080';
  4   | 
  5   | async function injectToken(page: any) {
  6   |   await page.addInitScript(() => {
  7   |     localStorage.setItem('sonora_token', 'fake-jwt-token');
  8   |     localStorage.setItem('sonora_user', JSON.stringify({ email: 'user@sonora.dev', role: 'USER' }));
  9   |   });
  10  | }
  11  | 
  12  | async function mockLoginSuccess(page: any) {
  13  |   await page.route(`${API}/api/auth/login`, route =>
  14  |     route.fulfill({
  15  |       status: 200,
  16  |       contentType: 'application/json',
  17  |       body: JSON.stringify({ token: 'fake-jwt-token', email: 'user@sonora.dev', role: 'USER' }),
  18  |     })
  19  |   );
  20  | }
  21  | 
  22  | async function mockLoginFailure(page: any) {
  23  |   await page.route(`${API}/api/auth/login`, route =>
  24  |     route.fulfill({
  25  |       status: 401,
  26  |       contentType: 'application/json',
  27  |       body: JSON.stringify({ message: 'Identifiants invalides' }),
  28  |     })
  29  |   );
  30  | }
  31  | 
  32  | async function mockRegisterSuccess(page: any) {
  33  |   await page.route(`${API}/api/auth/register`, route =>
  34  |     route.fulfill({
  35  |       status: 201,
  36  |       contentType: 'application/json',
  37  |       body: JSON.stringify({ token: 'fake-jwt-token', email: 'nouveau@sonora.dev', role: 'USER' }),
  38  |     })
  39  |   );
  40  | }
  41  | 
  42  | async function mockProtectedRoutes(page: any) {
  43  |   for (const path of ['playlists', 'tracks', 'albums', 'artists', 'stats', 'favorites']) {
  44  |     await page.route(`${API}/api/${path}**`, route =>
  45  |       route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify([]) })
  46  |     );
  47  |   }
  48  | }
  49  | 
  50  | test.describe('Page de connexion', () => {
  51  | 
  52  |   test('affiche le formulaire de connexion', async ({ page }) => {
  53  |     await page.goto('/login');
  54  |     await expect(page.locator('input[formControlName="email"]')).toBeVisible();
  55  |     await expect(page.locator('input[formControlName="password"]')).toBeVisible();
  56  |     await expect(page.locator('button[type="submit"]')).toBeVisible();
  57  |   });
  58  | 
  59  |   test('affiche une erreur si les champs sont vides', async ({ page }) => {
  60  |     await page.goto('/login');
  61  |     await page.locator('button[type="submit"]').click({ force: true });
> 62  |     await expect(page.getByText("L'email est requis")).toBeVisible();
      |                                                        ^ Error: expect(locator).toBeVisible() failed
  63  |     await expect(page.getByText('Le mot de passe est requis')).toBeVisible();
  64  |   });
  65  | 
  66  |   test('affiche une erreur pour un email invalide', async ({ page }) => {
  67  |     await page.goto('/login');
  68  |     await page.locator('input[formControlName="email"]').fill('pas-un-email');
  69  |     await page.locator('input[formControlName="password"]').fill('motdepasse');
  70  |     await page.locator('button[type="submit"]').click({ force: true });
  71  |     await expect(page.getByText('Email invalide')).toBeVisible();
  72  |   });
  73  | 
  74  |   test("affiche le message d'erreur API en cas d'échec", async ({ page }) => {
  75  |     await mockLoginFailure(page);
  76  |     await page.goto('/login');
  77  |     await page.locator('input[formControlName="email"]').fill('mauvais@sonora.dev');
  78  |     await page.locator('input[formControlName="password"]').fill('mauvaismdp');
  79  |     await page.locator('button[type="submit"]').click();
  80  |     await expect(page.getByText('Identifiants invalides')).toBeVisible();
  81  |   });
  82  | 
  83  |   test('redirige vers /playlists après une connexion réussie', async ({ page }) => {
  84  |     await mockLoginSuccess(page);
  85  |     await mockProtectedRoutes(page);
  86  |     await page.goto('/login');
  87  |     await page.locator('input[formControlName="email"]').fill('user@sonora.dev');
  88  |     await page.locator('input[formControlName="password"]').fill('password123');
  89  |     await page.locator('button[type="submit"]').click();
  90  |     await page.waitForURL('**/playlists', { timeout: 10000 });
  91  |     await expect(page).toHaveURL('/playlists');
  92  |   });
  93  | 
  94  |   test("contient un lien vers la page d'inscription", async ({ page }) => {
  95  |     await page.goto('/login');
  96  |     await expect(
  97  |       page.locator('a[href="/register"]').first()
  98  |     ).toBeVisible();
  99  |   });
  100 | 
  101 | });
  102 | 
  103 | test.describe("Page d'inscription", () => {
  104 | 
  105 |   test("affiche le formulaire d'inscription", async ({ page }) => {
  106 |     await page.goto('/register');
  107 |     await expect(page.locator('input[formControlName="email"]')).toBeVisible();
  108 |     await expect(page.locator('input[formControlName="password"]')).toBeVisible();
  109 |     await expect(page.locator('input[formControlName="confirmPassword"]')).toBeVisible();
  110 |   });
  111 | 
  112 |   test('affiche une erreur si les mots de passe ne correspondent pas', async ({ page }) => {
  113 |     await page.goto('/register');
  114 |     await page.locator('input[formControlName="email"]').fill('test@sonora.dev');
  115 |     await page.locator('input[formControlName="password"]').fill('Password1!');
  116 |     await page.locator('input[formControlName="confirmPassword"]').fill('AutrePassword1!');
  117 |     await page.locator('button[type="submit"]').click({ force: true });
  118 |     await expect(page.getByText('Les mots de passe ne correspondent pas')).toBeVisible();
  119 |   });
  120 | 
  121 |   test('redirige après une inscription réussie', async ({ page }) => {
  122 |     await mockRegisterSuccess(page);
  123 |     await mockProtectedRoutes(page);
  124 |     await page.goto('/register');
  125 |     await page.locator('input[formControlName="email"]').fill('nouveau@sonora.dev');
  126 |     await page.locator('input[formControlName="password"]').fill('Password1!');
  127 |     await page.locator('input[formControlName="confirmPassword"]').fill('Password1!');
  128 |     await page.locator('button[type="submit"]').click();
  129 |     await expect(page).not.toHaveURL('/register');
  130 |   });
  131 | 
  132 | });
  133 | 
  134 | test.describe("Garde d'authentification", () => {
  135 | 
  136 |   test('redirige vers /login si non connecté — /catalogue', async ({ page }) => {
  137 |     await page.goto('/catalogue');
  138 |     await expect(page).toHaveURL('/login');
  139 |   });
  140 | 
  141 |   test('redirige vers /login si non connecté — /profile', async ({ page }) => {
  142 |     await page.goto('/profile');
  143 |     await expect(page).toHaveURL('/login');
  144 |   });
  145 | 
  146 |   test('redirige vers /login si non connecté — /playlists', async ({ page }) => {
  147 |     await page.goto('/playlists');
  148 |     await expect(page).toHaveURL('/login');
  149 |   });
  150 | 
  151 |   test('accès au catalogue autorisé après connexion', async ({ page }) => {
  152 |     await injectToken(page);
  153 |     await mockProtectedRoutes(page);
  154 |     await page.goto('/catalogue');
  155 |     await expect(page).toHaveURL('/catalogue');
  156 |   });
  157 | 
  158 | });
  159 | 
```