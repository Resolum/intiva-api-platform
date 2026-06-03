package com.resolum.intiva.platform.finances.domain.services;

import com.resolum.intiva.platform.finances.domain.model.aggregates.SpendingLimit;
import com.resolum.intiva.platform.finances.domain.model.queries.*;

import java.util.List;
import java.util.Optional;

/**
 * Application-facing query contract for the SpendingLimit aggregate.
 */
public interface SpendingLimitQueryService {
    /**
     * Retrieves one spending limit by identifier.
     *
     * @param query lookup query
     * @return the matching spending limit if it exists
     */
    Optional<SpendingLimit> handle(GetSpendingLimitByIdQuery query);

    /**
     * Retrieves all spending limits for an owner.
     *
     * @param query lookup query
     * @return list of matching limits
     */
    List<SpendingLimit> handle(GetSpendingLimitsByOwnerIdQuery query);

    /**
     * Retrieves all spending limits for an owner and owner type.
     *
     * @param query lookup query
     * @return list of matching limits
     */
    List<SpendingLimit> handle(GetSpendingLimitsByOwnerIdAndOwnerTypeQuery query);

    /**
     * Retrieves all spending limits that control a category.
     *
     * @param query lookup query
     * @return list of matching limits
     */
    List<SpendingLimit> handle(GetSpendingLimitsByCategoryIdQuery query);

    /**
     * Retrieves all spending limits that control a financial account.
     *
     * @param query lookup query
     * @return list of matching limits
     */
    List<SpendingLimit> handle(GetSpendingLimitsByFinancialAccountIdQuery query);
}
