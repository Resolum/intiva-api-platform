package com.resolum.intiva.platform.finances.domain.services;

import com.resolum.intiva.platform.finances.domain.model.aggregates.RecurringTransaction;
import com.resolum.intiva.platform.finances.domain.model.queries.GetRecurringTransactionByIdQuery;
import com.resolum.intiva.platform.finances.domain.model.queries.GetRecurringTransactionsByOwnerIdAndOwnerTypeQuery;
import com.resolum.intiva.platform.finances.domain.model.queries.GetRecurringTransactionsByOwnerIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Application-facing query contract for recurring transaction definitions.
 */
public interface RecurringTransactionQueryService {

    /**
     * Retrieves one recurring transaction definition by id.
     *
     * @param query identifier query
     * @return matching recurring transaction if it exists
     */
    Optional<RecurringTransaction> handle(GetRecurringTransactionByIdQuery query);

    /**
     * Retrieves recurring transaction definitions by owner id.
     *
     * @param query owner query
     * @return recurring transaction definitions owned by the specified owner
     */
    List<RecurringTransaction> handle(GetRecurringTransactionsByOwnerIdQuery query);

    /**
     * Retrieves recurring transaction definitions by owner id and owner type.
     *
     * @param query scoped owner query
     * @return recurring transaction definitions matching owner id and owner type
     */
    List<RecurringTransaction> handle(GetRecurringTransactionsByOwnerIdAndOwnerTypeQuery query);
}
