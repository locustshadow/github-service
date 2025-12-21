package com.branch.service.github.controller;

import com.branch.service.github.model.dto.ErrorResponse;
import com.branch.service.github.model.dto.UserProfileResponse;
import com.branch.service.github.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "Profile", description = "GitHub user profile operations")
@RestController
@RequestMapping("/api/v1")
public class ProfileController {
    // ******** Properties ********
    private static final Logger log = LoggerFactory.getLogger(ProfileController.class);
    // -- resources --
    @Autowired
    private ProfileService profileService;

    // ******** Domain methods ********
    @Operation(summary = "Get user profile", description = "Fetches GitHub user info and repository list")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Profile found", content = @Content(schema = @Schema(implementation = UserProfileResponse.class))),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "502", description = "GitHub API error", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/users/{username}/profile")
    public ResponseEntity<UserProfileResponse> getUserProfile(@Parameter(description = "GitHub username") @PathVariable String username) {
        log.debug("Received request for profile for user [{}]", username);
        UserProfileResponse profile = profileService.getUserProfile(username);
        log.debug("Successfully retrieved profile for user [{}]", username);
        return ResponseEntity.ok(profile);
    }
}
