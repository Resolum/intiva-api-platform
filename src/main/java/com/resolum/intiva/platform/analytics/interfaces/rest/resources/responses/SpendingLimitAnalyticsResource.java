package com.resolum.intiva.platform.analytics.interfaces.rest.resources.responses;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * REST response returned for spending limit analytics operations.
 *
 * @param ownerId         owner identifier
 * @param ownerType       owner scope (INDIVIDUAL or FAMILY)
 * @param totalLimitsSet  total number of spending limits set
 * @param limitsExceeded  number of limits that have been exceeded
 * @param limitsAtWarning number of limits in warning state
 * @param limitsSafe      number of limits in safe state
 * @param exceededRate    percentage of limits exceeded
 * @param warningRate     percentage of limits in warning
 * @param details         detailed information per spending limit
 * @param generatedAt     timestamp when this analytics was generated
 */
@Schema(description = "REST response for spending limit analytics.")
public record SpendingLimitAnalyticsResource(
        @Schema(description = "Owner identifier.", example = "1") String ownerId,
        @Schema(description = "Owner scope.", example = "INDIVIDUAL", allowableValues = {"INDIVIDUAL", "FAMILY"}) String ownerType,
        @Schema(description = "Total number of spending limits set.", example = "5") Integer totalLimitsSet,
        @Schema(description = "Number of limits that have been exceeded.", example = "1") Integer limitsExceeded,
        @Schema(description = "Number of limits in warning state.", example = "2") Integer limitsAtWarning,
        @Schema(description = "Number of limits in safe state.", example = "2") Integer limitsSafe,
        @Schema(description = "Percentage of limits exceeded.", example = "20.00") BigDecimal exceededRate,
        @Schema(description = "Percentage of limits in warning.", example = "40.00") BigDecimal warningRate,
        @Schema(description = "Detailed information per spending limit.") List<SpendingLimitDetailResource> details,
        @Schema(description = "Timestamp when this analytics was generated.", example = "2026-06-13T18:00:00Z") Instant generatedAt
) {
}
