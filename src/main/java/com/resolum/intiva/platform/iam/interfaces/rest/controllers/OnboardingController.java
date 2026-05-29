package com.resolum.intiva.platform.iam.interfaces.rest.controllers;

import com.resolum.intiva.platform.iam.domain.model.queries.GetOnboardingStatusQuery;
import com.resolum.intiva.platform.iam.domain.services.OnboardingCommandService;
import com.resolum.intiva.platform.iam.domain.services.OnboardingQueryService;
import com.resolum.intiva.platform.iam.interfaces.rest.assemblers.AdvanceTutorialStepCommandFromResourceAssembler;
import com.resolum.intiva.platform.iam.interfaces.rest.assemblers.OnboardingStatusResourceFromEntityAssembler;
import com.resolum.intiva.platform.iam.interfaces.rest.resources.requests.AdvanceOnboardingProcessResource;
import com.resolum.intiva.platform.iam.interfaces.rest.resources.responses.OnboardingStatusResource;
import com.resolum.intiva.platform.shared.interfaces.rest.resource.MessageResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * OnboardingController is a REST controller that manages user onboarding processes.
 * It provides endpoints for advancing the onboarding process for users.
 */
@RestController
@RequestMapping("/api/v1/onboardings")
@Tag(name = "Onboarding", description = "Endpoints for managing user onboarding")
public class OnboardingController {

    /**
     * OnboardingCommandService is a service that handles commands related to the onboarding process.
     * It encapsulates the business logic for advancing the onboarding steps and other related operations.
     */
    private final OnboardingCommandService onboardingCommandService;

    /**
     * OnboardingQueryService is a service that handles queries related to the onboarding process.
     * It provides methods for retrieving onboarding status and other related information.
     */
    private final OnboardingQueryService onboardingQueryService;

    /**
     * Constructor for OnboardingController.
     *
     * @param onboardingCommandService the service that handles onboarding commands
     */
    public OnboardingController(OnboardingCommandService onboardingCommandService, OnboardingQueryService onboardingQueryService) {
        this.onboardingCommandService = onboardingCommandService;
        this.onboardingQueryService = onboardingQueryService;
    }

    /**
     * Endpoint to retrieve the onboarding status for a user.
     *
     * @param userId the ID of the user for whom to retrieve the onboarding status
     * @return a response entity containing the onboarding status resource if found, or a 404 not found response if not found
     */
    @GetMapping("/status")
    @Operation(
            summary = "Get onboarding status",
            description = "Endpoint to retrieve the onboarding status for a user. This will return the current step of the onboarding process and any relevant information about the user's progress."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Onboarding status retrieved successfully"
    )
    public ResponseEntity<OnboardingStatusResource> getOnboardingStatus(
            @RequestParam Long userId) {
        var getOnboardingStatusQuery = new GetOnboardingStatusQuery(userId);
        var onboarding = onboardingQueryService.handle(getOnboardingStatusQuery);
        if (onboarding.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var onboardingResource = OnboardingStatusResourceFromEntityAssembler.fromEntityToResource(onboarding.get());
        return ResponseEntity.ok(onboardingResource);
    }

    /**
     * Endpoint to advance the onboarding process for a user.
     *
     * @param resource the resource containing the user ID for which to advance the onboarding process
     * @return a response entity with a success message if the onboarding process was advanced successfully
     */
    @PatchMapping("/advances")
    @Operation(
            summary = "Advance onboarding process",
            description = "Endpoint to advance the onboarding process for a user. This will move the user to the next step in the onboarding flow."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Onboarding process advanced successfully"
    )
    public ResponseEntity<?> advanceOnboarding(
            @RequestBody AdvanceOnboardingProcessResource resource
    ) {
        var advanceOnboardingStepCommand = AdvanceTutorialStepCommandFromResourceAssembler.toCommandFromResource(resource);
        onboardingCommandService.handle(advanceOnboardingStepCommand);
        return ResponseEntity.status(HttpStatus.OK).body(
                new MessageResource(
                        "Onboarding process advanced successfully"
                )
        );
    }
}
