package com.resolum.intiva.platform.iam.interfaces.rest.controllers;

import com.resolum.intiva.platform.iam.domain.services.UserCommandService;
import com.resolum.intiva.platform.iam.interfaces.rest.assemblers.AuthenticatedUserResourceFromEntityAssembler;
import com.resolum.intiva.platform.iam.interfaces.rest.assemblers.SignInCommandFromResourceAssembler;
import com.resolum.intiva.platform.iam.interfaces.rest.assemblers.SignUpCommandFromResourceAssembler;
import com.resolum.intiva.platform.iam.interfaces.rest.assemblers.UserResourceFromEntityAssembler;
import com.resolum.intiva.platform.iam.interfaces.rest.resources.requests.SignInResource;
import com.resolum.intiva.platform.iam.interfaces.rest.resources.requests.SignUpResource;
import com.resolum.intiva.platform.iam.interfaces.rest.resources.responses.AuthenticatedUserResource;
import com.resolum.intiva.platform.iam.interfaces.rest.resources.responses.UserResource;
import com.resolum.intiva.platform.shared.interfaces.rest.resource.MessageResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AuthenticationController is a REST controller that handles authentication-related endpoints, such as user registration (sign-up). It defines the API endpoints for authentication operations and uses the UserCommandService to perform the necessary business logic for user registration.
 *
 * <p>
 *      This controller is responsible for handling authentication-related requests.
 *      It exposes two endpoints:
 *      <ul>
 *          <li>POST /api/v1/authentication/sign-in</li>
 *          <li>POST /api/v1/authentication/sign-up</li>
 *      </ul>
 * </p>
 */
@RestController
@RequestMapping(value = "/api/v1/authentication", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Authentication", description = "Available Authentication Endpoints")
public class AuthenticationController {

    // UserCommandService is a service that handles user-related commands such as sign-up. It is injected into the controller to perform the necessary business logic for user registration.
    private final UserCommandService userCommandService;

    // Constructor injection for the UserCommandService dependency
    public AuthenticationController(UserCommandService userCommandService) {
        this.userCommandService = userCommandService;
    }

    /**
     * Endpoint to register a new user in the system. It accepts user registration details and creates a new user account if the provided information is valid.
     * @param resource The SignUpResource object containing the user registration details (e.g., email and password) sent in the request body.
     * @return A ResponseEntity containing the created UserResource if the registration is successful, or an appropriate error response if the registration fails (e.g., due to invalid input data or existing user).
     */
    @PostMapping(value = "/sign-up", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Register a new user",
            description = "Endpoint to register a new user in the system. It accepts user registration details and creates a new user account if the provided information is valid."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User created successfully.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserResource.class),
                            examples = @ExampleObject(
                                    name = "Successful user sign up",
                                    summary = "An example of a successful user registration",
                                    value = """
                                            {
                                                "id": "23145535s12345",
                                                "email": "test@gmail.com"
                                            }
                                            """,
                                    description = "Response contains the user id and the username of the registered user."
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data or user already exists"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    public ResponseEntity<?> signUp(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Data required to register a new user.",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SignUpResource.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Good user registration",
                                            summary = "A valid user registration example",
                                            value = """
                                                    {
                                                        "email": "test@gmail.com",
                                                        "password": "P@ssw0rd!"
                                                    }
                                                    """,
                                            description = "A valid user registration example."
                                    ),
                                    @ExampleObject(
                                            name = "Bad user registration",
                                            summary = "An invalid user registration example",
                                            value = """
                                                    {
                                                        "email": "hello_world",
                                                        "password": "123456"
                                                    }
                                                    """,
                                            description = "Username (email) has an invalid format and password does not meet security requirements."
                                    )
                            }
                    )
            )
            @RequestBody SignUpResource resource
    ) {
        try {
            var signUpCommand = SignUpCommandFromResourceAssembler.toCommandFromResource(resource);
            var user = userCommandService.handle(signUpCommand);
            if (user.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            var userResource = UserResourceFromEntityAssembler.toResourceFromEntity(user.get());
            return new ResponseEntity<>(userResource, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResource(e.getMessage()));
        }
    }

    /**
     * Endpoint to authenticate a user and return a JWT token if the provided credentials are valid.
     * @param resource the SignInResource object containing the user's email and password.
     * @return a ResponseEntity containing the AuthenticatedUserResource if the authentication is successful, or an appropriate error response if the authentication fails (e.g., invalid credentials).
     */
    @PostMapping(value = "/sign-in", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Sign in user",
            description = "Authenticates a user and returns a JWT token if credentials are valid"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User authenticated successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AuthenticatedUserResource.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Successful user sign in",
                                            summary = "An example of a successful user authentication",
                                            value = """
                                            {
                                                "userId": 1,
                                                "email": "farid@gmail.com",
                                                "token": "eyJhbGciOi"
                                            }
                                            """,
                                            description = "Response contains the user id, email and the JWT token of the authenticated user."),
                                    @ExampleObject(
                                            name = "Failed user sign in",
                                            summary = "An example of a failed user authentication",
                                            value = """
                                            {
                                                "error": "Invalid credentials"
                                            }
                                            """,
                                            description = "Response contains an error message indicating that the provided credentials are invalid."
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found or invalid credentials"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    public ResponseEntity<?> signIn(
            @RequestBody SignInResource resource
    ) {
        try {
            var signInCommand =
                    SignInCommandFromResourceAssembler.toCommandFromResource(resource);

            var authenticatedUser = userCommandService.handle(signInCommand);

            if (authenticatedUser.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            var authenticatedUserResource = AuthenticatedUserResourceFromEntityAssembler.toResourceFromEntity(
                    authenticatedUser.get().getLeft(),
                    authenticatedUser.get().getRight()
            );

            return new ResponseEntity<>(authenticatedUserResource, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResource(e.getMessage()));
        }
    }
}
