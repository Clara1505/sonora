-- ============================================================
-- V1 — Schéma initial Sonora
-- Généré à partir des entités JPA — ne pas modifier manuellement.
-- Pour toute évolution du schéma, créer un nouveau fichier V2__...
-- ============================================================

-- ── Users ────────────────────────────────────────────────────
CREATE TABLE users (
    id            BIGSERIAL    PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(20)  NOT NULL,
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ── Artists ──────────────────────────────────────────────────
CREATE TABLE artists (
    id        BIGSERIAL    PRIMARY KEY,
    name      VARCHAR(255) NOT NULL,
    bio       TEXT,
    image_url VARCHAR(512)
);

-- ── Albums ───────────────────────────────────────────────────
CREATE TABLE albums (
    id           BIGSERIAL    PRIMARY KEY,
    title        VARCHAR(255) NOT NULL,
    release_year INTEGER,
    cover_url    VARCHAR(512),
    artist_id    BIGINT       NOT NULL REFERENCES artists(id)
);

-- ── Tracks ───────────────────────────────────────────────────
CREATE TABLE tracks (
    id        BIGSERIAL    PRIMARY KEY,
    title     VARCHAR(255) NOT NULL,
    duration  INTEGER,
    genre     VARCHAR(50),
    mood      VARCHAR(50),
    file_path VARCHAR(512),
    album_id  BIGINT REFERENCES albums(id),
    artist_id BIGINT NOT NULL REFERENCES artists(id)
);

-- ── Playlists ────────────────────────────────────────────────
CREATE TABLE playlists (
    id         BIGSERIAL    PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    type       VARCHAR(20)  NOT NULL,
    image_url  VARCHAR(512),
    created_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    user_id    BIGINT       NOT NULL REFERENCES users(id)
);

-- ── Playlist ↔ Tracks (join table) ──────────────────────────
CREATE TABLE playlist_tracks (
    playlist_id BIGINT NOT NULL REFERENCES playlists(id),
    track_id    BIGINT NOT NULL REFERENCES tracks(id),
    PRIMARY KEY (playlist_id, track_id)
);

-- ── Favorites ────────────────────────────────────────────────
CREATE TABLE favorites (
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT    NOT NULL REFERENCES users(id),
    track_id   BIGINT    NOT NULL REFERENCES tracks(id),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_favorites UNIQUE (user_id, track_id)
);

-- ── Play history ─────────────────────────────────────────────
CREATE TABLE play_history (
    id                BIGSERIAL PRIMARY KEY,
    user_id           BIGINT    NOT NULL REFERENCES users(id),
    track_id          BIGINT    NOT NULL REFERENCES tracks(id),
    played_at         TIMESTAMP NOT NULL DEFAULT NOW(),
    duration_listened INTEGER
);

-- ── Index pour les requêtes fréquentes ───────────────────────
CREATE INDEX idx_tracks_artist    ON tracks(artist_id);
CREATE INDEX idx_tracks_album     ON tracks(album_id);
CREATE INDEX idx_albums_artist    ON albums(artist_id);
CREATE INDEX idx_playlists_user   ON playlists(user_id);
CREATE INDEX idx_favorites_user   ON favorites(user_id);
CREATE INDEX idx_play_history_user ON play_history(user_id);
CREATE INDEX idx_play_history_track ON play_history(track_id);