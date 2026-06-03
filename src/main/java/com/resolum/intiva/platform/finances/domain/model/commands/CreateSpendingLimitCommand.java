package com.resolum.intiva.platform.finances.domain.model.commands;

import com.resolum.intiva.platform.finances.domain.model.valueobjects.SpendingLimitTargetType;
import com.resolum.intiva.platform.shared.domain.valueobjects.Money;
import com.resolum.intiva.platform.shared.domain.valueobjects.OwnerTypes;
import jakarta.validation.Valid;

import java.time.LocalDate;

/**
 * Command used to create a spending limit for personal or family finances.
 *
 * @param ownerId user id for INDIVIDUAL limits or group id for FAMILY limits
 * @param ownerType scope of the limit: personal or group/family
 * @param targetType kind of target controlled by the limit
 * @param targetId category id or financial account id, depending on targetType
 * @param limitAmount maximum amount allowed during the period
 * @param startDate first date included in the period
 * @param endDate last date included in the period
 */
public record CreateSpendingLimitCommand(
        Long ownerId,
        OwnerTypes ownerType,
        SpendingLimitTargetType targetType,
        Long targetId,
        @Valid Money limitAmount,
        LocalDate startDate,
        LocalDate endDate
) {
}
