package com.resolum.intiva.platform.savings.domain.services;

import com.resolum.intiva.platform.savings.domain.model.aggregates.SavingGoal;
import com.resolum.intiva.platform.savings.domain.model.queries.GetAllSavingGoalsByUserIdQuery;
import com.resolum.intiva.platform.savings.domain.model.queries.GetAllSavingGoalsByGroupIdQuery;
import com.resolum.intiva.platform.savings.domain.model.queries.GetSavingGoalByIdQuery;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for handling saving goal queries.
 */
public interface SavingGoalQueryService {

    /**
     * Retrieves a saving goal by its unique identifier.
     *
     * @param query the query containing the saving goal ID
     * @return an Optional containing the SavingGoal if found, or empty otherwise
     */
    Optional<SavingGoal> handle(GetSavingGoalByIdQuery query);

    /**
     * Retrieves all saving goals belonging to a specific user.
     *
     * @param query the query containing the user ID
     * @return a list of SavingGoal entities
     */
    List<SavingGoal> handle(GetAllSavingGoalsByUserIdQuery query);

    /**
     * Retrieves all saving goals belonging to a specific family or group.
     *
     * @param query the query containing the group ID
     * @return a list of SavingGoal entities
     */
    List<SavingGoal> handle(GetAllSavingGoalsByGroupIdQuery query);
}
