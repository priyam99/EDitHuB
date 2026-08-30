-- Reviews table
CREATE TABLE reviews (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    submission_id UUID         NOT NULL REFERENCES submissions(id) ON DELETE CASCADE,
    reviewer_id   UUID         NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    rating        INTEGER      CHECK (rating BETWEEN 1 AND 5),
    feedback      TEXT         NOT NULL,
    decision      VARCHAR(20)  NOT NULL,
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_review_decision CHECK (decision IN ('ACCEPT', 'REQUEST_CHANGES', 'REJECT'))
);

CREATE INDEX idx_reviews_submission ON reviews (submission_id);
CREATE INDEX idx_reviews_reviewer ON reviews (reviewer_id);
