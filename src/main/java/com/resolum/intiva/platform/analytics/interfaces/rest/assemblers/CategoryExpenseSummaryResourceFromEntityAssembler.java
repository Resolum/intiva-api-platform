package com.resolum.intiva.platform.analytics.interfaces.rest.assemblers;

import com.resolum.intiva.platform.analytics.domain.model.valueobjects.CategoryExpenseSummary;
import com.resolum.intiva.platform.analytics.interfaces.rest.resources.responses.CategoryExpenseSummaryResource;
import com.resolum.intiva.platform.analytics.interfaces.rest.resources.responses.MoneyResource;

/**
 * Maps a {@link CategoryExpenseSummary} value object into its REST response representation.
 */
public class CategoryExpenseSummaryResourceFromEntityAssembler {

    /**
     * Converts a category expense summary value object into an API response resource.
     *
     * @param entity the category expense summary value object
     * @return the corresponding response resource
     */
    public static CategoryExpenseSummaryResource toResourceFromEntity(CategoryExpenseSummary entity) {
        return new CategoryExpenseSummaryResource(
                entity.categoryId(),
                entity.categoryName(),
                entity.categoryColor(),
                new MoneyResource(entity.totalAmount().getAmount(), entity.totalAmount().getCurrencyCode()),
                entity.transactionCount(),
                entity.percentage()
        );
    }
}
