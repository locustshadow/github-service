package com.branch.service.github.model.github;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class UserResponse {
    // ******** Properties ********
    private String login;
    private String name;
    @JsonProperty("avatar_url")
    private String avatarUrl;
    private String bio;
    private String location;
    private String email;
    private String url;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("public_repos")
    private int publicRepos;
    private int followers;
    private int following;
}