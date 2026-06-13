package com.resolum.intiva.platform.household.application.internal;

import java.time.LocalDateTime;

public record InvitationLinkResult(
        String token,
        String inviteUrl,
        LocalDateTime expiresAt
) {
}
