package com.resolum.intiva.platform.household.domain.model.commands;

/**
 * Command to reject a pending family group invitation by token.
 * The rejectorId is nullable for cases where the user rejects without being authenticated.
 *
 * @param token        the unique token of the invitation to reject
 * @param rejectorId   the user ID of the person rejecting (nullable for unauthenticated rejection)
 * @param rejectorName the display name of the person rejecting (nullable, defaults to "Un usuario")
 */
public record RejectInvitationCommand(
        String token,
        Long rejectorId,
        String rejectorName
) {

    public RejectInvitationCommand(String token, Long rejectorId) {
        this(token, rejectorId, null);
    }

    public String rejectorName() {
        return rejectorName != null ? rejectorName : "Un usuario";
    }
}
