package com.branch.service.github.client;

import com.branch.service.github.exception.GitHubApiException;
import com.branch.service.github.exception.UserNotFoundException;
import com.branch.service.github.model.github.RepoResponse;
import com.branch.service.github.model.github.UserResponse;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;


@Component
public class GitHubClient {
    // ******** Properties ********
    private static final Logger log = LoggerFactory.getLogger(GitHubClient.class);
    private RestClient restClient;
    @Value("${github.api.base-url}")
    private String baseUrl;
    // -- resources --
    @Autowired
    private RestClient.Builder restClientBuilder;

    // ******** Initialization ********
    @PostConstruct
    private void init() {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
    }

    // ******** Domain methods ********
    public UserResponse getUserInfo(String username) {
        log.debug("Calling GitHub API for user info for user [{}]", username);
        try {
            return restClient.get().uri("/users/{username}", username).retrieve().body(UserResponse.class);
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("User [{}] not found on GitHub", username);
            throw new UserNotFoundException(username);
        } catch (RestClientException e) {
            log.error("Error fetching user info for user [{}]", username, e);
            throw new GitHubApiException("Failed to fetch user info from GitHub", e);
        }
    }

    public List<RepoResponse> getUserRepos(String username) {
        log.debug("Calling GitHub API for user repos for user [{}]", username);
        try {
            return restClient.get().uri("/users/{username}/repos", username).retrieve().body(new ParameterizedTypeReference<>() {});
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("User [{}] not found on GitHub", username);
            throw new UserNotFoundException(username);
        } catch (RestClientException e) {
            log.error("Error fetching repos for user [{}]", username, e);
            throw new GitHubApiException("Failed to fetch repos from GitHub", e);
        }
    }
}