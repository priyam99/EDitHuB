-- Media files table
CREATE TABLE media_files (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id    UUID         NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    uploaded_by   UUID         NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    file_name     VARCHAR(255) NOT NULL,
    storage_key   VARCHAR(500) NOT NULL UNIQUE,
    file_type     VARCHAR(20)  NOT NULL DEFAULT 'VIDEO',
    mime_type     VARCHAR(100) NOT NULL,
    file_size     BIGINT       NOT NULL,
    duration      DOUBLE PRECISION,
    width         INTEGER,
    height        INTEGER,
    thumbnail_key VARCHAR(500),
    checksum      VARCHAR(128),
    status        VARCHAR(20)  NOT NULL DEFAULT 'UPLOADING',
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_media_file_type CHECK (file_type IN ('VIDEO', 'AUDIO', 'IMAGE', 'DOCUMENT', 'OTHER')),
    CONSTRAINT chk_media_status CHECK (status IN ('UPLOADING', 'PROCESSING', 'READY', 'FAILED'))
);

CREATE INDEX idx_media_files_project ON media_files (project_id);
CREATE INDEX idx_media_files_uploader ON media_files (uploaded_by);
CREATE INDEX idx_media_files_status ON media_files (status);
