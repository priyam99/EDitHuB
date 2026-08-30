import os
import json
import time
import subprocess
import tempfile
import redis
import psycopg2
import boto3

REDIS_HOST = os.getenv("REDIS_HOST", "localhost")
REDIS_PORT = int(os.getenv("REDIS_PORT", 6379))
DB_HOST = os.getenv("DB_HOST", "localhost")
DB_PORT = os.getenv("DB_PORT", "5432")
DB_NAME = os.getenv("DB_NAME", "edithub")
DB_USER = os.getenv("DB_USER", "edithub")
DB_PASSWORD = os.getenv("DB_PASSWORD", "edithub_dev_password")

S3_ENDPOINT = os.getenv("S3_ENDPOINT", "http://localhost:9000")
S3_ACCESS_KEY = os.getenv("S3_ACCESS_KEY", "edithub_minio")
S3_SECRET_KEY = os.getenv("S3_SECRET_KEY", "edithub_minio_password")
S3_REGION = os.getenv("S3_REGION", "us-east-1")
BUCKET_MEDIA = os.getenv("BUCKET_MEDIA", "edithub-media")
BUCKET_THUMBNAILS = os.getenv("BUCKET_THUMBNAILS", "edithub-thumbnails")

print("[Video Worker] Initializing FFmpeg video processing worker...")

s3 = boto3.client(
    "s3",
    endpoint_url=S3_ENDPOINT,
    aws_access_key_id=S3_ACCESS_KEY,
    aws_secret_access_key=S3_SECRET_KEY,
    region_name=S3_REGION,
)

r = redis.Redis(host=REDIS_HOST, port=REDIS_PORT, db=0)

def extract_metadata(file_path):
    cmd = [
        "ffprobe",
        "-v", "quiet",
        "-print_format", "json",
        "-show_format",
        "-show_streams",
        file_path,
    ]
    res = subprocess.run(cmd, capture_output=True, text=True)
    if res.returncode != 0:
        return {}
    data = json.loads(res.stdout)
    duration = float(data.get("format", {}).get("duration", 0))
    width, height = 0, 0
    for stream in data.get("streams", []):
        if stream.get("codec_type") == "video":
            width = stream.get("width", 0)
            height = stream.get("height", 0)
            break
    return {"duration": duration, "width": width, "height": height}

def generate_thumbnail(file_path, output_jpg, timestamp=1.0):
    cmd = [
        "ffmpeg",
        "-y",
        "-ss", str(timestamp),
        "-i", file_path,
        "-vframes", "1",
        "-q:v", "2",
        "-vf", "scale=640:-1",
        output_jpg,
    ]
    res = subprocess.run(cmd, capture_output=True, text=True)
    return res.returncode == 0

def process_job(job_data):
    media_id = job_data.get("media_id")
    storage_key = job_data.get("storage_key")
    print(f"[Video Worker] Processing media: {media_id} ({storage_key})")

    with tempfile.TemporaryDirectory() as tmpdir:
        local_video = os.path.join(tmpdir, "input.mp4")
        local_thumb = os.path.join(tmpdir, "thumb.jpg")

        # 1. Download video from S3
        s3.download_file(BUCKET_MEDIA, storage_key, local_video)

        # 2. Extract metadata
        meta = extract_metadata(local_video)

        # 3. Generate thumbnail
        thumb_key = f"thumbnails/{media_id}_thumb.jpg"
        if generate_thumbnail(local_video, local_thumb, timestamp=min(1.0, meta.get("duration", 1.0) / 2)):
            s3.upload_file(BUCKET_THUMBNAILS, thumb_key, local_thumb, ExtraArgs={"ContentType": "image/jpeg"})

        # 4. Update Database
        conn = psycopg2.connect(
            host=DB_HOST, port=DB_PORT, dbname=DB_NAME, user=DB_USER, password=DB_PASSWORD
        )
        with conn.cursor() as cur:
            cur.execute(
                """
                UPDATE media_files
                SET duration = %s, width = %s, height = %s, thumbnail_key = %s, status = 'READY'
                WHERE id = %s
                """,
                (meta.get("duration"), meta.get("width"), meta.get("height"), thumb_key, media_id),
            )
            conn.commit()
        conn.close()
        print(f"[Video Worker] Successfully processed media: {media_id}")

def main():
    print("[Video Worker] Listening for jobs on Redis queue: edithub:video:queue...")
    while True:
        try:
            item = r.blpop("edithub:video:queue", timeout=5)
            if item:
                _, payload = item
                job_data = json.loads(payload.decode("utf-8"))
                process_job(job_data)
        except Exception as e:
            print(f"[Video Worker] Error processing job: {e}")
            time.sleep(2)

if __name__ == "__main__":
    main()
