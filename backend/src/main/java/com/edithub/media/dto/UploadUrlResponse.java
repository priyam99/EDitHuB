package com.edithub.media.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadUrlResponse {

    private UUID mediaId;
    private String uploadUrl;
    private String storageKey;
    private String bucket;
}
