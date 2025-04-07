package com.example.Atipera.controller;

import com.example.Atipera.exception.UserNotFoundException;
import com.example.Atipera.model.BranchResponse;
import com.example.Atipera.model.Owner;
import com.example.Atipera.model.RepositoryResponse;
import com.example.Atipera.service.GithubService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GithubController.class)
@Import(RestTemplateAutoConfiguration.class)
public class GithubControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GithubService githubService;

    @Test
    public void shouldReturnRepositoriesForValidUser() throws Exception {
        String username = "pcygan01";
        Owner owner = new Owner(username);
        List<BranchResponse> branches = Arrays.asList(
                new BranchResponse("main", "abc123"),
                new BranchResponse("dev", "def456")
        );
        RepositoryResponse repo = new RepositoryResponse("repo1", owner, branches);
        List<RepositoryResponse> repositories = Collections.singletonList(repo);

        when(githubService.getUserRepositories(username)).thenReturn(repositories);

        mockMvc.perform(get("/api/users/{username}/repositories", username)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("repo1")))
                .andExpect(jsonPath("$[0].owner.login", is(username)))
                .andExpect(jsonPath("$[0].branches", hasSize(2)))
                .andExpect(jsonPath("$[0].branches[0].name", is("main")))
                .andExpect(jsonPath("$[0].branches[0].sha", is("abc123")))
                .andExpect(jsonPath("$[0].branches[1].name", is("dev")))
                .andExpect(jsonPath("$[0].branches[1].sha", is("def456")));
    }

    @Test
    public void shouldReturn404ForNonExistentUser() throws Exception {
        String username = "non-existent-user";
        when(githubService.getUserRepositories(username))
                .thenThrow(new UserNotFoundException("User not found: " + username));

        mockMvc.perform(get("/api/users/{username}/repositories", username)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("User not found: " + username)));
    }
} 