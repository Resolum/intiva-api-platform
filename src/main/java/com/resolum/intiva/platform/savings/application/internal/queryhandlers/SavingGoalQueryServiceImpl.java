package com.resolum.intiva.platform.savings.application.internal.queryhandlers;

import com.resolum.intiva.platform.savings.domain.model.aggregates.SavingGoal;
import com.resolum.intiva.platform.savings.domain.model.queries.GetAllCompletedSavingGoalsByUserIdQuery;
import com.resolum.intiva.platform.savings.domain.model.queries.GetAllSavingGoalsByUserIdQuery;
import com.resolum.intiva.platform.savings.domain.model.queries.GetAllSavingGoalsByGroupIdQuery;
import com.resolum.intiva.platform.savings.domain.model.queries.GetSavingGoalByIdQuery;
import com.resolum.intiva.platform.savings.domain.services.SavingGoalQueryService;
import com.resolum.intiva.platform.savings.infrastructure.persistence.jpa.repositories.SavingGoalRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the SavingGoalQueryService.
 * Handles queries related to retrieving saving goals.
 */
@Service
public class SavingGoalQueryServiceImpl implements SavingGoalQueryService {

    private final SavingGoalRepository savingGoalRepository;

    /**
     * Constructs the query service implementation.
     *
     * @param savingGoalRepository the repository for saving goals
     */
    public SavingGoalQueryServiceImpl(SavingGoalRepository savingGoalRepository) {
        this.savingGoalRepository = savingGoalRepository;
    }

    /**
     * Retrieves a saving goal by its unique identifier.
     *
     * @param query the query containing the saving goal ID
     * @return an Optional containing the SavingGoal if found, or empty otherwise
     */
    @Override
    public Optional<SavingGoal> handle(GetSavingGoalByIdQuery query) {
        return savingGoalRepository.findById(query.savingGoalId());
    }

    /**
     * Retrieves all saving goals belonging to a specific user.
     *
     * @param query the query containing the user ID
     * @return a list of SavingGoal entities
     */
    @Override
    public List<SavingGoal> handle(GetAllSavingGoalsByUserIdQuery query) {
        return savingGoalRepository.findAllByActorUserId(query.userId());
    }

    /**
     * Retrieves all saving goals belonging to a specific family or group.
     *
     * @param query the query containing the group ID
     * @return a list of SavingGoal entities
     */
    @Override
    public List<SavingGoal> handle(GetAllSavingGoalsByGroupIdQuery query) {
        return savingGoalRepository.findAllByOwnerId(query.groupId());
    }

    /**
     * Retrieves all saving goals with COMPLETED status belonging to a specific user.
     *
     * @param query the query containing the user ID
     * @return a list of completed SavingGoal entities for the specified user
     */
    @Override
    public List<SavingGoal> handle(GetAllCompletedSavingGoalsByUserIdQuery query) {
        return savingGoalRepository.findByActorUserIdAndStatus(
                query.userId(),
                com.resolum.intiva.platform.savings.domain.model.valueobjects.SavingGoalStatus.COMPLETED
        );
    }
}
