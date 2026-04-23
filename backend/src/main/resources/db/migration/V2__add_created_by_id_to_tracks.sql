ALTER TABLE tracks ADD COLUMN IF NOT EXISTS created_by_id BIGINT REFERENCES users(id);
CREATE INDEX IF NOT EXISTS idx_tracks_created_by ON tracks(created_by_id);
