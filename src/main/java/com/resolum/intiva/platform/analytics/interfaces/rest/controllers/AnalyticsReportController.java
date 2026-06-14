package com.resolum.intiva.platform.analytics.interfaces.rest.controllers;

import com.resolum.intiva.platform.analytics.domain.model.exceptions.InvalidReportPeriodException;
import com.resolum.intiva.platform.analytics.domain.model.queries.GetReportPreviewQuery;
import com.resolum.intiva.platform.analytics.domain.services.ReportCommandService;
import com.resolum.intiva.platform.analytics.domain.services.ReportQueryService;
import com.resolum.intiva.platform.analytics.interfaces.rest.assemblers.GenerateReportCommandFromResourceAssembler;
import com.resolum.intiva.platform.analytics.interfaces.rest.assemblers.ReportPreviewResourceFromEntityAssembler;
import com.resolum.intiva.platform.analytics.interfaces.rest.resources.requests.GenerateReportResource;
import com.resolum.intiva.platform.analytics.interfaces.rest.resources.responses.ReportPreviewResource;
import com.resolum.intiva.platform.shared.domain.valueobjects.OwnerTypes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * REST controller exposing report preview and generation endpoints under the
 * {@code /api/v1/analytics/reports} base path.
 *
 * <p>Provides two operations:
 * <ul>
 *   <li><b>GET /summary</b> — returns a JSON preview of the financial report
 *       with aggregated metrics and top categories.</li>
 *   <li><b>POST /generate</b> — generates a CSV or PDF file and returns it as
 *       a downloadable attachment.</li>
 * </ul>
 * All endpoints require authentication via Bearer token.</p>
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/analytics/reports", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Analytics Reports", description = "Endpoints for report preview and generation.")
public class AnalyticsReportController {

    /**
     * Service for computing report preview summaries.
     */
    private final ReportQueryService reportQueryService;

    /**
     * Service for generating downloadable report files.
     */
    private final ReportCommandService reportCommandService;

    /**
     * Creates the controller with its required service dependencies.
     *
     * @param reportQueryService   the report preview query service
     * @param reportCommandService the report generation command service
     */
    public AnalyticsReportController(ReportQueryService reportQueryService,
                                     ReportCommandService reportCommandService) {
        this.reportQueryService = reportQueryService;
        this.reportCommandService = reportCommandService;
    }

    /**
     * Retrieves a financial report preview summary for the given owner and period.
     *
     * <p>The preview is computed on the fly and returns aggregated metrics including
     * total income, total expenses, net balance, transaction count, and the top 5
     * expense categories.</p>
     *
     * @param ownerId     the owner identifier
     * @param ownerType   the owner scope (INDIVIDUAL or FAMILY)
     * @param periodStart the inclusive start date of the period (yyyy-MM-dd)
     * @param periodEnd   the inclusive end date of the period (yyyy-MM-dd)
     * @param categoryId  optional category identifier to filter by
     * @return 200 OK with the report preview, or 400 if parameters are invalid
     */
    @GetMapping("/summary")
    @Operation(summary = "Get report preview summary",
            description = "Returns a financial summary preview for the given owner and period.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Report preview retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ReportPreviewResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid parameters")
    })
    public ResponseEntity<ReportPreviewResource> getReportSummary(
            @Parameter(in = ParameterIn.QUERY, description = "Owner identifier.", example = "1", required = true)
            @RequestParam String ownerId,
            @Parameter(in = ParameterIn.QUERY, description = "Owner scope.", example = "INDIVIDUAL", required = true,
                    schema = @Schema(allowableValues = {"INDIVIDUAL", "FAMILY"}))
            @RequestParam String ownerType,
            @Parameter(in = ParameterIn.QUERY, description = "Period start date.", example = "2026-01-01", required = true)
            @RequestParam LocalDate periodStart,
            @Parameter(in = ParameterIn.QUERY, description = "Period end date.", example = "2026-06-30", required = true)
            @RequestParam LocalDate periodEnd,
            @Parameter(in = ParameterIn.QUERY, description = "Category identifier (optional).", example = "1")
            @RequestParam(required = false) String categoryId
    ) {
        log.info("GET /api/v1/analytics/reports/summary - ownerId={}, ownerType={}, periodStart={}, periodEnd={}",
                ownerId, ownerType, periodStart, periodEnd);

        if (periodStart.isAfter(periodEnd)) {
            throw new InvalidReportPeriodException("Period start must be before period end");
        }

        OwnerTypes ot;
        try {
            ot = OwnerTypes.valueOf(ownerType.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        var query = new GetReportPreviewQuery(ownerId, ot, periodStart, periodEnd, categoryId);
        var preview = reportQueryService.getReportPreview(query);
        var resource = ReportPreviewResourceFromEntityAssembler.toResourceFromEntity(preview);

        return ResponseEntity.ok(resource);
    }

    /**
     * Generates a downloadable report file (CSV or PDF) and returns it as an
     * HTTP attachment.
     *
     * <p>The response includes the appropriate {@code Content-Type} and
     * {@code Content-Disposition} headers so that the browser or API client
     * will download the file automatically.</p>
     *
     * @param resource the report generation parameters
     * @return 200 OK with the file content as a downloadable attachment, or 400
     *         if parameters are invalid
     */
    @PostMapping("/generate")
    @Operation(summary = "Generate a report file",
            description = "Generates a CSV or PDF report for the given owner and period and returns it as a downloadable file.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Report generated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters")
    })
    public ResponseEntity<byte[]> generateReport(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Report generation parameters", required = true)
            @RequestBody GenerateReportResource resource
    ) {
        log.info("POST /api/v1/analytics/reports/generate - ownerId={}, format={}",
                resource.ownerId(), resource.format());

        var command = GenerateReportCommandFromResourceAssembler.toCommandFromResource(resource);
        var report = reportCommandService.generateReport(command);

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(report.getContentType()));
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename(report.getFileName())
                        .build());

        return new ResponseEntity<>(report.getContent(), headers, HttpStatus.OK);
    }
}
