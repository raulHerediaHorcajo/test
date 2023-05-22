package com.example.demo.security;

import com.example.demo.exception.ErrorInfo;
import com.example.demo.security.jwt.AuthService;
import com.example.demo.security.jwt.dto.AuthResponse;
import com.example.demo.security.jwt.dto.LoginRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "API related to all aspects about the Authentication")
@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    @Autowired
    private AuthService authService;

    public AuthRestController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Login to API")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Login successfully",
                                        content = { @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = AuthResponse.class))}),
                            @ApiResponse(responseCode = "400", description = "Invalid username or password",
                                        content = { @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorInfo.class))}),
                            @ApiResponse(responseCode = "401", description = "Unauthorized",
                                        content = @Content)
    })
    @Parameters(value = {@Parameter(name = "AccessToken",
                                    description = "Access token used to authenticate and authorize users in the API. " +
                                        "It is generated after a user has logged in and has been successfully authenticated."),
                        @Parameter(name = "RefreshToken",
                                    description = "Token used to request a new AccessToken once the current AccessToken " +
                                        "has expired or is about to expire.")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @CookieValue(name = "AccessToken", required = false) String accessToken,
            @CookieValue(name = "RefreshToken", required = false) String refreshToken,
            @Valid @RequestBody LoginRequest loginRequest
    ) {
        return authService.login(loginRequest, accessToken, refreshToken);
    }

    @Operation(summary = "Refresh the access token")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Access token refreshed",
                                        content = { @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = AuthResponse.class))})
    })
    @Parameter(name = "RefreshToken",
                description = "Token used to request a new AccessToken once the current AccessToken " +
                    "has expired or is about to expire.")
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            @CookieValue(name = "RefreshToken", required = false) String refreshToken
    ) {
        return authService.refresh(refreshToken);
    }

    @Operation(summary = "Logout to API")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Logout successfully",
                                        content = { @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = AuthResponse.class))})
    })
    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(HttpServletRequest request) {
        return authService.logout(request);
    }
}
