package com.branch.service.github.controller;

import com.branch.service.github.model.dto.UserProfileResponse;
import com.branch.service.github.service.ProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1")
public class ProfileController {
    // ******** Properties ********
    private static final Logger log = LoggerFactory.getLogger(ProfileController.class);
    // -- resources --
    @Autowired
    private ProfileService profileService;

    // ******** Domain methods ********
    @GetMapping("/users/{username}/profile")
    public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable String username) {
        // NOTE: could change the logging to debug level if we don't want this filling up the logs
        log.info("Received request for profile for user [{}]", username);
        UserProfileResponse profile = profileService.getUserProfile(username);
        log.info("Successfully retrieved profile for user: {}", username);
        return ResponseEntity.ok(profile);
    }
}
