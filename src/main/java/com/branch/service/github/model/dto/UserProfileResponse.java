package com.branch.service.github.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Setter
@Getter
public class UserProfileResponse {
    // ******** Properties ********
    @JsonProperty("user_name")
    private String userName;
    @JsonProperty("display_name")
    private String displayName;
    private String avatar;
    @JsonProperty("geo_location")
    private String geoLocation;
    private String email;
    private String url;
    @JsonProperty("created_at")
    private String createdAt;
    private List<RepoInfo> repos;

    // ******** Synthetic properties ********
    @JsonProperty("repo_count")
    public int getRepoCount() {
        return (repos != null) ? repos.size() : 0;
    }

    // ******** Inner classes ********
    @Setter
    @Getter
    public static class RepoInfo {
        // ******** Properties ********
        private String name;
        private String url;

        // ******** Constructors *******
        public RepoInfo() {}
        public RepoInfo(String name, String url) {
            this.name = name;
            this.url = url;
        }
    }
}