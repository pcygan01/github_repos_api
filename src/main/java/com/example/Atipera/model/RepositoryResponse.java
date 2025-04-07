package com.example.Atipera.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepositoryResponse {
    private String name;
    private Owner owner;
    private List<BranchResponse> branches;
} 