package com.branch.service.github.service;

import com.branch.service.github.client.GitHubClient;
import com.branch.service.github.model.dto.UserProfileResponse;
import com.branch.service.github.model.github.RepoResponse;
import com.branch.service.github.model.github.UserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;


@Service
public class ProfileService {
    // ******** Properties ********
    private static final Logger log = LoggerFactory.getLogger(ProfileService.class);
    private final GitHubClient gitHubClient;

    // ******** Constructor ********
    public ProfileService(GitHubClient gitHubClient) {
        this.gitHubClient = gitHubClient;
    }

    // ******** Domain methods ********
    public UserProfileResponse getUserProfile(String username) {
        log.debug("Fetching profile data for username: {}", username);
        // prepare to call both endpoints asynchronously
        CompletableFuture<UserResponse> userInfoFuture = CompletableFuture.supplyAsync(() -> gitHubClient.getUserInfo(username));
        CompletableFuture<List<RepoResponse>> reposFuture = CompletableFuture.supplyAsync(() -> gitHubClient.getUserRepos(username));

        try {
            // call both. only takes as long as the slowest response
            CompletableFuture.allOf(userInfoFuture, reposFuture).join();
            UserResponse userInfo = userInfoFuture.join();
            List<RepoResponse> repos = reposFuture.join();
            return buildUserProfileResponse(userInfo, repos);
        } catch (Exception e) {
            // if any failure, see which future(s) failed for specific error logging
            if (userInfoFuture.isCompletedExceptionally()) {
                log.error("User info fetch failed for: {}", username, e);
            }
            if (reposFuture.isCompletedExceptionally()) {
                log.error("Repos fetch failed for: {}", username, e);
            }
            throw new RuntimeException("Failed to fetch GitHub profile for user: " + username, e);
        }
    }

    // ******** Private domain methods ********
    private UserProfileResponse buildUserProfileResponse(UserResponse userInfo, List<RepoResponse> repos) {
        UserProfileResponse response = new UserProfileResponse();
        response.setUserName(userInfo.getLogin());
        response.setDisplayName(userInfo.getName());
        response.setAvatar(userInfo.getAvatarUrl());
        response.setGeoLocation(userInfo.getLocation());
        response.setEmail(userInfo.getEmail());
        response.setUrl(userInfo.getUrl());
        response.setCreatedAt(formatDate(userInfo.getCreatedAt()));
        response.setRepos(repos.stream().map(repo -> new UserProfileResponse.RepoInfo(repo.getName(), repo.getUrl())).toList());
        return response;
    }

    private String formatDate(String iso8601Date) {
        if (iso8601Date == null) {
            return null;
        }
        try {
            // parse the ISO 8601 (i.e. - "2011-01-25T18:44:36Z")
            java.time.Instant instant = java.time.Instant.parse(iso8601Date);
            // convert to RFC 1123 (i.e. - "Tue, 25 Jan 2011 18:44:36 GMT")
            return java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME.withZone(java.time.ZoneId.of("GMT")).format(instant);
        } catch (Exception e) {
            log.error("Error converting date format: {}", iso8601Date, e);
            return iso8601Date; // Return original if conversion fails
        }
    }
}
