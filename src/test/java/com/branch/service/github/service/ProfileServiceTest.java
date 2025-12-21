package com.branch.service.github.service;

import com.branch.service.github.client.GitHubClient;
import com.branch.service.github.exception.GitHubApiException;
import com.branch.service.github.exception.UserNotFoundException;
import com.branch.service.github.model.dto.UserProfileResponse;
import com.branch.service.github.model.github.RepoResponse;
import com.branch.service.github.model.github.UserResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {
    @Mock
    private GitHubClient gitHubClient;
    @InjectMocks
    private ProfileService profileService; // <-- class under test

    // ******** Tests ********
    @Test
    void getUserProfile_returnsAggregatedProfile() {
        // given
        UserResponse userResponse = createTestUserResponse();
        List<RepoResponse> repoResponses = createTestRepoResponses();
        when(gitHubClient.getUserInfo("octocat")).thenReturn(userResponse);
        when(gitHubClient.getUserRepos("octocat")).thenReturn(repoResponses);

        // when
        UserProfileResponse result = profileService.getUserProfile("octocat");

        // then
        assertThat(result.getUserName()).isEqualTo("octocat");
        assertThat(result.getDisplayName()).isEqualTo("The Octocat");
        assertThat(result.getAvatar()).isEqualTo("https://avatars.githubusercontent.com/u/583231");
        assertThat(result.getGeoLocation()).isEqualTo("San Francisco");
        assertThat(result.getEmail()).isEqualTo("octocat@github.com");
        assertThat(result.getRepos()).hasSize(2);
        assertThat(result.getRepoCount()).isEqualTo(2);
    }

    @Test
    void getUserProfile_formatsDateToRfc1123() {
        // given
        UserResponse userResponse = createTestUserResponse();
        userResponse.setCreatedAt("2011-01-25T18:44:36Z");
        when(gitHubClient.getUserInfo("octocat")).thenReturn(userResponse);
        when(gitHubClient.getUserRepos("octocat")).thenReturn(List.of());

        // when
        UserProfileResponse result = profileService.getUserProfile("octocat");

        // then
        assertThat(result.getCreatedAt()).isEqualTo("Tue, 25 Jan 2011 18:44:36 GMT");
    }

    @Test
    void getUserProfile_userNotFound_throwsUserNotFoundException() {
        // given
        when(gitHubClient.getUserInfo("bogususer"))
            .thenThrow(new UserNotFoundException("bogususer"));

        // when/then
        assertThatThrownBy(() -> profileService.getUserProfile("bogususer"))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessageContaining("bogususer");
    }

    @Test
    void getUserProfile_gitHubApiError_throwsGitHubApiException() {
        // given
        when(gitHubClient.getUserInfo("octocat"))
            .thenThrow(new GitHubApiException("API error", new RuntimeException("connection refused")));

        // when/then
        assertThatThrownBy(() -> profileService.getUserProfile("octocat"))
            .isInstanceOf(GitHubApiException.class);
    }

    // ******** Helper methods ********
    private UserResponse createTestUserResponse() {
        UserResponse user = new UserResponse();
        user.setLogin("octocat");
        user.setName("The Octocat");
        user.setAvatarUrl("https://avatars.githubusercontent.com/u/583231");
        user.setLocation("San Francisco");
        user.setEmail("octocat@github.com");
        user.setUrl("https://api.github.com/users/octocat");
        user.setCreatedAt("2011-01-25T18:44:36Z");
        return user;
    }

    private List<RepoResponse> createTestRepoResponses() {
        RepoResponse repo1 = new RepoResponse();
        repo1.setName("hello-world");
        repo1.setUrl("https://github.com/octocat/hello-world");

        RepoResponse repo2 = new RepoResponse();
        repo2.setName("spoon-knife");
        repo2.setUrl("https://github.com/octocat/spoon-knife");

        return List.of(repo1, repo2);
    }
}
