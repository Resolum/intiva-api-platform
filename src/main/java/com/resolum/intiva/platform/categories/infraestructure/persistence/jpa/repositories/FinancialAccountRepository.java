package com.resolum.intiva.platform.categories.infraestructure.persistence.jpa.repositories;

import com.resolum.intiva.platform.categories.domain.model.aggregates.FinancialAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

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
}
