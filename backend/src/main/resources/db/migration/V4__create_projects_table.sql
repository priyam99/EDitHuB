-- Projects table
CREATE TABLE projects (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    owner_id        UUID         NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title           VARCHAR(200) NOT NULL,
    description     VARCHAR(500) NOT NULL,
    brief           TEXT         NOT NULL,
    category        VARCHAR(50)  NOT NULL,
    editing_style   VARCHAR(50),
    target_platform VARCHAR(50),
    aspect_ratio    VARCHAR(20),
    target_duration VARCHAR(50),
    deadline        TIMESTAMP,
    difficulty      VARCHAR(20)  NOT NULL DEFAULT 'INTERMEDIATE',
    visibility      VARCHAR(20)  NOT NULL DEFAULT 'PUBLIC',
    status          VARCHAR(20)  NOT NULL DEFAULT 'OPEN',
    license         VARCHAR(100) DEFAULT 'Portfolio Allowed',
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP    NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_difficulty CHECK (difficulty IN ('BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'EXPERT')),
    CONSTRAINT chk_visibility CHECK (visibility IN ('PUBLIC', 'UNLISTED', 'PRIVATE')),
    CONSTRAINT chk_status CHECK (status IN ('DRAFT', 'OPEN', 'IN_PROGRESS', 'COMPLETED', 'ARCHIVED'))
);

CREATE INDEX idx_projects_owner ON projects (owner_id);
CREATE INDEX idx_projects_category ON projects (category);
CREATE INDEX idx_projects_visibility ON projects (visibility);
CREATE INDEX idx_projects_status ON projects (status);
CREATE INDEX idx_projects_created_at ON projects (created_at DESC);
