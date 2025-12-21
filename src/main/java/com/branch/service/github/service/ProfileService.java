package com.branch.service.github.service;

import com.branch.service.github.client.GitHubClient;
import com.branch.service.github.exception.GitHubApiException;
import com.branch.service.github.exception.UserNotFoundException;
import com.branch.service.github.model.dto.UserProfileResponse;
import com.branch.service.github.model.github.RepoResponse;
import com.branch.service.github.model.github.UserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;


@Service
public class ProfileService {
    // ******** Properties ********
    private static final Logger log = LoggerFactory.getLogger(ProfileService.class);
    // -- resources --
    @Autowired
    private GitHubClient gitHubClient;

    // ******** Domain methods ********
    // NOTE: if this got a lot of traffic, and we knew the data didn't change often, we could add caching here
    // @Cacheable(value = "userProfiles", key = "#username") // TTL configured via cache provider (e.g., Caffeine: expireAfterWrite=60s)
    public UserProfileResponse getUserProfile(String username) {
        log.debug("fetching profile data for user [{}]", username);

        // prepare to call both GitHub endpoints asynchronously
        CompletableFuture<UserResponse> userInfoFuture = CompletableFuture.supplyAsync(() -> gitHubClient.getUserInfo(username));
        CompletableFuture<List<RepoResponse>> reposFuture = CompletableFuture.supplyAsync(() -> gitHubClient.getUserRepos(username));

        try {
            // call both. only takes as long as the slowest response
            CompletableFuture.allOf(userInfoFuture, reposFuture).join();
            // get responses
            UserResponse userInfo = userInfoFuture.join();
            List<RepoResponse> repos = reposFuture.join();
            // aggregate results
            return buildUserProfileResponse(userInfo, repos);
        } catch (CompletionException e) {
            // handle various causes so all errors aren't just 500
            Throwable cause = e.getCause();
            if (cause instanceof UserNotFoundException) {
                throw (UserNotFoundException) cause;
            }
            if (cause instanceof GitHubApiException) {
                throw (GitHubApiException) cause;
            }
            log.error("failed to fetch GitHub profile for user [{}]", username, e);
            throw new GitHubApiException("failed to fetch GitHub profile for user [" + username + "]", cause);
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
            log.error("unable to convert date string [{}] to RFC 1123 format", iso8601Date, e);
            return iso8601Date;
        }
    }
}
