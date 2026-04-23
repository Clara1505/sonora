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

| Méthode | Route | Auth requise | Description |
|---|---|---|---|
| POST | `/api/auth/register` | Non | Créer un compte |
| POST | `/api/auth/login` | Non | Obtenir un token JWT |
| GET | `/api/tracks` | Oui | Lister les pistes |
| POST | `/api/tracks` | Oui (ADMIN) | Créer une piste |
| GET | `/api/tracks/{id}` | Oui | Détail d'une piste |
| GET | `/api/albums` | Oui | Lister les albums |
| GET | `/api/artists` | Oui | Lister les artistes |
| GET | `/api/playlists` | Oui | Mes playlists |
| POST | `/api/playlists` | Oui | Créer une playlist |
| GET | `/api/favorites` | Oui | Mes favoris |
| POST | `/api/favorites` | Oui | Ajouter aux favoris |
| GET | `/api/stats/me` | Oui | Mes statistiques d'écoute |
| GET | `/api/streaming/{id}` | Oui | Streamer une piste (Range) |
| POST | `/api/streaming/played` | Oui | Enregistrer une écoute |
| POST | `/api/upload` | Oui (ADMIN) | Upload d'un fichier audio |
| GET | `/api/spotify/search` | Oui | Recherche Spotify |

> La documentation complète et interactive est disponible via Swagger UI quand `SWAGGER_ENABLED=true`.

### Arrêt et nettoyage

\`\`\`bash
# Arrêter les conteneurs
docker compose down

# Arrêter ET supprimer les volumes (base de données, données Kafka)
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

> Note : le passage en rôle ARTISTE est actuellement effectué par un administrateur.

### Profil Administrateur

Tous les droits, plus :
- Créer, modifier, supprimer des pistes, albums et artistes
- Gérer les comptes utilisateurs
- Consulter les statistiques globales de la plateforme

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
│   │   │   └── service/      # Logique métier
│   │   └── resources/
│   │       ├── application.properties        # Config production (variables env)
│   │       └── application-dev.properties    # Config développement local
│   └── src/test/     # Tests unitaires et intégration
│
├── frontend/         # SPA Angular 21
│   └── src/app/
│       ├── guards/       # Auth guard (routes protégées)
│       ├── interceptors/ # Injection automatique du token JWT
│       ├── layout/       # Composants persistants (navbar, player bar)
│       ├── models/       # Interfaces TypeScript
│       ├── pages/        # Composants de page (routing)
│       └── services/     # Appels API
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