package com.resolum.intiva.platform.analytics.interfaces.rest.resources.responses;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * REST response for a saving goal detail in analytics.
 *
 * @param savingGoalId      saving goal identifier
 * @param title             title of the goal
 * @param targetAmount      target amount
 * @param currentAmount     current saved amount
 * @param progressPercentage progress percentage
 * @param deadline          deadline date
 * @param status            goal status (INPROGRESS, COMPLETED, UNCOMPLETED)
 * @param daysRemaining     days remaining until the deadline
 */
@Schema(description = "REST response for a saving goal detail in analytics.")
public record SavingGoalDetailResource(
        @Schema(description = "Saving goal identifier.", example = "1") String savingGoalId,
        @Schema(description = "Title of the goal.", example = "Vacaciones a Europa") String title,
        @Schema(description = "Target amount.") MoneyResource targetAmount,
        @Schema(description = "Current saved amount.") MoneyResource currentAmount,
        @Schema(description = "Progress percentage.", example = "60.00") BigDecimal progressPercentage,
        @Schema(description = "Deadline date.", example = "2026-12-31") LocalDate deadline,
        @Schema(description = "Goal status.", example = "INPROGRESS", allowableValues = {"INPROGRESS", "COMPLETED", "UNCOMPLETED"}) String status,
        @Schema(description = "Days remaining.", example = "120") Long daysRemaining
) {
}
