package com.resolum.intiva.platform.household.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

@Embeddable
public record FamilyResourcesUsage(
        int membersActive,
        int spendingLimitsSet,
        int savingGoalsSet
) {
    public FamilyResourcesUsage() {
        this(0, 0, 0);
    }
}
