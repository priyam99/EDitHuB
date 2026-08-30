-- Versions table
CREATE TABLE versions (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id        UUID         NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    editor_id         UUID         NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    parent_version_id UUID         REFERENCES versions(id) ON DELETE SET NULL,
    version_number    INTEGER      NOT NULL DEFAULT 1,
    title             VARCHAR(200) NOT NULL,
    description       TEXT         NOT NULL,
    preview_key       VARCHAR(500),
    source_file_key   VARCHAR(500),
    software_used     VARCHAR(100),
    changes           TEXT,
    status            VARCHAR(20)  NOT NULL DEFAULT 'DRAFT',
    created_at        TIMESTAMP    NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_version_status CHECK (status IN ('DRAFT', 'SUBMITTED', 'UNDER_REVIEW', 'ACCEPTED', 'REJECTED'))
);

CREATE INDEX idx_versions_project ON versions (project_id);
CREATE INDEX idx_versions_editor ON versions (editor_id);
CREATE INDEX idx_versions_parent ON versions (parent_version_id);
