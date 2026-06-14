package com.resolum.intiva.platform.categories.infraestructure.persistence.jpa.repositories;

import com.resolum.intiva.platform.categories.domain.model.aggregates.FinancialAccount;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing FinancialAccount entities in the database.
 *
 * This interface extends JpaRepository, providing CRUD operations and custom query methods for FinancialAccount entities.
 */
public interface FinancialAccountRepository extends JpaRepository<FinancialAccount, Long> {

    /**
     * Find all financial accounts by owner id
     * @param ownerId the owner id
     * @return the list of financial accounts
     */
    List<FinancialAccount> findAllByOwnerId(Long ownerId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from FinancialAccount a where a.id = :id")
    Optional<FinancialAccount> findByIdForUpdate(@Param("id") Long id);
}
