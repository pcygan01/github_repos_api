package com.example.Atipera.service;

import com.example.Atipera.exception.UserNotFoundException;
import com.example.Atipera.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GithubServiceImpl implements GithubService {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    @Autowired
    public GithubServiceImpl(RestTemplate restTemplate, @Value("${github.api.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    @Override
    public List<RepositoryResponse> getUserRepositories(String username) {
        try {
            List<Repository> repositories = fetchUserRepositories(username);
            
            return repositories.stream()
                    .filter(repo -> !repo.isFork())
                    .map(repo -> {
                        List<Branch> branches = fetchRepositoryBranches(username, repo.getName());
                        
                        List<BranchResponse> branchResponses = branches.stream()
                                .map(branch -> new BranchResponse(branch.getName(), branch.getCommit().getSha()))
                                .collect(Collectors.toList());
                        
                        return new RepositoryResponse(repo.getName(), repo.getOwner(), branchResponses);
                    })
                    .collect(Collectors.toList());
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new UserNotFoundException("User not found: " + username);
            }
            throw e;
        }
    }

    private List<Repository> fetchUserRepositories(String username) {
        String url = baseUrl + "/users/" + username + "/repos";
        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Repository>>() {}
        ).getBody();
    }

    private List<Branch> fetchRepositoryBranches(String username, String repositoryName) {
        String url = baseUrl + "/repos/" + username + "/" + repositoryName + "/branches";
        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Branch>>() {}
        ).getBody();
    }
} 