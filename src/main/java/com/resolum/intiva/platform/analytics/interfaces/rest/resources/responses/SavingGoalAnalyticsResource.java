package com.resolum.intiva.platform.analytics.interfaces.rest.resources.responses;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * REST response returned for saving goal analytics operations.
 *
 * @param ownerId            owner identifier
 * @param ownerType          owner scope (INDIVIDUAL or FAMILY)
 * @param totalGoals         total number of saving goals
 * @param goalsCompleted     number of completed goals
 * @param goalsInProgress    number of goals in progress
 * @param goalsUncompleted   number of uncompleted goals
 * @param totalTargetAmount  total target amount across all goals
 * @param totalCurrentAmount total current saved amount across all goals
 * @param overallProgress    overall progress percentage
 * @param completionRate     completion rate percentage
 * @param details            detailed information per saving goal
 * @param generatedAt        timestamp when this analytics was generated
 */
@Schema(description = "REST response for saving goal analytics.")
public record SavingGoalAnalyticsResource(
        @Schema(description = "Owner identifier.", example = "1") String ownerId,
        @Schema(description = "Owner scope.", example = "INDIVIDUAL", allowableValues = {"INDIVIDUAL", "FAMILY"}) String ownerType,
        @Schema(description = "Total number of saving goals.", example = "3") Integer totalGoals,
        @Schema(description = "Number of completed goals.", example = "1") Integer goalsCompleted,
        @Schema(description = "Number of goals in progress.", example = "1") Integer goalsInProgress,
        @Schema(description = "Number of uncompleted goals.", example = "1") Integer goalsUncompleted,
        @Schema(description = "Total target amount across all goals.") MoneyResource totalTargetAmount,
        @Schema(description = "Total current saved amount across all goals.") MoneyResource totalCurrentAmount,
        @Schema(description = "Overall progress percentage.", example = "45.00") BigDecimal overallProgress,
        @Schema(description = "Completion rate percentage.", example = "33.33") BigDecimal completionRate,
        @Schema(description = "Detailed information per saving goal.") List<SavingGoalDetailResource> details,
        @Schema(description = "Timestamp when this analytics was generated.", example = "2026-06-13T18:00:00Z") Instant generatedAt
) {
}
