package com.branch.service.github.client;

import com.branch.service.github.model.github.RepoResponse;
import com.branch.service.github.model.github.UserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;


@Component
public class GitHubClient {
    // ******** Properties ********
    private static final Logger log = LoggerFactory.getLogger(GitHubClient.class);
    private static final String GITHUB_API_BASE_URL = "https://api.github.com";
    private final RestClient restClient;

    // ******** Constructors ********
    public GitHubClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.baseUrl(GITHUB_API_BASE_URL).build();
    }

    // ******** Domain methods ********
    public UserResponse getUserInfo(String username) {
        log.debug("Calling GitHub API for user info: {}", username);
        try {
            return restClient.get().uri("/users/{username}", username).retrieve().body(UserResponse.class);
        } catch (RestClientException e) {
            log.error("Error fetching user info for: {}", username, e);
            throw new RuntimeException("Failed to fetch user info from GitHub", e);
        }
    }

    public List<RepoResponse> getUserRepos(String username) {
        log.debug("Calling GitHub API for user repos: {}", username);
        try {
            return restClient.get().uri("/users/{username}/repos", username).retrieve().body(new ParameterizedTypeReference<List<RepoResponse>>() {});
        } catch (RestClientException e) {
            log.error("Error fetching repos for: {}", username, e);
            throw new RuntimeException("Failed to fetch repos from GitHub", e);
        }
    }
}