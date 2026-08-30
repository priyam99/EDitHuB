package com.edithub.media.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CompleteUploadRequest {

    @NotNull(message = "Media ID is required")
    private UUID mediaId;

    private String checksum;
}
