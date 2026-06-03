package com.resolum.intiva.platform.finances.infrastructure.persistence.jpa.repositories;

import com.resolum.intiva.platform.finances.domain.model.aggregates.RecurringTransaction;
import com.resolum.intiva.platform.shared.domain.valueobjects.OwnerTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Spring Data repository for recurring transaction persistence and scheduler lookup queries.
 */
@Repository
public interface RecurringTransactionRepository extends JpaRepository<RecurringTransaction, Long> {

    /**
     * Finds recurring transaction definitions by owner id.
     *
     * @param ownerId owner identifier
     * @return definitions owned by the given owner id
     */
    List<RecurringTransaction> findByOwnerId(Long ownerId);

    /**
     * Finds recurring transaction definitions by owner id and owner type.
     *
     * @param ownerId owner identifier
     * @param ownerType ownership scope
     * @return definitions matching the given owner information
     */
    List<RecurringTransaction> findByOwnerIdAndOwnerType(Long ownerId, OwnerTypes ownerType);

    /**
     * Finds active recurring transaction definitions that are due on or before the provided date.
     *
     * @param date scheduler cutoff date
     * @return due recurring transaction definitions
     */
    List<RecurringTransaction> findAllByActiveTrueAndNextExecutionDateLessThanEqual(LocalDate date);
}
