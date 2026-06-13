package com.resolum.intiva.platform.household.domain.model.commands;

public record ClaimDeferredInviteCommand(
        String installId,
        String token
) {
}
