package com.resolum.intiva.platform.analytics.interfaces.rest.resources.requests;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request to generate a report.")
public record GenerateReportResource(
        @Schema(description = "Owner identifier.", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        String ownerId,
        @Schema(description = "Owner scope.", example = "INDIVIDUAL", requiredMode = Schema.RequiredMode.REQUIRED,
                allowableValues = {"INDIVIDUAL", "FAMILY"})
        String ownerType,
        @Schema(description = "Period start date (yyyy-MM-dd).", example = "2026-01-01", requiredMode = Schema.RequiredMode.REQUIRED)
        String periodStart,
        @Schema(description = "Period end date (yyyy-MM-dd).", example = "2026-06-30", requiredMode = Schema.RequiredMode.REQUIRED)
        String periodEnd,
        @Schema(description = "Category identifier (optional, null for all categories).", example = "1")
        String categoryId,
        @Schema(description = "Report format.", example = "CSV", requiredMode = Schema.RequiredMode.REQUIRED,
                allowableValues = {"CSV", "PDF"})
        String format
) {
}
