package com.resolum.intiva.platform.analytics.interfaces.rest.controllers;

import com.resolum.intiva.platform.analytics.domain.model.queries.*;
import com.resolum.intiva.platform.analytics.domain.services.AnalyticsQueryService;
import com.resolum.intiva.platform.analytics.interfaces.rest.assemblers.*;
import com.resolum.intiva.platform.analytics.interfaces.rest.resources.responses.*;
import com.resolum.intiva.platform.shared.domain.valueobjects.OwnerTypes;
import com.resolum.intiva.platform.shared.domain.valueobjects.PeriodTypes;
import com.resolum.intiva.platform.shared.interfaces.rest.resource.MessageResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST controller that exposes read-only analytics and insights for the finances and savings bounded contexts.
 *
 * <p>The same API serves both personal and family finances by using {@code ownerType} to distinguish
 * INDIVIDUAL from FAMILY ownership. All data is computed on the fly from the underlying {@code finances}
 * and {@code savings} bounded contexts — no dedicated analytics tables are used.</p>
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/analytics", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Analytics", description = "Endpoints for financial analytics and insights.")
public class AnalyticsController {

    /**
     * Domain query service used to compute analytics from finances and savings data.
     */
    private final AnalyticsQueryService analyticsQueryService;

    /**
     * Creates the analytics controller with its required query service dependency.
     *
     * @param analyticsQueryService query service used to compute all analytics
     */
    public AnalyticsController(AnalyticsQueryService analyticsQueryService) {
        this.analyticsQueryService = analyticsQueryService;
    }

    /**
     * Retrieves a financial summary for the given owner and period, including total income, expenses,
     * net balance, savings rate, and expenses grouped by category.
     */
    @GetMapping("/summary")
    @Operation(
            summary = "Get analytics summary",
            description = "Returns a financial summary for the given owner and period, including total income, expenses, net balance, and expenses grouped by category."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Analytics summary retrieved successfully",
                    content = @Content(schema = @Schema(implementation = AnalyticsSummaryResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid parameters")
    })
    public ResponseEntity<AnalyticsSummaryResource> getAnalyticsSummary(
            @Parameter(in = ParameterIn.QUERY, description = "Owner identifier.", example = "1", required = true)
            @RequestParam String ownerId,
            @Parameter(in = ParameterIn.QUERY, description = "Owner scope.", example = "INDIVIDUAL", required = true,
                    schema = @Schema(allowableValues = {"INDIVIDUAL", "FAMILY"}))
            @RequestParam String ownerType,
            @Parameter(in = ParameterIn.QUERY, description = "Period type.", example = "MONTHLY", required = true,
                    schema = @Schema(allowableValues = {"DAILY", "WEEKLY", "MONTHLY", "ANNUAL"}))
            @RequestParam String periodType,
            @Parameter(in = ParameterIn.QUERY, description = "Period start date.", example = "2026-06-01", required = true)
            @RequestParam LocalDate periodStart,
            @Parameter(in = ParameterIn.QUERY, description = "Period end date.", example = "2026-06-30", required = true)
            @RequestParam LocalDate periodEnd
    ) {
        log.info("GET /api/v1/analytics/summary - ownerId={}, ownerType={}, periodType={}, periodStart={}, periodEnd={}",
                ownerId, ownerType, periodType, periodStart, periodEnd);
        try {
            var query = new GetAnalyticsSummaryByOwnerQuery(
                    ownerId,
                    OwnerTypes.valueOf(ownerType.toUpperCase()),
                    PeriodTypes.valueOf(periodType.toUpperCase()),
                    periodStart,
                    periodEnd
            );
            var summary = analyticsQueryService.handle(query);
            var resource = AnalyticsSummaryResourceFromEntityAssembler.toResourceFromEntity(summary);
            return ResponseEntity.ok(resource);
        } catch (IllegalArgumentException e) {
            log.warn("Bad request for analytics summary: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            log.error("Unexpected error getting analytics summary", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    /**
     * Retrieves analytics about spending limits for the given owner, including how many are exceeded,
     * in warning, or safe.
     */
    @GetMapping("/spending-limits")
    @Operation(
            summary = "Get spending limit analytics",
            description = "Returns analytics about spending limits for the given owner, including how many are exceeded, in warning, or safe."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Spending limit analytics retrieved successfully",
                    content = @Content(schema = @Schema(implementation = SpendingLimitAnalyticsResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid parameters")
    })
    public ResponseEntity<SpendingLimitAnalyticsResource> getSpendingLimitAnalytics(
            @Parameter(in = ParameterIn.QUERY, description = "Owner identifier.", example = "1", required = true)
            @RequestParam String ownerId,
            @Parameter(in = ParameterIn.QUERY, description = "Owner scope.", example = "INDIVIDUAL", required = true,
                    schema = @Schema(allowableValues = {"INDIVIDUAL", "FAMILY"}))
            @RequestParam String ownerType,
            @Parameter(in = ParameterIn.QUERY, description = "Period type.", example = "MONTHLY", required = true,
                    schema = @Schema(allowableValues = {"DAILY", "WEEKLY", "MONTHLY", "ANNUAL"}))
            @RequestParam String periodType
    ) {
        log.info("GET /api/v1/analytics/spending-limits - ownerId={}, ownerType={}, periodType={}",
                ownerId, ownerType, periodType);
        try {
            var query = new GetSpendingLimitAnalyticsByOwnerQuery(
                    ownerId,
                    OwnerTypes.valueOf(ownerType.toUpperCase()),
                    PeriodTypes.valueOf(periodType.toUpperCase())
            );
            var analytics = analyticsQueryService.handle(query);
            var resource = SpendingLimitAnalyticsResourceFromEntityAssembler.toResourceFromEntity(analytics);
            return ResponseEntity.ok(resource);
        } catch (IllegalArgumentException e) {
            log.warn("Bad request for spending limit analytics: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            log.error("Unexpected error getting spending limit analytics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    /**
     * Retrieves analytics about saving goals for the given owner, including completion rate and overall progress.
     */
    @GetMapping("/saving-goals")
    @Operation(
            summary = "Get saving goal analytics",
            description = "Returns analytics about saving goals for the given owner, including completion rate and overall progress."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saving goal analytics retrieved successfully",
                    content = @Content(schema = @Schema(implementation = SavingGoalAnalyticsResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid parameters")
    })
    public ResponseEntity<SavingGoalAnalyticsResource> getSavingGoalAnalytics(
            @Parameter(in = ParameterIn.QUERY, description = "Owner identifier.", example = "1", required = true)
            @RequestParam String ownerId,
            @Parameter(in = ParameterIn.QUERY, description = "Owner scope.", example = "INDIVIDUAL", required = true,
                    schema = @Schema(allowableValues = {"INDIVIDUAL", "FAMILY"}))
            @RequestParam String ownerType
    ) {
        log.info("GET /api/v1/analytics/saving-goals - ownerId={}, ownerType={}", ownerId, ownerType);
        try {
            var query = new GetSavingGoalAnalyticsByOwnerQuery(
                    ownerId,
                    OwnerTypes.valueOf(ownerType.toUpperCase())
            );
            var analytics = analyticsQueryService.handle(query);
            var resource = SavingGoalAnalyticsResourceFromEntityAssembler.toResourceFromEntity(analytics);
            return ResponseEntity.ok(resource);
        } catch (IllegalArgumentException e) {
            log.warn("Bad request for saving goal analytics: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            log.error("Unexpected error getting saving goal analytics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    /**
     * Returns the top N categories with the highest expenses for the given owner and period.
     */
    @GetMapping("/categories/ranking")
    @Operation(
            summary = "Get category expense ranking",
            description = "Returns the top N categories with the highest expenses for the given owner and period."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category expense ranking retrieved successfully",
                    content = @Content(schema = @Schema(implementation = CategoryExpenseSummaryResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid parameters")
    })
    public ResponseEntity<List<CategoryExpenseSummaryResource>> getCategoryExpenseRanking(
            @Parameter(in = ParameterIn.QUERY, description = "Owner identifier.", example = "1", required = true)
            @RequestParam String ownerId,
            @Parameter(in = ParameterIn.QUERY, description = "Owner scope.", example = "INDIVIDUAL", required = true,
                    schema = @Schema(allowableValues = {"INDIVIDUAL", "FAMILY"}))
            @RequestParam String ownerType,
            @Parameter(in = ParameterIn.QUERY, description = "Period type.", example = "MONTHLY", required = true,
                    schema = @Schema(allowableValues = {"DAILY", "WEEKLY", "MONTHLY", "ANNUAL"}))
            @RequestParam String periodType,
            @Parameter(in = ParameterIn.QUERY, description = "Period start date.", example = "2026-06-01", required = true)
            @RequestParam LocalDate periodStart,
            @Parameter(in = ParameterIn.QUERY, description = "Period end date.", example = "2026-06-30", required = true)
            @RequestParam LocalDate periodEnd,
            @Parameter(in = ParameterIn.QUERY, description = "Maximum number of categories to return.", example = "5")
            @RequestParam(defaultValue = "5") Integer limit
    ) {
        log.info("GET /api/v1/analytics/categories/ranking - ownerId={}, periodType={}, limit={}",
                ownerId, periodType, limit);
        try {
            var query = new GetCategoryExpenseRankingQuery(
                    ownerId,
                    OwnerTypes.valueOf(ownerType.toUpperCase()),
                    PeriodTypes.valueOf(periodType.toUpperCase()),
                    periodStart,
                    periodEnd,
                    limit
            );
            var ranking = analyticsQueryService.handle(query);
            var resources = ranking.stream()
                    .map(CategoryExpenseSummaryResourceFromEntityAssembler::toResourceFromEntity)
                    .toList();
            return ResponseEntity.ok(resources);
        } catch (IllegalArgumentException e) {
            log.warn("Bad request for category expense ranking: {}", e.getMessage());
            return ResponseEntity.badRequest().body(List.of());
        } catch (Exception e) {
            log.error("Unexpected error getting category expense ranking", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(List.of());
        }
    }

    /**
     * Returns the income vs expense trend for the last N periods.
     */
    @GetMapping("/trend")
    @Operation(
            summary = "Get income vs expense trend",
            description = "Returns the income vs expense trend for the last N periods."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trend retrieved successfully",
                    content = @Content(schema = @Schema(implementation = AnalyticsPeriodTrendResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid parameters")
    })
    public ResponseEntity<List<AnalyticsPeriodTrendResource>> getIncomeVsExpenseTrend(
            @Parameter(in = ParameterIn.QUERY, description = "Owner identifier.", example = "1", required = true)
            @RequestParam String ownerId,
            @Parameter(in = ParameterIn.QUERY, description = "Owner scope.", example = "INDIVIDUAL", required = true,
                    schema = @Schema(allowableValues = {"INDIVIDUAL", "FAMILY"}))
            @RequestParam String ownerType,
            @Parameter(in = ParameterIn.QUERY, description = "Period type.", example = "MONTHLY", required = true,
                    schema = @Schema(allowableValues = {"DAILY", "WEEKLY", "MONTHLY", "ANNUAL"}))
            @RequestParam String periodType,
            @Parameter(in = ParameterIn.QUERY, description = "Number of past periods to include.", example = "6")
            @RequestParam(defaultValue = "6") Integer lastNPeriods
    ) {
        log.info("GET /api/v1/analytics/trend - ownerId={}, periodType={}, lastNPeriods={}",
                ownerId, periodType, lastNPeriods);
        try {
            var query = new GetIncomeVsExpenseTrendQuery(
                    ownerId,
                    OwnerTypes.valueOf(ownerType.toUpperCase()),
                    PeriodTypes.valueOf(periodType.toUpperCase()),
                    lastNPeriods
            );
            var trends = analyticsQueryService.handle(query);
            var resources = trends.stream()
                    .map(AnalyticsPeriodTrendResourceFromEntityAssembler::toResourceFromEntity)
                    .toList();
            return ResponseEntity.ok(resources);
        } catch (IllegalArgumentException e) {
            log.warn("Bad request for income vs expense trend: {}", e.getMessage());
            return ResponseEntity.badRequest().body(List.of());
        } catch (Exception e) {
            log.error("Unexpected error getting income vs expense trend", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(List.of());
        }
    }
}
