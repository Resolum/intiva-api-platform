package com.resolum.intiva.platform.household.application.internal.queryhandlers;

import com.resolum.intiva.platform.household.domain.model.aggregates.Family;
import com.resolum.intiva.platform.household.domain.model.queries.GetFamiliesByOwnerIdQuery;
import com.resolum.intiva.platform.household.domain.model.queries.GetFamilyByIdQuery;
import com.resolum.intiva.platform.household.domain.services.FamilyQueryService;
import com.resolum.intiva.platform.household.infrastructure.persistence.jpa.repositories.FamilyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of FamilyQueryService that delegates to the JPA repository.
 */
@Slf4j
@Service
public class FamilyQueryServiceImpl implements FamilyQueryService {

    private final FamilyRepository familyRepository;

    /**
     * Creates the query service with the required repository dependency.
     *
     * @param familyRepository the family group repository
     */
    public FamilyQueryServiceImpl(FamilyRepository familyRepository) {
        this.familyRepository = familyRepository;
    }

    @Override
    public Optional<Family> handle(GetFamilyByIdQuery query) {
        log.debug("Querying family by id: {}", query.familyId());
        return familyRepository.findById(query.familyId());
    }

    @Override
    public List<Family> handle(GetFamiliesByOwnerIdQuery query) {
        log.debug("Querying families for ownerId: {}", query.ownerId().getValue());
        return familyRepository.findByOwnerId(query.ownerId());
    }
}
