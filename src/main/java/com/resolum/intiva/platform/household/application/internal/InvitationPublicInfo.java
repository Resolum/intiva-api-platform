package com.resolum.intiva.platform.household.application.internal;

import java.time.LocalDateTime;

public record InvitationPublicInfo(
        String groupName,
        String inviterName,
        int memberCount,
        String status,
        LocalDateTime expiresAt
) {
}
