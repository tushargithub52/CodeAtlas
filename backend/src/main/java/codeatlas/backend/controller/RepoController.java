package codeatlas.backend.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import codeatlas.backend.dto.IndexStatusResponse;
import codeatlas.backend.dto.RepositoryResponse;
import codeatlas.backend.security.CurrentUser;
import codeatlas.backend.service.RepositoryService;
import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping("/api/repos")
@RequiredArgsConstructor 
public class RepoController {

    private final CurrentUser currentUser;
    private final RepositoryService repositoryService;
    
    @GetMapping 
    public List<RepositoryResponse> list(
        @RequestParam(name = "refresh", defaultValue = "true") boolean refresh
    ) {
        UUID userId = currentUser.require().getId();

        if (refresh) {
            return repositoryService.syncRepos(userId);
        }
        
        return repositoryService.listRepos(userId);
    }

    @GetMapping("/{id}")
    public RepositoryResponse get(@PathVariable UUID id) {
        UUID userId = currentUser.require().getId();
        return repositoryService.toResponse(repositoryService.getOwnedRepo(userId, id));
    }

    @GetMapping("/{id}/status")
    public IndexStatusResponse getIndexStatus(@PathVariable UUID id) {
        UUID userId = currentUser.require().getId();
        return repositoryService.getIndexStatus(id, userId);
    }
}
