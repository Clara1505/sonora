# Sonora 🎵

Plateforme de streaming et de référencement musical.  
Stack : **Spring Boot 3.5 · Angular 21 · PostgreSQL · Kafka (KRaft) · Docker**

---

## Table des matières

- [Documentation d'exploitation](#documentation-dexploitation)
  - [Prérequis](#prérequis)
  - [Démarrage rapide (Docker Compose)](#démarrage-rapide-docker-compose)
  - [Variables d'environnement](#variables-denvironnement)
  - [Endpoints accessibles](#endpoints-accessibles)
  - [Arrêt et nettoyage](#arrêt-et-nettoyage)
- [Documentation utilisateur](#documentation-utilisateur)
  - [Profil Auditeur](#profil-auditeur)
  - [Profil Artiste](#profil-artiste)
  - [Profil Administrateur](#profil-administrateur)
- [Documentation développeur](#documentation-développeur)
  - [Structure du monorepo](#structure-du-monorepo)
  - [Architecture backend](#architecture-backend)
  - [Démarrage en mode développement](#démarrage-en-mode-développement)
  - [Tests](#tests)
  - [CI/CD](#cicd)

---

## Documentation d'exploitation

### Prérequis

| Outil | Version minimale |
|---|---|
| Docker | 24+ |
| Docker Compose | 2.20+ |
| Java (dev local uniquement) | 21 |
| Node.js (dev local uniquement) | 20 |

### Démarrage rapide (Docker Compose)

\`\`\`bash
# 1. Cloner le dépôt
git clone https://github.com/<org>/sonora.git
cd sonora

# 2. Créer le fichier de configuration à partir du modèle
cp .env.example .env
# Éditer .env et renseigner au minimum : DB_PASSWORD, JWT_SECRET

# 3. Démarrer toute l'infrastructure
docker compose up -d

# 4. Vérifier que tous les services sont sains
docker compose ps
\`\`\`

Le backend met ~30 s à démarrer le temps que PostgreSQL et Kafka soient prêts.

### Variables d'environnement

Toutes les variables sont décrites dans [`.env.example`](.env.example).  
Les variables **obligatoires** (sans valeur par défaut) sont :

| Variable | Description |
|---|---|
| `DB_PASSWORD` | Mot de passe PostgreSQL |
| `JWT_SECRET` | Clé de signature JWT (min. 32 caractères) |

Les variables **optionnelles** (valeur par défaut utilisée si absentes) sont :

| Variable | Défaut | Description |
|---|---|---|
| `ADMIN_EMAIL` | `admin@sonora.com` | Email du compte administrateur créé au démarrage |
| `ADMIN_PASSWORD` | `Admin1234!` | Mot de passe du compte administrateur |

Pour générer un secret JWT sécurisé :
\`\`\`bash
openssl rand -base64 32
\`\`\`

### Endpoints accessibles

| Service | URL locale | Description |
|---|---|---|
| Backend API | `http://localhost:8080` | API REST Spring Boot |
| Swagger UI | `http://localhost:8080/swagger-ui.html` | Documentation interactive (dev uniquement) |
| Frontend | `http://localhost:4200` | Application Angular |
| Redpanda Console | `http://localhost:8082` | Interface Kafka |
| pgAdmin 4 | `http://localhost:5050` | Interface PostgreSQL |

#### Résumé des routes API

**Authentification**

| Méthode | Route | Auth | Description |
|---|---|---|---|
| POST | `/api/auth/register` | Non | Créer un compte (rôle USER) |
| POST | `/api/auth/login` | Non | Obtenir un token JWT |

**Catalogue**

| Méthode | Route | Auth | Description |
|---|---|---|---|
| GET | `/api/tracks` | Oui | Lister toutes les pistes |
| POST | `/api/tracks` | ADMIN | Créer une piste (métadonnées) |
| GET | `/api/tracks/{id}` | Oui | Détail d'une piste |
| PUT | `/api/tracks/{id}` | ADMIN | Modifier une piste |
| DELETE | `/api/tracks/{id}` | Oui (propriétaire ou ADMIN) | Supprimer une piste |
| POST | `/api/tracks/upload` | Oui | Uploader un fichier audio (MP3/WAV, max 100 Mo) |
| GET | `/api/tracks/{id}/stream` | Non | Streamer une piste (support Range) |
| POST | `/api/tracks/{id}/played` | Oui | Enregistrer une écoute |
| GET | `/api/albums` | Oui | Lister les albums |
| GET | `/api/artists` | Oui | Lister les artistes |

**Playlists & Favoris**

| Méthode | Route | Auth | Description |
|---|---|---|---|
| GET | `/api/playlists` | Oui | Mes playlists |
| POST | `/api/playlists` | Oui | Créer une playlist |
| GET | `/api/playlists/{id}` | Oui | Détail d'une playlist |
| DELETE | `/api/playlists/{id}` | Oui (propriétaire ou ADMIN) | Supprimer une playlist |
| POST | `/api/playlists/{id}/tracks/{trackId}` | Oui | Ajouter une piste à une playlist |
| DELETE | `/api/playlists/{id}/tracks/{trackId}` | Oui | Retirer une piste d'une playlist |
| GET | `/api/playlists/all` | ADMIN | Toutes les playlists (tous utilisateurs) |
| GET | `/api/favorites` | Oui | Mes favoris |
| POST | `/api/favorites/{trackId}` | Oui | Ajouter aux favoris |
| DELETE | `/api/favorites/{trackId}` | Oui | Retirer des favoris |

**Statistiques**

| Méthode | Route | Auth | Description |
|---|---|---|---|
| GET | `/api/stats/me` | Oui | Mes statistiques d'écoute |
| GET | `/api/stats/tracks/{id}` | Oui | Statistiques d'une piste |

**Administration (ADMIN uniquement)**

| Méthode | Route | Auth | Description |
|---|---|---|---|
| GET | `/api/admin/users` | ADMIN | Lister tous les utilisateurs (rôle USER) |
| DELETE | `/api/admin/users/{id}` | ADMIN | Supprimer un utilisateur |
| GET | `/api/admin/stats` | ADMIN | Statistiques globales (users, pistes, playlists) |

**Divers**

| Méthode | Route | Auth | Description |
|---|---|---|---|
| GET | `/api/spotify/search` | Oui | Recherche Spotify |
| GET | `/actuator/health` | Non | Santé du service |

> La documentation complète et interactive est disponible via Swagger UI quand `SWAGGER_ENABLED=true`.

### Arrêt et nettoyage


# Arrêter les conteneurs
\`\`\`bash
docker compose down
\`\`\`

# Arrêter ET supprimer les volumes (base de données, données Kafka)
\`\`\`bash
docker compose down -v
\`\`\`

---

## Documentation utilisateur

### Profil Auditeur

Compte standard créé à l'inscription (`/register`).

**Fonctionnalités disponibles :**
- Parcourir le catalogue de pistes, albums et artistes
- Écouter une piste via le lecteur intégré (barre de lecture en bas)
- Ajouter/retirer des pistes de ses favoris
- Créer et gérer ses playlists personnelles
- Ajouter des pistes à une playlist existante
- Consulter ses statistiques d'écoute (pistes les plus écoutées, historique)
- Rechercher des artistes via l'intégration Spotify

### Profil Artiste

Même droits que l'Auditeur, plus :
- Accès à la page de gestion de son catalogue
- Upload de fichiers audio (MP3 / WAV, max 100 Mo)

### Profil Administrateur

Compte avec le rôle `ADMIN`. **Un administrateur est créé automatiquement au premier démarrage** (voir [Compte administrateur](#compte-administrateur)).

Fonctionnalités supplémentaires par rapport à l'Auditeur :

| Fonctionnalité | Détail |
|---|---|
| **Catalogue — suppression** | Bouton "Supprimer" visible sur chaque piste du catalogue |
| **Playlists — vue globale** | L'onglet Playlists affiche toutes les playlists de tous les utilisateurs |
| **Playlists — suppression** | Peut supprimer n'importe quelle playlist, même celle d'un autre utilisateur |
| **Utilisateurs** | L'onglet "Favoris" est remplacé par "Utilisateurs" : liste tous les comptes USER avec leur date d'inscription et un bouton de suppression |
| **Profil — stats globales** | Le profil affiche un bloc "Statistiques de l'application" : nombre d'utilisateurs, de pistes et de playlists, avec un lien vers la page de gestion |

---

### Compte administrateur

#### Identifiants par défaut

Un compte administrateur est **automatiquement créé au démarrage du backend** (`DataInitializer`) s'il n'existe pas déjà :

| Champ | Valeur par défaut |
|---|---|
| Email | `admin@sonora.com` |
| Mot de passe | `Admin1234!` |

> Ces valeurs sont surchargeable via les variables d'environnement `ADMIN_EMAIL` et `ADMIN_PASSWORD` dans `docker-compose.yml` ou `.env`.

#### Changer les identifiants admin

**Via Docker Compose (recommandé) :**

```bash
# Dans .env ou en ligne de commande
ADMIN_EMAIL=mon-admin@exemple.com ADMIN_PASSWORD=MonMdpSécurisé! docker compose up -d
```

**Via le script fourni :**

```bash
# Crée (ou recrée) l'admin avec les credentials donnés et redémarre le backend
./scripts/register_admin.sh mon-admin@exemple.com MonMdpSécurisé!
```

Le script [`scripts/register_admin.sh`](scripts/register_admin.sh) exporte les variables et redémarre le conteneur `sonora-backend` via Docker Compose. Si Docker Compose n'est pas disponible, il affiche la commande Maven équivalente.

#### Créer un second administrateur

La création d'un deuxième admin se fait directement via `psql` :

```bash
# Obtenir le hash BCrypt du mot de passe (Python 3 requis)
HASH=$(python3 -c "import bcrypt; print(bcrypt.hashpw(b'NouveauMdp!', bcrypt.gensalt(10)).decode())")

# Insérer dans la base
docker compose exec sonora-db psql -U postgres -d sonora -c \
  "INSERT INTO users (email, password_hash, role, created_at) \
   VALUES ('autre-admin@exemple.com', '$HASH', 'ADMIN', NOW());"
```

---

## Documentation développeur

### Structure du monorepo

\`\`\`
sonora/
├── backend/          # API Spring Boot (Java 21)
│   ├── src/main/
│   │   ├── java/com/sonora/sonora/
│   │   │   ├── config/       # Configuration Spring (OpenAPI, Security)
│   │   │   ├── controller/   # Couche REST (entrée HTTP)
│   │   │   ├── dto/          # Data Transfer Objects (records Java)
│   │   │   ├── exception/    # Gestion globale des erreurs
│   │   │   ├── kafka/        # Producteurs et consommateurs Kafka
│   │   │   ├── model/        # Entités JPA + enums métier
│   │   │   ├── repository/   # Interfaces Spring Data JPA
│   │   │   ├── security/     # JWT, filtres, SecurityConfig
│   │   │   ├── service/      # Logique métier
│   │   │   └── DataInitializer       # Création du compte admin au démarrage
│   │   └── resources/
│   │       ├── application.properties        # Config production (variables env)
│   │       └── application-dev.properties    # Config développement local
│   └── src/test/     # Tests unitaires et intégration
│
├── frontend/         # SPA Angular 21
│   └── src/app/
│       ├── guards/       # authGuard (routes protégées) · adminGuard (routes ADMIN)
│       ├── interceptors/ # Injection automatique du token JWT
│       ├── layout/       # Composants persistants (navbar, player bar)
│       ├── models/       # Interfaces TypeScript
│       ├── pages/        # Composants de page (routing)
│       └── services/     # Appels API (dont AdminService)
│
├── docker-compose.yml  # Infrastructure complète
├── .env.example        # Modèle des variables d'environnement
└── .github/workflows/  # Pipelines CI/CD
\`\`\`

### Architecture backend

L'architecture suit le pattern **MVC en couches** (Controller → Service → Repository) avec :

- **DTOs** : tous les échanges HTTP passent par des records Java immuables, jamais par les entités JPA directement
- **GlobalExceptionHandler** : toutes les exceptions métier sont centralisées et retournent un `ApiErrorResponse` structuré
- **Kafka** : les événements d'écoute (`track-played`) et d'import (`track-imported`) transitent par des topics Kafka dédiés, desserrant le couplage entre le streaming et la persistance des stats
- **JWT** : authentification stateless via JJWT 0.12, filtre appliqué sur toutes les routes sauf `/api/auth/**`

### Démarrage en mode développement

**Backend seul (IntelliJ / ligne de commande) :**
\`\`\`bash
# Démarrer uniquement les dépendances infra
docker compose up -d sonora-db kafka-0 kafka-1 kafka-2

cd backend
export $(cat ../.env | xargs)
export SPRING_PROFILES_ACTIVE=dev
./mvnw spring-boot:run
\`\`\`

**Frontend seul :**
\`\`\`bash
cd frontend
npm install
npm start
# Accessible sur http://localhost:4200
\`\`\`

### Tests

\`\`\`bash
# Backend — tous les tests + rapport JaCoCo
cd backend
./mvnw verify

# Le rapport de couverture est généré dans :
# backend/target/site/jacoco/index.html

# Frontend — tests unitaires (Vitest)
cd frontend
npm test
\`\`\`

La couverture backend doit rester au-dessus de **80 %** (enforced en CI).

### CI/CD

Le pipeline GitHub Actions (`.github/workflows/ci.yml`) suit cette logique :

| Événement | Jobs exécutés |
|---|---|
| Push / PR sur n'importe quelle branche | Build + Tests backend, Build + Lint + Tests frontend |
| Push sur `dev` ou `main` | + Build & Push image Docker sur GHCR |
| Push sur `main` uniquement | + Création de la GitHub Release |

Les secrets GitHub Actions requis :
- `SPOTIFY_CLIENT_ID` / `SPOTIFY_CLIENT_SECRET`
- `DB_PASSWORD` et `JWT_SECRET` (si déploiement réel)
