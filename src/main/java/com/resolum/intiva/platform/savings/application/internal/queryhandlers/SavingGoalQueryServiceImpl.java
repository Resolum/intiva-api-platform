package com.resolum.intiva.platform.savings.application.internal.queryhandlers;

import com.resolum.intiva.platform.savings.domain.model.aggregates.SavingGoal;
import com.resolum.intiva.platform.savings.domain.model.queries.GetAllCompletedSavingGoalsByUserIdQuery;
import com.resolum.intiva.platform.savings.domain.model.queries.GetAllSavingGoalsByUserIdQuery;
import com.resolum.intiva.platform.savings.domain.model.queries.GetAllSavingGoalsByGroupIdQuery;
import com.resolum.intiva.platform.savings.domain.model.queries.GetSavingGoalByIdQuery;
import com.resolum.intiva.platform.savings.domain.services.SavingGoalQueryService;
import com.resolum.intiva.platform.savings.infrastructure.persistence.jpa.repositories.SavingGoalRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the SavingGoalQueryService.
 * Handles queries related to retrieving saving goals.
 */
@Slf4j
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
        log.debug("Querying saving goal by id={}", query.savingGoalId());
        return savingGoalRepository.findById(query.savingGoalId());
    }

    @Override
    public List<SavingGoal> handle(GetAllSavingGoalsByUserIdQuery query) {
        log.debug("Querying saving goals for userId={}", query.userId());
        return savingGoalRepository.findAllByActorUserId(query.userId());
    }

    @Override
    public List<SavingGoal> handle(GetAllSavingGoalsByGroupIdQuery query) {
        log.debug("Querying saving goals for groupId={}", query.groupId());
        return savingGoalRepository.findAllByOwnerId(query.groupId());
    }

    @Override
    public List<SavingGoal> handle(GetAllCompletedSavingGoalsByUserIdQuery query) {
        log.debug("Querying completed saving goals for userId={}", query.userId());
        return savingGoalRepository.findByActorUserIdAndStatus(
                query.userId(),
                com.resolum.intiva.platform.savings.domain.model.valueobjects.SavingGoalStatus.COMPLETED
        );
    }
}
