package com.resolum.intiva.platform.profiles.domain.model.aggregates;

import com.resolum.intiva.platform.profiles.domain.model.valueobjects.FirstTransactionTutorialStep;
import com.resolum.intiva.platform.shared.domain.aggregates.AuditableAbstractAggregate;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * Represents the onboarding process for a user, tracking their progress through the first transaction tutorial.
 */
@Entity
@Getter
@Setter
public class Onboarding extends AuditableAbstractAggregate<Onboarding> {

    private Long userId;

    @Enumerated(EnumType.STRING)
    private FirstTransactionTutorialStep currentStep;

    private boolean onboardingCompleted;

    private Instant completedAt;

    /**
     * Default constructor for JPA. Should not be used directly.
     */
    protected Onboarding() {
    }

    /**
     * Initializes the onboarding process for a new user, starting at the first tutorial step.
     *
     * @param ownerId The ID of the user for whom the onboarding is being created.
     */
    public Onboarding(Long ownerId) {
        this.userId = ownerId;
        this.currentStep = FirstTransactionTutorialStep.PRESENTATION;
        this.onboardingCompleted = false;
    }

    /**
     * Advances the user to the next step in the first transaction tutorial. If the user is already at the last step, marks the onboarding as completed.
     *
     * @throws IllegalStateException if the onboarding process has not been started (i.e., currentStep is null).
     */
    public void advanceTutorialStep() {

        if (this.currentStep == null) {
            throw new IllegalStateException("Tutorial not started");
        }

        switch (this.currentStep) {

            case PRESENTATION ->
                    this.currentStep =
                            FirstTransactionTutorialStep.OPEN_CREATE_TRANSACTION;

            case OPEN_CREATE_TRANSACTION ->
                    this.currentStep =
                            FirstTransactionTutorialStep.SELECT_CATEGORY;

            case SELECT_CATEGORY ->
                    this.currentStep =
                            FirstTransactionTutorialStep.CONFIRM_TRANSACTION;

            case CONFIRM_TRANSACTION -> {
                this.currentStep = FirstTransactionTutorialStep.COMPLETED;
                this.onboardingCompleted = true;
                this.completedAt = Instant.now();
            }

            case COMPLETED ->
                    throw new IllegalStateException("Onboarding already completed");
        }
    }

    /**
     * Completes the onboarding tutorial without walking through every step.
     */
    public void skipTutorial() {
        this.currentStep = FirstTransactionTutorialStep.COMPLETED;
        this.onboardingCompleted = true;
        this.completedAt = Instant.now();
    }

    /**
     * Rolls back the user to the previous step in the first transaction tutorial.
     * Used when the user abandons the flow mid-way and needs to restart from a safe state.
     *
     * @throws IllegalStateException if the onboarding is already at the first step or completed.
     */
    public void rollbackTutorialStep() {

        if (this.currentStep == null) {
            throw new IllegalStateException("Tutorial not started");
        }

        if (this.onboardingCompleted) {
            throw new IllegalStateException("Cannot rollback a completed onboarding");
        }

        switch (this.currentStep) {

            case SELECT_CATEGORY, CONFIRM_TRANSACTION ->
                    this.currentStep =
                            FirstTransactionTutorialStep.OPEN_CREATE_TRANSACTION;

            case OPEN_CREATE_TRANSACTION ->
                    this.currentStep =
                            FirstTransactionTutorialStep.PRESENTATION;

            case PRESENTATION ->
                    throw new IllegalStateException("Cannot rollback from the presentation step");

            case COMPLETED ->
                    throw new IllegalStateException("Cannot rollback a completed onboarding");
        }
    }
}
