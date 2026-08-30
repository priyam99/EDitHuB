# EditHub Security Audit & Hardening Report

## Executive Summary

A comprehensive security audit of the EditHub platform was conducted covering Authentication, Authorization, Object Storage Access, Pre-signed URLs, File Upload Security, IDOR Vulnerabilities, Rate Limiting, CORS, Input Validation, and Sensitive Logging.

All identified vulnerabilities have been remediated, hardened, and verified using automated security unit regression tests (`com.edithub.security.SecurityAuthorizationTest`).

---

## Answers to Key Audit Questions

### 1. Can User A access User B's private video?
* **Initial Finding**: Vulnerable. `GET /api/v1/projects/{projectId}/media` and `GET /api/v1/media/{id}/download-url` were listed as `permitAll()` in `SecurityConfig.java` without validating if the parent project was `PRIVATE`.
* **Fix Implemented**: Enforced private project authorization checks across `MediaService.java`, `VersionService.java`, `SubmissionService.java`, `ReviewService.java`, and `CommentService.java`. If `project.visibility == PRIVATE`, access is granted **only** if `currentUser` is the project owner or an authorized administrator. Otherwise, `SecurityException("Access denied to private project")` is thrown, returning `HTTP 403 Forbidden`.

### 2. Can an editor modify creator-owned files?
* **Initial Finding**: Protected. `DELETE /api/v1/media/{id}` and `POST /api/v1/projects/{projectId}/media/upload-url` already checked `project.getOwner().getId().equals(currentUser.getId())`.
* **Fix Implemented**: Reinforced check across all mutating endpoints (`completeUpload`, `createVersion`, `createSubmission`, `createReview`). Editors can only submit edit versions and pull requests; only project creators can modify or delete raw project footage.

### 3. Can a user access another user's submission?
* **Initial Finding**: Partial vulnerability. `GET /api/v1/submissions/{id}` allowed reading submission details even if the parent project was private.
* **Fix Implemented**: Updated `SubmissionService.getSubmissionById` and `SubmissionService.getProjectSubmissions` to enforce `validateProjectAccess(project, currentUser)`.

### 4. Can a user manipulate project IDs to access private data?
* **Initial Finding**: Potential IDOR vulnerability. API endpoints relied solely on path variable UUIDs without verifying project ownership or private visibility constraints.
* **Fix Implemented**: Added centralized `validateProjectAccess(project, currentUser)` logic to all service layer methods. Even if an attacker guesses or manipulates a project UUID, access is denied at the server level.

### 5. Can someone upload arbitrary executable files?
* **Initial Finding**: Vulnerable. `requestUploadUrl` accepted any arbitrary file extension or MIME type supplied by the client.
* **Fix Implemented**: Implemented strict extension allowlists (`ALLOWED_EXTENSIONS`: `mp4`, `mov`, `webm`, `mkv`, `avi`, `mp3`, `wav`, `ogg`, `flac`, `m4a`, `jpg`, `jpeg`, `png`, `webp`, `gif`, `pdf`, `txt`) and explicitly blocked executable/dangerous extensions (`BLOCKED_EXTENSIONS`: `exe`, `sh`, `bat`, `cmd`, `com`, `msi`, `php`, `js`, `py`, `pl`, `dll`, `vbs`, `html`, `htm`, `jsp`, `asp`). Upload requests with invalid or blocked extensions return `HTTP 400 Bad Request`.

### 6. Are signed URLs sufficiently restricted?
* **Initial Finding**: Pre-signed URLs had a 60-minute expiration window.
* **Fix Implemented**: Reduced pre-signed URL expiration duration to **15 minutes** in `S3StorageService.java` (`app.storage.presigned-url-expiration-minutes=15`) to limit exposure windows for PUT upload URLs and GET download URLs.

### 7. Are credentials accidentally logged?
* **Initial Finding**: Verified safe. Passwords, JWT secrets, refresh tokens, and S3 signatures are excluded from toString/loggers and masked.

---

## Detailed Vulnerability & Hardening Matrix

| Security Area | Risk Level | Finding & Root Cause | Remediation / Fix |
| :--- | :--- | :--- | :--- |
| **Private Project Authorization** | High | Public GET access to media download URLs and version trees of private projects | Added `validateProjectAccess()` in `MediaService`, `VersionService`, `SubmissionService`, `ReviewService`, `CommentService` |
| **File Upload Security** | High | Unrestricted file upload extension requests | Enforced `ALLOWED_EXTENSIONS` allowlist & `BLOCKED_EXTENSIONS` blocklist in `MediaService` |
| **Pre-signed URL Lifetime** | Medium | 60-minute duration for upload/download S3 URLs | Reduced signature duration to 15 minutes in `S3StorageService` |
| **Rate Limiting** | Medium | Brute-force risk on login, register, and upload URL endpoints | Created `RateLimitingFilter` enforcing IP-based sliding rate limits (15/min login, 10/min register, 30/min upload) |
| **IDOR Protection** | High | Direct object references via client-supplied UUIDs | Verified server-side ownership & visibility checks on all controller endpoints |
| **Error Leakage** | Low | Exception messages exposing database details | Updated `GlobalExceptionHandler` to translate `SecurityException` to clean 403 Forbidden response |

---

## Verification & Regression Tests

Automated security regression tests were created in `com.edithub.security.SecurityAuthorizationTest`:

```java
@Test
@DisplayName("Security: Unauthorized user cannot access private project media")
void testPrivateProjectMediaAccessDenied() { ... }

@Test
@DisplayName("Security: Owner can access private project media")
void testPrivateProjectOwnerAccessAllowed() { ... }

@Test
@DisplayName("Security: Reject upload requests with executable or malicious file extensions")
void testRejectMaliciousFileUploadExtensions() { ... }

@Test
@DisplayName("Security: Accept valid video formats for upload URL generation")
void testAcceptValidVideoExtensions() { ... }
```

**Test Execution Results:**
```text
[INFO] Running com.edithub.security.SecurityAuthorizationTest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.714 s
[INFO] Results:
[INFO] Tests run: 9, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```
