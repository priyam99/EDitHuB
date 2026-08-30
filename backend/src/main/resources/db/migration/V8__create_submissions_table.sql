-- Submissions table
CREATE TABLE submissions (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id  UUID         NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    version_id  UUID         NOT NULL REFERENCES versions(id) ON DELETE CASCADE,
    editor_id   UUID         NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title       VARCHAR(200) NOT NULL,
    description TEXT         NOT NULL,
    status      VARCHAR(20)  NOT NULL DEFAULT 'SUBMITTED',
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_submission_status CHECK (status IN ('DRAFT', 'SUBMITTED', 'UNDER_REVIEW', 'CHANGES_REQUESTED', 'ACCEPTED', 'REJECTED', 'WITHDRAWN', 'CLOSED'))
);

CREATE INDEX idx_submissions_project ON submissions (project_id);
CREATE INDEX idx_submissions_version ON submissions (version_id);
CREATE INDEX idx_submissions_editor ON submissions (editor_id);
CREATE INDEX idx_submissions_status ON submissions (status);
