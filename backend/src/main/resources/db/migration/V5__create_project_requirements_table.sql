-- Project requirements table
CREATE TABLE project_requirements (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id  UUID    NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    skill_id    UUID    NOT NULL REFERENCES skills(id) ON DELETE CASCADE,
    is_required BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT uk_project_skill UNIQUE (project_id, skill_id)
);

CREATE INDEX idx_project_req_project ON project_requirements (project_id);
CREATE INDEX idx_project_req_skill ON project_requirements (skill_id);
