# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: catalogue.spec.ts >> Page d'accueil >> affiche la page d'accueil sans être connecté
- Location: e2e/catalogue.spec.ts:69:7

# Error details

```
Error: expect(page).toHaveURL(expected) failed

Expected: "http://localhost:4200/"
Received: "http://localhost:4200/login"
Timeout:  5000ms

Call log:
  - Expect "toHaveURL" with timeout 5000ms
    9 × unexpected value "http://localhost:4200/login"

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
  1  | import { test, expect } from '@playwright/test';
  2  | 
  3  | const API = 'http://localhost:8080';
  4  | 
  5  | const MOCK_TRACKS = [
  6  |   {
  7  |     id: 1, title: 'One More Time', duration: 320, genre: 'ELECTRONIC', mood: 'HYPE',
  8  |     filePath: '/tracks/one-more-time.mp3',
  9  |     artist: { id: 1, name: 'Daft Punk' },
  10 |     album: { id: 1, title: 'Discovery' },
  11 |   },
  12 |   {
  13 |     id: 2, title: 'Harder Better Faster Stronger', duration: 224, genre: 'ELECTRONIC', mood: 'FOCUS',
  14 |     filePath: '/tracks/harder.mp3',
  15 |     artist: { id: 1, name: 'Daft Punk' },
  16 |     album: null,
  17 |   },
  18 | ];
  19 | 
  20 | async function injectToken(page: any) {
  21 |   await page.addInitScript(() => {
  22 |     localStorage.setItem('sonora_token', 'fake-jwt-token');
  23 |     localStorage.setItem('sonora_user', JSON.stringify({ email: 'user@sonora.dev', role: 'USER' }));
  24 |   });
  25 | }
  26 | 
  27 | async function mockAllApis(page: any, tracks = MOCK_TRACKS) {
  28 |   await page.route(`${API}/api/tracks**`, route =>
  29 |     route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify(tracks) })
  30 |   );
  31 |   for (const path of ['albums', 'artists', 'playlists', 'favorites']) {
  32 |     await page.route(`${API}/api/${path}**`, route =>
  33 |       route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify([]) })
  34 |     );
  35 |   }
  36 |   await page.route(`${API}/api/stats**`, route =>
  37 |     route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify({}) })
  38 |   );
  39 | }
  40 | 
  41 | test.describe('Catalogue', () => {
  42 | 
  43 |   test.beforeEach(async ({ page }) => {
  44 |     await injectToken(page);
  45 |     await mockAllApis(page);
  46 |   });
  47 | 
  48 |   test('affiche les pistes du catalogue', async ({ page }) => {
  49 |     await page.goto('/catalogue');
  50 |     await expect(page.getByText('One More Time')).toBeVisible();
  51 |     await expect(page.getByText('Harder Better Faster Stronger')).toBeVisible();
  52 |   });
  53 | 
  54 |   test("affiche le nom de l'artiste pour chaque piste", async ({ page }) => {
  55 |     await page.goto('/catalogue');
  56 |     await expect(page.getByText('Daft Punk').first()).toBeVisible();
  57 |   });
  58 | 
  59 |   test('la page catalogue se charge correctement', async ({ page }) => {
  60 |     await page.goto('/catalogue');
  61 |     await expect(page).toHaveURL('/catalogue');
  62 |     await expect(page.locator('body')).not.toContainText('Erreur');
  63 |   });
  64 | 
  65 | });
  66 | 
  67 | test.describe("Page d'accueil", () => {
  68 | 
  69 |   test("affiche la page d'accueil sans être connecté", async ({ page }) => {
  70 |     await page.goto('/');
> 71 |     await expect(page).toHaveURL('/');
     |                        ^ Error: expect(page).toHaveURL(expected) failed
  72 |   });
  73 | 
  74 |   test('contient un lien de connexion', async ({ page }) => {
  75 |     await page.goto('/');
  76 |     await expect(page.locator('a[href="/login"]').first()).toBeVisible();
  77 |   });
  78 | 
  79 |   test("contient un lien d'inscription", async ({ page }) => {
  80 |     await page.goto('/');
  81 |     await expect(page.locator('a[href="/register"]').first()).toBeVisible();
  82 |   });
  83 | 
  84 | });
  85 | 
```