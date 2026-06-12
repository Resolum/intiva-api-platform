package com.resolum.intiva.platform.categories.interfaces.rest.resources.requests;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Resource for filtering categories based on specific criteria.
 *
 * @param ownerType The type of user making the request (e.g., 'individual', 'family') - required.
 * @param ownerId The unique identifier of the user/family making the request - required.
 * @param type The type of category to filter (e.g., 'expense', 'income') - optional.
 */
@Schema(
        name = "CategoryFilterResource",
        description = "Resource for filtering categories based on specific criteria."
)
public record CategoryFilterResource(
        @Schema(
                example = "family",
                allowableValues = {"family", "individual"}
        )
        @Parameter(
                description = "The type of user making the request (e.g., 'individual', 'family').",
                required = true
        )
        String ownerType,

        @Parameter(
                description = "The unique identifier of the user/family making the request.",
                required = true
        )
        Long ownerId,

        @Schema(
                example = "expense",
                allowableValues = {"expense", "income"}
        )
        @Parameter(
                description = "The type of category to filter (e.g., 'expense', 'income').",
                required = false
        )
        String type
) {
}
