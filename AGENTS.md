# EditHub Agent Rules

You are working on EditHub, a GitHub-like collaboration platform for video editing.

## Architecture

- Use a modular monolith for the backend.
- Do not introduce microservices without explicit justification.
- Keep domain logic inside services/domain modules.
- Controllers should remain thin.
- Do not expose JPA entities directly through APIs.
- Use DTOs.
- Keep infrastructure concerns separated from business logic.

## Backend

Technology:

- Java
- Spring Boot
- PostgreSQL
- Spring Security
- Maven

Follow:

Controller
→ Service
→ Repository

Use transactions intentionally.

Validate input at API boundaries.

Never expose passwords, secrets or internal security information.

## Files

Never store video binaries in PostgreSQL.

Use object storage.

Large files should be uploaded directly from clients using pre-signed URLs.

The backend should manage authorization and metadata.

## Video Processing

Video processing must be asynchronous.

Never run expensive FFmpeg operations inside an HTTP request.

Processing must be retryable and idempotent.

## Security

Always verify ownership/authorization on the server.

Never trust IDs supplied by the client.

Protect against IDOR.

Never log passwords, tokens or signed URLs.

Never hardcode secrets.

## API

Use:

/api/v1/

Use consistent HTTP status codes.

Return predictable error responses.

Use pagination for collections.

## Database

Use migrations.

Do not manually modify production schema.

Add indexes only when justified.

Use foreign keys and constraints where appropriate.

## Frontend

Use:

Next.js
TypeScript
Tailwind

Create reusable components.

Keep API calls inside a dedicated API/client layer.

Handle:

loading
error
empty
success

states explicitly.

## Testing

Every new business feature should include tests.

Prioritize:

authorization
state transitions
file access
project ownership
submission workflow

## Git

Use small commits.

Commit messages should describe the change.

Do not mix unrelated features.

## Before finishing a task

Always:

1. Inspect existing implementation.
2. Understand existing architecture.
3. Implement the smallest clean solution.
4. Run tests.
5. Run build.
6. Fix failures.
7. Review security implications.
8. Explain what changed.

Do not rewrite unrelated code.

Do not add dependencies unless necessary.

Do not implement future features unless explicitly requested.

If requirements are ambiguous, document the assumption before making a major architectural decision.
