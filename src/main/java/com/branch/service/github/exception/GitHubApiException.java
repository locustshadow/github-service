package com.branch.service.github.exception;


public class GitHubApiException extends RuntimeException {
    public GitHubApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
