package com.resolum.intiva.platform.shared.interfaces.rest.resource;

/**
 * MessageResource is a record that represents a message resource in the REST API. It contains a single field, message, which holds the message string to be returned in the response.
 * @param message the message string to be returned in the response
 */
public record MessageResource(String message) {
}
