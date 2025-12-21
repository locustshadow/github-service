package com.branch.service.github.controller;

import com.branch.service.github.exception.GitHubApiException;
import com.branch.service.github.exception.UserNotFoundException;
import com.branch.service.github.model.dto.UserProfileResponse;
import com.branch.service.github.service.ProfileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@WebMvcTest(ProfileController.class) // <-- loads the Spring web layer
class ProfileControllerTest {
    @Autowired
    private MockMvcTester mockMvc; // <-- MockMvcTester simulates an HTTP request to test the full web layer without a real server
    @MockitoBean // <-- use spring context
    private ProfileService profileService;

    // ******** Tests ********
    @Test
    void getUserProfile_returnsProfile() {
        // given
        UserProfileResponse profile = createTestProfile();
        when(profileService.getUserProfile("octocat")) // <-- set up the behavior of the method on the mock
            .thenReturn(profile);

        // when/then
        assertThat(mockMvc.get().uri("/api/v1/users/octocat/profile")) // <-- verifies @GetMapping, path var extract, JSON serialization, exception handling, etc.
            .hasStatusOk()
            .bodyJson()
            .extractingPath("$.user_name").isEqualTo("octocat");
    }

    @Test
    void getUserProfile_userNotFound_returns404() {
        // given
        when(profileService.getUserProfile("bogususer"))
            .thenThrow(new UserNotFoundException("bogususer"));

        // when/then
        assertThat(mockMvc.get().uri("/api/v1/users/bogususer/profile"))
            .hasStatus(404)
            .bodyJson()
            .extractingPath("$.message").isEqualTo("User not found: bogususer");
    }

    @Test
    void getUserProfile_gitHubApiError_returns502() {
        // given
        when(profileService.getUserProfile("octocat"))
            .thenThrow(new GitHubApiException("GitHub API failed", new RuntimeException("connection refused")));

        // when/then
        assertThat(mockMvc.get().uri("/api/v1/users/octocat/profile"))
            .hasStatus(502)
            .bodyJson()
            .extractingPath("$.message").isEqualTo("GitHub API error");
    }

    // ******** Helper methods ********
    private UserProfileResponse createTestProfile() {
        UserProfileResponse profile = new UserProfileResponse();
        profile.setUserName("octocat");
        profile.setDisplayName("The Octocat");
        profile.setAvatar("https://avatars.githubusercontent.com/u/583231");
        profile.setGeoLocation("San Francisco");
        profile.setEmail("octocat@github.com");
        profile.setUrl("https://api.github.com/users/octocat");
        profile.setCreatedAt("Tue, 25 Jan 2011 18:44:36 GMT");
        profile.setRepos(List.of(
            new UserProfileResponse.RepoInfo("hello-world", "https://github.com/octocat/hello-world"),
            new UserProfileResponse.RepoInfo("spoon-knife", "https://github.com/octocat/spoon-knife")
        ));
        return profile;
    }
}
