package com.resolum.intiva.platform.savings.domain.services;

import com.resolum.intiva.platform.savings.domain.model.aggregates.SavingGoal;
import com.resolum.intiva.platform.savings.domain.model.commands.CompleteSavingGoalCommand;
import com.resolum.intiva.platform.savings.domain.model.commands.ContributeToSavingGoalCommand;
import com.resolum.intiva.platform.savings.domain.model.commands.CreateSavingGoalCommand;
import com.resolum.intiva.platform.savings.domain.model.commands.UncompleteSavingGoalCommand;
import java.util.Optional;

/**
 * Service interface for handling saving goal commands.
 */
public interface SavingGoalCommandService {

    /**
     * Handles the creation of a new saving goal.
     *
     * @param command the command containing the saving goal details
     * @return the created SavingGoal entity
     * @throws IllegalArgumentException if any of the command parameters are invalid
     */
    SavingGoal handle(CreateSavingGoalCommand command);

    /**
     * Handles a contribution to an existing saving goal.
     *
     * @param command the command containing the contribution details
     * @return an Optional containing the updated SavingGoal if found, or empty if not found
     * @throws IllegalArgumentException if the contribution amount is invalid
     */
    Optional<SavingGoal> handle(ContributeToSavingGoalCommand command);

    /**
     * Handles the completion of an existing saving goal.
     *
     * @param command the command containing the ID of the saving goal to complete
     * @return the updated SavingGoal with COMPLETED status
     * @throws IllegalArgumentException if the saving goal does not exist
     * @throws IllegalStateException    if the saving goal is already marked as completed
     */
    SavingGoal handle(CompleteSavingGoalCommand command);

    /**
     * Handles reverting an existing saving goal to uncompleted status.
     *
     * @param command the command containing the ID of the saving goal to uncomplete
     * @return the updated SavingGoal with UNCOMPLETED status
     * @throws IllegalArgumentException if the saving goal does not exist
     * @throws IllegalStateException    if the saving goal is already marked as uncompleted
     */
    SavingGoal handle(UncompleteSavingGoalCommand command);
}
