package com.resolum.intiva.platform.analytics.interfaces.rest.assemblers;

import com.resolum.intiva.platform.analytics.domain.model.valueobjects.CategoryExpenseSummary;
import com.resolum.intiva.platform.analytics.domain.model.valueobjects.ReportSummaryPreview;
import com.resolum.intiva.platform.analytics.interfaces.rest.resources.responses.MoneyResource;
import com.resolum.intiva.platform.analytics.interfaces.rest.resources.responses.ReportPreviewResource;
import com.resolum.intiva.platform.analytics.interfaces.rest.resources.responses.ReportSummaryItemResource;

/**
 * Maps a {@link ReportSummaryPreview} domain value object into its REST API
 * response representation {@link ReportPreviewResource}.
 *
 * <p>This assembler follows the static-method convention used throughout the
 * analytics bounded context (see
 * {@link com.resolum.intiva.platform.analytics.interfaces.rest.assemblers.AnalyticsSummaryResourceFromEntityAssembler
 * AnalyticsSummaryResourceFromEntityAssembler}).</p>
 */
public class ReportPreviewResourceFromEntityAssembler {

    /**
     * Converts a report summary preview into an API response resource.
     *
     * <p>Money values are flattened into {@link MoneyResource} instances.
     * Category summaries are converted via {@link #toItemResource}.</p>
     *
     * @param entity the domain report summary preview
     * @return the corresponding REST response resource
     */
    public static ReportPreviewResource toResourceFromEntity(ReportSummaryPreview entity) {
        return new ReportPreviewResource(
                new MoneyResource(entity.totalIncome().getAmount(), entity.totalIncome().getCurrencyCode()),
                new MoneyResource(entity.totalExpenses().getAmount(), entity.totalExpenses().getCurrencyCode()),
                new MoneyResource(entity.netBalance().getAmount(), entity.netBalance().getCurrencyCode()),
                entity.transactionCount(),
                entity.topCategories().stream()
                        .map(ReportPreviewResourceFromEntityAssembler::toItemResource)
                        .toList(),
                entity.periodStart().toString(),
                entity.periodEnd().toString(),
                entity.ownerId(),
                entity.ownerType().name()
        );
    }

    /**
     * Converts a {@link CategoryExpenseSummary} domain value object into a
     * {@link ReportSummaryItemResource} REST response.
     *
     * @param category the domain category expense summary
     * @return the corresponding REST item resource
     */
    private static ReportSummaryItemResource toItemResource(CategoryExpenseSummary category) {
        return new ReportSummaryItemResource(
                category.categoryId(),
                category.categoryName(),
                category.categoryColor(),
                new MoneyResource(category.totalAmount().getAmount(), category.totalAmount().getCurrencyCode()),
                category.transactionCount(),
                category.percentage().toString()
        );
    }
}
