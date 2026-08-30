-- Comments table
CREATE TABLE comments (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id        UUID      NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    submission_id     UUID      REFERENCES submissions(id) ON DELETE CASCADE,
    author_id         UUID      NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    parent_comment_id UUID      REFERENCES comments(id) ON DELETE CASCADE,
    content           TEXT      NOT NULL,
    timestamp_seconds DOUBLE PRECISION CHECK (timestamp_seconds >= 0),
    created_at        TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_comments_project ON comments (project_id);
CREATE INDEX idx_comments_submission ON comments (submission_id);
CREATE INDEX idx_comments_author ON comments (author_id);
