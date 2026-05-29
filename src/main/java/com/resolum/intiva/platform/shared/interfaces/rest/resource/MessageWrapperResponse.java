package com.resolum.intiva.platform.shared.interfaces.rest.resource;

/**
 * A generic response wrapper that encapsulates a message and data of type T.
 * This class is used to standardize the structure of API responses, providing a consistent format for clients to consume.
 *
 * @param <T> the type of the data being returned in the response
 * @param message a message describing the response, such as success or error information
 * @param data the actual data being returned in the response, which can be of any type specified by T
 */
public record MessageWrapperResponse<T>(
        String message,
        T data
) {
}
