-- Skills reference table
CREATE TABLE skills (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(100) NOT NULL UNIQUE,
    category    VARCHAR(50)  NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- User skills junction table
CREATE TABLE user_skills (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id          UUID        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    skill_id         UUID        NOT NULL REFERENCES skills(id) ON DELETE CASCADE,
    experience_level VARCHAR(20) NOT NULL DEFAULT 'INTERMEDIATE',

    CONSTRAINT uk_user_skill UNIQUE (user_id, skill_id),
    CONSTRAINT chk_experience CHECK (experience_level IN ('BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'EXPERT'))
);

CREATE INDEX idx_user_skills_user ON user_skills (user_id);
CREATE INDEX idx_user_skills_skill ON user_skills (skill_id);

-- User software preferences table
CREATE TABLE user_software (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID         NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name        VARCHAR(100) NOT NULL,
    proficiency VARCHAR(20) NOT NULL DEFAULT 'INTERMEDIATE',

    CONSTRAINT uk_user_software UNIQUE (user_id, name)
);

CREATE INDEX idx_user_software_user ON user_software (user_id);

-- Seed predefined skills
INSERT INTO skills (name, category) VALUES
    ('Color Grading', 'COLOR'),
    ('Color Correction', 'COLOR'),
    ('Sound Design', 'AUDIO'),
    ('Audio Mixing', 'AUDIO'),
    ('Motion Graphics', 'MOTION'),
    ('Visual Effects', 'VFX'),
    ('Subtitles', 'EDITING'),
    ('Transitions', 'EDITING'),
    ('Video Editing', 'EDITING'),
    ('Compositing', 'VFX'),
    ('3D Animation', 'MOTION'),
    ('Typography', 'MOTION'),
    ('Storytelling', 'EDITING'),
    ('Pacing', 'EDITING'),
    ('Thumbnail Design', 'OTHER'),
    ('Green Screen', 'VFX'),
    ('Rotoscoping', 'VFX'),
    ('Noise Reduction', 'AUDIO'),
    ('Voiceover', 'AUDIO'),
    ('Music Selection', 'AUDIO');
