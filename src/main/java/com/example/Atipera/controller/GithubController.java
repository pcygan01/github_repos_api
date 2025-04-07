package com.example.Atipera.controller;

import com.example.Atipera.model.RepositoryResponse;
import com.example.Atipera.service.GithubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class GithubController {

    private final GithubService githubService;

    @Autowired
    public GithubController(GithubService githubService) {
        this.githubService = githubService;
    }

    @GetMapping("/users/{username}/repositories")
    public ResponseEntity<List<RepositoryResponse>> getUserRepositories(@PathVariable String username) {
        List<RepositoryResponse> repositories = githubService.getUserRepositories(username);
        return ResponseEntity.ok(repositories);
    }
} 