# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: catalogue.spec.ts >> Catalogue >> affiche le nom de l'artiste pour chaque piste
- Location: e2e/catalogue.spec.ts:54:7

# Error details

```
Error: expect(locator).toBeVisible() failed

Locator: getByText('Daft Punk').first()
Expected: visible
Timeout: 5000ms
Error: element(s) not found

Call log:
  - Expect "toBeVisible" with timeout 5000ms
  - waiting for getByText('Daft Punk').first()

```

# Page snapshot

```yaml
- generic [ref=e2]:
  - generic [ref=e3]:
    - link "Sonora" [ref=e4] [cursor=pointer]:
      - /url: /
      - img [ref=e5]: music_note
      - generic [ref=e6]: Sonora
    - navigation [ref=e7]:
      - link "Catalogue" [ref=e8] [cursor=pointer]:
        - /url: /catalogue
        - img [ref=e9]: library_music
        - generic [ref=e10]: Catalogue
      - link "Playlists" [ref=e13] [cursor=pointer]:
        - /url: /playlists
        - img [ref=e14]: queue_music
        - generic [ref=e15]: Playlists
      - link "Favoris" [ref=e18] [cursor=pointer]:
        - /url: /favorites
        - img [ref=e19]: favorite
        - generic [ref=e20]: Favoris
      - link [ref=e23] [cursor=pointer]:
        - /url: /upload
        - img [ref=e24]: cloud_upload
    - button [ref=e27] [cursor=pointer]:
      - img [ref=e28]: account_circle
  - main [ref=e31]:
    - generic [ref=e33]:
      - generic [ref=e34]:
        - heading "Catalogue Musical" [level=1] [ref=e35]
        - paragraph [ref=e36]: 2 titre(s) disponible(s)
      - generic [ref=e37]:
        - generic [ref=e40]:
          - generic [ref=e41]: Rechercher...
          - textbox "Rechercher..." [ref=e43]:
            - /placeholder: Titre, artiste, album
          - img [ref=e45]: search
        - generic [ref=e49] [cursor=pointer]:
          - generic [ref=e50]: Genre
          - combobox "Genre" [ref=e52]:
            - generic [ref=e53]:
              - generic [ref=e55]: Tous
              - img [ref=e58]
        - generic [ref=e63] [cursor=pointer]:
          - generic [ref=e64]: Mood
          - combobox "Mood" [ref=e66]:
            - generic [ref=e67]:
              - generic [ref=e69]: Tous
              - img [ref=e72]
        - button "Réinitialiser" [ref=e75]:
          - img [ref=e76]: clear
          - generic [ref=e77]: Réinitialiser
      - generic [ref=e80]:
        - generic [ref=e81] [cursor=pointer]:
          - generic [ref=e82]:
            - img [ref=e84]: music_note
            - generic [ref=e85]:
              - generic [ref=e86]: One More Time
              - generic [ref=e87]: Artiste inconnu
          - generic [ref=e89]:
            - generic [ref=e90]: ELECTRONIC
            - generic [ref=e91]: HYPE
            - generic [ref=e92]:
              - img [ref=e93]: schedule
              - text: 5:20
          - generic [ref=e94]:
            - button "Écouter" [ref=e95]:
              - img [ref=e96]: play_circle
            - button "Favoris" [ref=e99]:
              - img [ref=e100]: favorite_border
            - button "Détails" [ref=e103]:
              - img [ref=e104]: info
        - generic [ref=e107] [cursor=pointer]:
          - generic [ref=e108]:
            - img [ref=e110]: music_note
            - generic [ref=e111]:
              - generic [ref=e112]: Harder Better Faster Stronger
              - generic [ref=e113]: Artiste inconnu
          - generic [ref=e115]:
            - generic [ref=e116]: ELECTRONIC
            - generic [ref=e117]: FOCUS
            - generic [ref=e118]:
              - img [ref=e119]: schedule
              - text: 3:44
          - generic [ref=e120]:
            - button "Écouter" [ref=e121]:
              - img [ref=e122]: play_circle
            - button "Favoris" [ref=e125]:
              - img [ref=e126]: favorite_border
            - button "Détails" [ref=e129]:
              - img [ref=e130]: info
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
> 56 |     await expect(page.getByText('Daft Punk').first()).toBeVisible();
     |                                                       ^ Error: expect(locator).toBeVisible() failed
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
  71 |     await expect(page).toHaveURL('/');
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