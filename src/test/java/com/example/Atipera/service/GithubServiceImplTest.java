package com.example.Atipera.service;

import com.example.Atipera.exception.UserNotFoundException;
import com.example.Atipera.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GithubServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private GithubServiceImpl githubService;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "https://api.github.com";
        githubService = new GithubServiceImpl(restTemplate, baseUrl);
    }

    @Test
    public void shouldReturnRepositoriesForValidUser() {
        String username = "pcygan01";
        
        Owner owner = new Owner(username);
        
        Repository repo1 = Repository.builder()
                .name("repo1")
                .fork(false)
                .owner(owner)
                .build();
        
        Repository repo2 = Repository.builder()
                .name("repo2")
                .fork(true)
                .owner(owner)
                .build();
        
        List<Repository> repositories = Arrays.asList(repo1, repo2);
        
        Commit commit = new Commit("abc123");
        Branch branch = new Branch("main", commit);
        List<Branch> branches = Collections.singletonList(branch);
        
        ResponseEntity<List<Repository>> reposResponse = ResponseEntity.ok(repositories);
        ResponseEntity<List<Branch>> branchesResponse = ResponseEntity.ok(branches);
        
        when(restTemplate.exchange(
                eq(baseUrl + "/users/" + username + "/repos"),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class))
        ).thenReturn(reposResponse);
        
        when(restTemplate.exchange(
                eq(baseUrl + "/repos/" + username + "/repo1/branches"),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class))
        ).thenReturn(branchesResponse);
        
        List<RepositoryResponse> result = githubService.getUserRepositories(username);
        
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("repo1");
        assertThat(result.get(0).getOwner().getLogin()).isEqualTo(username);
        assertThat(result.get(0).getBranches()).hasSize(1);
        assertThat(result.get(0).getBranches().get(0).getName()).isEqualTo("main");
        assertThat(result.get(0).getBranches().get(0).getSha()).isEqualTo("abc123");
    }

    @Test
    public void shouldThrowExceptionForNonExistentUser() {
        String username = "non-existent-user";
        
        when(restTemplate.exchange(
                eq(baseUrl + "/users/" + username + "/repos"),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class))
        ).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));
        
        assertThrows(UserNotFoundException.class, () -> {
            githubService.getUserRepositories(username);
        });
    }
} 