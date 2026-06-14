package com.resolum.intiva.platform.household.infrastructure.persistence.redis.entities;

import com.resolum.intiva.platform.shared.infrastructure.persistence.redis.entities.AbstractCacheEntity;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.concurrent.TimeUnit;

@RedisHash("invitation:link")
public class InvitationLinkCacheEntity extends AbstractCacheEntity {

    private Long familyId;
    private Long inviterId;
    private String inviterName;
    private String familyName;
    private String inviteeEmail;
    private String message;
    private String inviteUrl;

    @TimeToLive(unit = TimeUnit.SECONDS)
    private Long ttl = 900L;

    public InvitationLinkCacheEntity() {}

    public InvitationLinkCacheEntity(String token, Long familyId, Long inviterId, String inviterName, String familyName, String inviteeEmail, String message, String inviteUrl) {
        setId(token);
        this.familyId = familyId;
        this.inviterId = inviterId;
        this.inviterName = inviterName;
        this.familyName = familyName;
        this.inviteeEmail = inviteeEmail;
        this.message = message;
        this.inviteUrl = inviteUrl;
    }

    public Long getFamilyId() { return familyId; }
    public void setFamilyId(Long familyId) { this.familyId = familyId; }

    public Long getInviterId() { return inviterId; }
    public void setInviterId(Long inviterId) { this.inviterId = inviterId; }

    public String getInviterName() { return inviterName; }
    public void setInviterName(String inviterName) { this.inviterName = inviterName; }

    public String getFamilyName() { return familyName; }
    public void setFamilyName(String familyName) { this.familyName = familyName; }

    public String getInviteeEmail() { return inviteeEmail; }
    public void setInviteeEmail(String inviteeEmail) { this.inviteeEmail = inviteeEmail; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getInviteUrl() { return inviteUrl; }
    public void setInviteUrl(String inviteUrl) { this.inviteUrl = inviteUrl; }

    public Long getTtl() { return ttl; }
    public void setTtl(Long ttl) { this.ttl = ttl; }
}
