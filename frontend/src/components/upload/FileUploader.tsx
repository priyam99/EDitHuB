'use client';

import React, { useState } from 'react';
import { api } from '@/lib/api';
import { MediaFileDto } from '@/lib/types';

interface FileUploaderProps {
  projectId: string;
  onUploadSuccess: (mediaFile: MediaFileDto) => void;
}

export function FileUploader({ projectId, onUploadSuccess }: FileUploaderProps) {
  const [uploading, setUploading] = useState(false);
  const [progress, setProgress] = useState(0);
  const [error, setError] = useState<string | null>(null);

  const handleFileChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const files = e.target.files;
    if (!files || files.length === 0) return;

    const file = files[0];
    setError(null);
    setUploading(true);
    setProgress(10);

    try {
      // 1. Request pre-signed upload URL from backend
      const urlRes = await api.fetch<{ mediaId: string; uploadUrl: string; storageKey: string }>(
        `/projects/${projectId}/media/upload-url`,
        {
          method: 'POST',
          body: JSON.stringify({
            fileName: file.name,
            mimeType: file.type || 'video/mp4',
            fileSize: file.size,
            fileType: file.type.startsWith('audio/') ? 'AUDIO' : file.type.startsWith('image/') ? 'IMAGE' : 'VIDEO',
          }),
        }
      );

      if (!urlRes.success || !urlRes.data) {
        throw new Error(urlRes.message || 'Failed to get upload URL');
      }

      const { mediaId, uploadUrl } = urlRes.data;
      setProgress(40);

      // 2. Upload file directly to S3 / MinIO pre-signed PUT URL
      const s3Res = await fetch(uploadUrl, {
        method: 'PUT',
        headers: {
          'Content-Type': file.type || 'application/octet-stream',
        },
        body: file,
      });

      if (!s3Res.ok) {
        throw new Error(`S3 upload failed with status ${s3Res.status}`);
      }

      setProgress(80);

      // 3. Notify backend of completion
      const completeRes = await api.fetch<MediaFileDto>(
        `/projects/${projectId}/media/complete`,
        {
          method: 'POST',
          body: JSON.stringify({ mediaId }),
        }
      );

      if (completeRes.success && completeRes.data) {
        setProgress(100);
        onUploadSuccess(completeRes.data);
      }
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : 'Upload failed';
      setError(msg);
    } finally {
      setUploading(false);
      setTimeout(() => setProgress(0), 1000);
    }
  };

  return (
    <div className="space-y-4">
      <div className="border-2 border-dashed border-slate-800 hover:border-indigo-500/50 bg-slate-950 p-8 rounded-2xl text-center space-y-3 transition-colors relative">
        <input
          type="file"
          accept="video/*,audio/*,image/*"
          disabled={uploading}
          onChange={handleFileChange}
          className="absolute inset-0 opacity-0 cursor-pointer disabled:cursor-not-allowed"
        />

        <div className="text-4xl">📤</div>
        <div className="text-sm font-bold text-white">
          {uploading ? 'Uploading Footage directly to Storage...' : 'Click or Drag & Drop Footage / Assets'}
        </div>
        <p className="text-xs text-slate-500 font-mono">
          Supports MP4, MOV, WAV, PNG (Up to 10 GB via S3 Pre-signed URLs)
        </p>

        {uploading && (
          <div className="w-full bg-slate-900 rounded-full h-2 overflow-hidden border border-slate-800 mt-4 max-w-md mx-auto">
            <div
              className="bg-gradient-to-r from-indigo-500 to-purple-500 h-full transition-all duration-300"
              style={{ width: `${progress}%` }}
            />
          </div>
        )}
      </div>

      {error && (
        <div className="p-3 bg-red-500/10 border border-red-500/30 rounded-lg text-red-400 text-xs font-medium text-center">
          {error}
        </div>
      )}
    </div>
  );
}
