package com.resolum.intiva.platform.communications.infrastructure.providers.fcm.configuration;

/*
 * This class is used to hold the settings for Firebase Messaging, such as the project ID and the path to the credentials file.
 * It is used by the FirebaseConfiguration class to initialize the FirebaseApp instance.
 */
public record FirebaseSettings (
    String projectId,
    String credentialsPath,
    boolean enabled
) {

}
