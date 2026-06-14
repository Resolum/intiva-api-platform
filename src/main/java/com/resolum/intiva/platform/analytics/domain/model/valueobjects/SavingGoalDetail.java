package com.resolum.intiva.platform.analytics.domain.model.valueobjects;

import com.resolum.intiva.platform.savings.domain.model.valueobjects.SavingGoalStatus;
import com.resolum.intiva.platform.shared.domain.valueobjects.Money;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Detailed information about a single saving goal for analytics purposes.
 *
 * @param savingGoalId       saving goal identifier
 * @param title              title of the saving goal
 * @param targetAmount       target amount to be saved
 * @param currentAmount      current saved amount
 * @param progressPercentage progress percentage toward the target
 * @param deadline           deadline date for the goal
 * @param status             current status of the goal
 * @param daysRemaining      days remaining until the deadline
 */
@Schema(description = "Detailed information about a single saving goal for analytics purposes.")
public record SavingGoalDetail(
        @Schema(description = "Saving goal identifier.", example = "1") String savingGoalId,
        @Schema(description = "Title of the saving goal.", example = "Vacaciones a Europa") String title,
        @Schema(description = "Target amount to be saved.") Money targetAmount,
        @Schema(description = "Current saved amount.") Money currentAmount,
        @Schema(description = "Progress percentage toward the target.", example = "60.00") BigDecimal progressPercentage,
        @Schema(description = "Deadline date for the goal.", example = "2026-12-31") LocalDate deadline,
        @Schema(description = "Current status of the goal.") SavingGoalStatus status,
        @Schema(description = "Days remaining until the deadline.", example = "120") Long daysRemaining
) {
}
