package com.resolum.intiva.platform.savings.application.internal.commandhandlers;

import com.resolum.intiva.platform.savings.domain.model.aggregates.SavingGoal;
import com.resolum.intiva.platform.savings.domain.model.commands.CompleteSavingGoalCommand;
import com.resolum.intiva.platform.savings.domain.model.commands.ContributeToSavingGoalCommand;
import com.resolum.intiva.platform.savings.domain.model.commands.CreateSavingGoalCommand;
import com.resolum.intiva.platform.savings.domain.model.commands.DeleteSavingGoalCommand;
import com.resolum.intiva.platform.savings.domain.model.commands.UncompleteSavingGoalCommand;
import com.resolum.intiva.platform.savings.domain.model.commands.UpdateSavingGoalCommand;
import com.resolum.intiva.platform.savings.domain.model.entities.GoalContribution;
import com.resolum.intiva.platform.savings.domain.services.SavingGoalCommandService;
import com.resolum.intiva.platform.savings.infrastructure.persistence.jpa.repositories.GoalContributionRepository;
import com.resolum.intiva.platform.savings.infrastructure.persistence.jpa.repositories.SavingGoalRepository;
import com.resolum.intiva.platform.shared.domain.valueobjects.Money;
import com.resolum.intiva.platform.shared.domain.valueobjects.OwnerTypes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

import java.util.Optional;

/**
 * Implementation of the SavingGoalCommandService.
 * Handles commands related to creating and contributing to saving goals.
 */
@Slf4j
@Service
public class SavingGoalCommandServiceImpl implements SavingGoalCommandService {

    private final SavingGoalRepository savingGoalRepository;
    private final GoalContributionRepository goalContributionRepository;

    /**
     * Constructs the command service implementation.
     *
     * @param savingGoalRepository       the repository for saving goals
     * @param goalContributionRepository the repository for goal contributions
     */
    public SavingGoalCommandServiceImpl(SavingGoalRepository savingGoalRepository, GoalContributionRepository goalContributionRepository) {
        this.savingGoalRepository = savingGoalRepository;
        this.goalContributionRepository = goalContributionRepository;
    }

    /**
     * Handles the creation of a new saving goal.
     *
     * @param command the command containing the saving goal details
     * @return the created SavingGoal entity
     * @throws IllegalArgumentException if the title is blank, target amount is invalid, deadline is in the past, or required IDs are missing
     */
    @Override
    @Transactional
    public SavingGoal handle(CreateSavingGoalCommand command) {
        log.info("Creating saving goal: title='{}'", command.title());
        if (command.title() == null || command.title().trim().isEmpty()) {
            throw new IllegalArgumentException("Title must not be null or empty");
        }
        if (command.targetAmount() == null || command.targetAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Target amount must be greater than zero");
        }
        if (command.deadline() == null || command.deadline().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Deadline must not be in the past");
        }

        Long actorUserId = null;
        String ownerId = null;

        if (command.ownerType() == OwnerTypes.INDIVIDUAL) {
            if (command.actorUserId() == null) {
                throw new IllegalArgumentException("performedByUserId is required for INDIVIDUAL owner type");
            }
            actorUserId = command.actorUserId();
        } else if (command.ownerType() == OwnerTypes.FAMILY) {
            if (command.ownerId() == null || command.ownerId().trim().isEmpty()) {
                throw new IllegalArgumentException("ownerId (groupId) is required for FAMILY owner type");
            }
            ownerId = command.ownerId();
        }

        var targetAmount = new Money(command.targetAmount(), command.currencyCode());
        var currentAmount = new Money(BigDecimal.ZERO, command.currencyCode());

        var savingGoal = new SavingGoal(
                command.ownerType(),
                actorUserId,
                ownerId,
                command.title(),
                currentAmount,
                targetAmount,
                command.description(),
                command.startsAt() != null ? command.startsAt() : Instant.now(),
                command.deadline(),
                command.categoryId()
        );

        var saved = savingGoalRepository.save(savingGoal);
        log.info("Saving goal created with id={}", saved.getId());
        return saved;
    }

    /**
     * Handles a contribution to an existing saving goal.
     *
     * @param command the command containing the contribution details
     * @return an Optional containing the updated SavingGoal if found, or empty if not found
     * @throws IllegalArgumentException if the contribution amount is invalid
     */
    @Override
    @Transactional
    public Optional<SavingGoal> handle(ContributeToSavingGoalCommand command) {
        log.info("Contributing to saving goal id={}", command.savingGoalId());
        var savingGoalOpt = savingGoalRepository.findById(command.savingGoalId());
        
        if (savingGoalOpt.isEmpty()) {
            log.warn("Saving goal not found for contribution: id={}", command.savingGoalId());
            return Optional.empty(); 
        }

        var savingGoal = savingGoalOpt.get();

        var contributionAmount = new Money(command.amount(), command.currencyCode());
        var contribution = new GoalContribution(contributionAmount, command.contributorId(), command.savingGoalId());
        
        savingGoal.contribute(contribution);
        
        goalContributionRepository.save(contribution);
        savingGoalRepository.save(savingGoal);
        
        log.info("Contribution of {} registered to saving goal id={}", command.amount(), command.savingGoalId());
        return Optional.of(savingGoal);
    }

    /**
     * Handles the completion of an existing saving goal.
     * Finds the goal, validates it is not already completed, marks it as completed, and persists it.
     *
     * @param command the command containing the ID of the saving goal to complete
     * @return the updated SavingGoal with COMPLETED status
     * @throws IllegalArgumentException if no saving goal exists with the given ID
     * @throws IllegalStateException    if the saving goal is already marked as completed
     */
    @Override
    @Transactional
    public SavingGoal handle(CompleteSavingGoalCommand command) {
        log.info("Completing saving goal id={}", command.savingGoalId());
        var savingGoalOpt = savingGoalRepository.findById(command.savingGoalId());
        if (savingGoalOpt.isEmpty()) {
            log.warn("Saving goal not found for complete: id={}", command.savingGoalId());
            throw new IllegalArgumentException("Saving goal not found with id: " + command.savingGoalId());
        }
        var savingGoal = savingGoalOpt.get();
        savingGoal.completes();
        var saved = savingGoalRepository.save(savingGoal);
        log.info("Saving goal id={} completed", saved.getId());
        return saved;
    }

    /**
     * Handles reverting an existing saving goal back to uncompleted status.
     * Finds the goal, validates it is not already uncompleted, marks it as uncompleted, and persists it.
     *
     * @param command the command containing the ID of the saving goal to uncomplete
     * @return the updated SavingGoal with UNCOMPLETED status
     * @throws IllegalArgumentException if no saving goal exists with the given ID
     * @throws IllegalStateException    if the saving goal is already marked as uncompleted
     */
    @Override
    @Transactional
    public SavingGoal handle(UncompleteSavingGoalCommand command) {
        log.info("Uncompleting saving goal id={}", command.savingGoalId());
        var savingGoalOpt = savingGoalRepository.findById(command.savingGoalId());
        if (savingGoalOpt.isEmpty()) {
            log.warn("Saving goal not found for uncomplete: id={}", command.savingGoalId());
            throw new IllegalArgumentException("Saving goal not found with id: " + command.savingGoalId());
        }
        var savingGoal = savingGoalOpt.get();
        savingGoal.uncompletes();
        var saved = savingGoalRepository.save(savingGoal);
        log.info("Saving goal id={} uncompleted", saved.getId());
        return saved;
    }

    /**
     * Handles updating the title, description, and/or target amount of an existing saving goal.
     * Only fields provided (non-null) are updated. The operation is rejected if the deadline
     * has already passed.
     *
     * @param command the command containing the ID and the fields to update
     * @return the updated SavingGoal entity
     * @throws IllegalArgumentException if no saving goal exists with the given ID
     * @throws IllegalStateException    if the saving goal's deadline has already passed
     */
    @Override
    @Transactional
    public SavingGoal handle(UpdateSavingGoalCommand command) {
        log.info("Updating saving goal id={}", command.savingGoalId());
        var savingGoalOpt = savingGoalRepository.findById(command.savingGoalId());
        if (savingGoalOpt.isEmpty()) {
            log.warn("Saving goal not found for update: id={}", command.savingGoalId());
            throw new IllegalArgumentException("Saving goal not found with id: " + command.savingGoalId());
        }
        var savingGoal = savingGoalOpt.get();

        if (command.title() != null || command.description() != null) {
            String newTitle = command.title() != null ? command.title() : savingGoal.getTitle();
            String newDescription = command.description() != null ? command.description() : savingGoal.getDescription();
            savingGoal.editDescriptionOrTitle(newDescription, newTitle);
        }

        if (command.newTargetAmount() != null) {
            if (command.newTargetAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Target amount must be greater than zero");
            }
            var newTargetAmount = new Money(command.newTargetAmount(), savingGoal.getTargetAmount().currencyCode());
            savingGoal.editTargetAmount(newTargetAmount);
        }

        var saved = savingGoalRepository.save(savingGoal);
        log.info("Saving goal id={} updated", saved.getId());
        return saved;
    }

    /**
     * Handles deleting an existing saving goal.
     * The operation is rejected if the saving goal's deadline has already passed.
     *
     * @param command the command containing the ID of the saving goal to delete
     * @throws IllegalArgumentException if no saving goal exists with the given ID
     * @throws IllegalStateException    if the saving goal's deadline has already passed
     */
    @Override
    @Transactional
    public void handle(DeleteSavingGoalCommand command) {
        log.info("Deleting saving goal id={}", command.savingGoalId());
        var savingGoalOpt = savingGoalRepository.findById(command.savingGoalId());
        if (savingGoalOpt.isEmpty()) {
            log.warn("Saving goal not found for delete: id={}", command.savingGoalId());
            throw new IllegalArgumentException("Saving goal not found with id: " + command.savingGoalId());
        }
        var savingGoal = savingGoalOpt.get();
        if (!savingGoal.isEditable()) {
            throw new IllegalStateException("Saving goal cannot be deleted after its deadline has passed");
        }
        savingGoalRepository.delete(savingGoal);
        log.info("Saving goal id={} deleted", command.savingGoalId());
    }
}
