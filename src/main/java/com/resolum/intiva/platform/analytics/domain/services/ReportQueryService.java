package com.resolum.intiva.platform.analytics.domain.services;

import com.resolum.intiva.platform.analytics.domain.model.queries.GetReportPreviewQuery;
import com.resolum.intiva.platform.analytics.domain.model.valueobjects.ReportSummaryPreview;

/**
 * Service interface for report preview queries.
 *
 * <p>Implementations retrieve financial data through the ACL layer and compute
 * aggregated metrics without persisting or caching the result. The preview is
 * always computed on the fly from the source transactions.</p>
 */
public interface ReportQueryService {

    /**
     * Computes a financial report preview for the given query parameters.
     *
     * <p>The preview includes income/expense totals, net balance, transaction count,
     * and the top 5 expense categories with their respective amounts and
     * percentages.</p>
     *
     * @param query the query parameters (owner, period, optional category filter)
     * @return a fully populated {@link ReportSummaryPreview} with aggregated metrics
     */
    ReportSummaryPreview getReportPreview(GetReportPreviewQuery query);
}
