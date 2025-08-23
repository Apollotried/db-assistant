package com.marouane.db_assistant.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user registration and login")
public class AuthenticationController {
    private final AuthenticationService service;

    @PostMapping(value = "/register", produces = "application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(
            summary = "Register a new user",
            description = "Accepts registration details and creates a new account.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Registration details including firstName, lastName, email, and password",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "firstName": "John",
                                              "lastName": "Doe",
                                              "email": "john.doe@example.com",
                                              "password": "strongPassword123"
                                            }
                                            """
                            )
                    )

            )
    )
    public ResponseEntity<?> register(
            @RequestBody @Valid RegistrationRequest request
    )  {
        service.register(request);
        return ResponseEntity.accepted().build();
    }


    @PostMapping(value = "/authenticate", produces = "application/json")
    @Operation(
            summary = "Authenticate a user",
            description = "Validates credentials and returns a JWT token on success.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User credentials including email and password",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "email": "john.doe@example.com",
                                              "password": "strongPassword123"
                                            }
                                            """
                            )
                    )
            )

    )
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody @Valid AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }

}
