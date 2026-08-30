package com.edithub.security;

import com.edithub.media.dto.UploadUrlRequest;
import com.edithub.media.model.MediaFileType;
import com.edithub.media.repository.MediaFileRepository;
import com.edithub.media.service.MediaService;
import com.edithub.media.service.S3StorageService;
import com.edithub.project.model.Project;
import com.edithub.project.model.ProjectVisibility;
import com.edithub.project.repository.ProjectRepository;
import com.edithub.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityAuthorizationTest {

    @Mock
    private MediaFileRepository mediaFileRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private S3StorageService storageService;

    @InjectMocks
    private MediaService mediaService;

    private User owner;
    private User unauthorizedUser;
    private Project privateProject;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .username("creator")
                .email("creator@edithub.com")
                .build();
        owner.setId(UUID.randomUUID());

        unauthorizedUser = User.builder()
                .username("attacker")
                .email("attacker@edithub.com")
                .build();
        unauthorizedUser.setId(UUID.randomUUID());

        privateProject = Project.builder()
                .title("Private Confidential Footage")
                .owner(owner)
                .visibility(ProjectVisibility.PRIVATE)
                .build();
        privateProject.setId(UUID.randomUUID());
    }

    @Test
    @DisplayName("Security: Unauthorized user cannot access private project media")
    void testPrivateProjectMediaAccessDenied() {
        when(projectRepository.findById(privateProject.getId())).thenReturn(Optional.of(privateProject));

        assertThrows(SecurityException.class, () -> {
            mediaService.getProjectMediaFiles(privateProject.getId(), unauthorizedUser);
        });

        assertThrows(SecurityException.class, () -> {
            mediaService.getProjectMediaFiles(privateProject.getId(), null);
        });
    }

    @Test
    @DisplayName("Security: Owner can access private project media")
    void testPrivateProjectOwnerAccessAllowed() {
        when(projectRepository.findById(privateProject.getId())).thenReturn(Optional.of(privateProject));

        assertDoesNotThrow(() -> {
            mediaService.getProjectMediaFiles(privateProject.getId(), owner);
        });
    }

    @Test
    @DisplayName("Security: Reject upload requests with executable or malicious file extensions")
    void testRejectMaliciousFileUploadExtensions() {
        when(projectRepository.findById(privateProject.getId())).thenReturn(Optional.of(privateProject));

        UploadUrlRequest exeRequest = new UploadUrlRequest();
        exeRequest.setFileName("malicious.exe");
        exeRequest.setMimeType("application/x-msdownload");
        exeRequest.setFileSize(1024L);

        assertThrows(IllegalArgumentException.class, () -> {
            mediaService.requestUploadUrl(privateProject.getId(), owner, exeRequest);
        });

        UploadUrlRequest phpRequest = new UploadUrlRequest();
        phpRequest.setFileName("webshell.php");
        phpRequest.setMimeType("application/x-httpd-php");
        phpRequest.setFileSize(1024L);

        assertThrows(IllegalArgumentException.class, () -> {
            mediaService.requestUploadUrl(privateProject.getId(), owner, phpRequest);
        });

        UploadUrlRequest shRequest = new UploadUrlRequest();
        shRequest.setFileName("script.sh");
        shRequest.setMimeType("text/x-shellscript");
        shRequest.setFileSize(1024L);

        assertThrows(IllegalArgumentException.class, () -> {
            mediaService.requestUploadUrl(privateProject.getId(), owner, shRequest);
        });
    }

    @Test
    @DisplayName("Security: Accept valid video formats for upload URL generation")
    void testAcceptValidVideoExtensions() {
        when(projectRepository.findById(privateProject.getId())).thenReturn(Optional.of(privateProject));
        when(mediaFileRepository.save(any())).thenAnswer(i -> {
            com.edithub.media.model.MediaFile mf = i.getArgument(0);
            mf.setId(UUID.randomUUID());
            return mf;
        });

        UploadUrlRequest validRequest = new UploadUrlRequest();
        validRequest.setFileName("raw_footage_4k.mp4");
        validRequest.setMimeType("video/mp4");
        validRequest.setFileSize(5000000L);
        validRequest.setFileType(MediaFileType.VIDEO);

        assertDoesNotThrow(() -> {
            mediaService.requestUploadUrl(privateProject.getId(), owner, validRequest);
        });
    }
}
