package com.resolum.intiva.platform.household.infrastructure.persistence.jpa;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "deferred_deep_links")
public class DeferredDeepLinkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "install_id", nullable = false)
    private String installId;

    @Column(name = "invite_token", nullable = false)
    private String inviteToken;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private boolean claimed;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    public DeferredDeepLinkEntity(String installId, String inviteToken) {
        this.installId = installId;
        this.inviteToken = inviteToken;
        this.claimed = false;
    }

    public void markClaimed() {
        this.claimed = true;
    }
}
