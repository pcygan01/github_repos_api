package com.example.Atipera.service;

import com.example.Atipera.model.RepositoryResponse;

import java.util.List;

public interface GithubService {
    List<RepositoryResponse> getUserRepositories(String username);
} 