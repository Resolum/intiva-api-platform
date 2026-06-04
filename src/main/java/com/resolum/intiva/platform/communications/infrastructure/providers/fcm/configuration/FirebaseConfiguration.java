package com.resolum.intiva.platform.communications.infrastructure.providers.fcm.configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
public class FirebaseConfiguration {

    @Value("${firebase.project.id:}")
    private String projectId;

    @Value("${firebase.credentials.path:}")
    private String credentialsPath;

    @Value("${integrations.fcm.enabled:false}")
    private boolean enabled;

    @Bean
    FirebaseSettings firebaseMessagingSettings() {
        return new FirebaseSettings(projectId, credentialsPath, enabled);
    }

    @Bean
    FirebaseApp firebaseApp(FirebaseSettings settings) throws IOException {
        if (!settings.enabled()) {
            throw new IllegalStateException("FirebaseApp should not be created when FCM integration is disabled.");
        }

        validateCredentialsPath(settings.credentialsPath());

        if (FirebaseApp.getApps().isEmpty()) {
            try (var serviceAccount = new FileInputStream(settings.credentialsPath())) {
                var options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .setProjectId(settings.projectId())
                        .build();

                return FirebaseApp.initializeApp(options);
            }
        }
        return FirebaseApp.getInstance();
    }

    /**
     * Validates that the configured Firebase credentials path points to one concrete existing file.
     *
     * @param credentialsPath configured credentials path
     */
    private void validateCredentialsPath(String credentialsPath) {
        if (credentialsPath == null || credentialsPath.isBlank()) {
            throw new IllegalStateException("firebase.credentials.path is required when integrations.fcm.enabled=true.");
        }
        if (credentialsPath.contains("*") || credentialsPath.contains("?")) {
            throw new IllegalStateException(
                    "firebase.credentials.path must point to one concrete JSON file. Wildcards are not supported: " + credentialsPath
            );
        }

        var path = Path.of(credentialsPath);
        if (!Files.exists(path) || !Files.isRegularFile(path)) {
            throw new IllegalStateException(
                    "firebase.credentials.path must point to an existing JSON file. Current value: " + credentialsPath
            );
        }
    }
}
