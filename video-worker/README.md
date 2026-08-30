# Video Worker

FFmpeg-based video processing service for EditHub.

## Status

Not yet implemented. This service will handle:

- Video metadata extraction (duration, resolution, codec)
- Thumbnail generation
- Low-resolution preview generation
- Audio waveform extraction

## Technology

- Python or Java (TBD — see [Open Decision #4](../docs/product-requirements.md))
- FFmpeg
- Redis (job queue consumer)

## Setup

```bash
# Will be implemented in Phase 3 (Media Upload)
docker compose up video-worker
```
