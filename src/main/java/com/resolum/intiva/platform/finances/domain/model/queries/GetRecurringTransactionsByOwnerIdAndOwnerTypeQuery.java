package com.resolum.intiva.platform.finances.domain.model.queries;

import com.resolum.intiva.platform.shared.domain.valueobjects.OwnerTypes;

/**
 * Query used to retrieve recurring transaction definitions for one owner inside a specific ownership scope.
 *
 * @param ownerId owner identifier used as filter
 * @param ownerType owner scope used as filter
 */
public record GetRecurringTransactionsByOwnerIdAndOwnerTypeQuery(Long ownerId, OwnerTypes ownerType) {
}
