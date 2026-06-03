package com.resolum.intiva.platform.finances.domain.model.queries;

import com.resolum.intiva.platform.shared.domain.valueobjects.OwnerTypes;

/**
 * Query used to retrieve spending limits by owner id and owner type.
 *
 * @param ownerId user id or group id
 * @param ownerType scope of the owner
 */
public record GetSpendingLimitsByOwnerIdAndOwnerTypeQuery(Long ownerId, OwnerTypes ownerType) {
}
