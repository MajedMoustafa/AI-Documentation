package ai.documentation.service;

import ai.documentation.domain.Codebas;
import ai.documentation.domain.PullRequest;
import ai.documentation.domain.User;
import ai.documentation.events.BeforeDeleteCodebas;
import ai.documentation.events.BeforeDeleteUser;
import ai.documentation.model.PullRequestDTO;
import ai.documentation.repos.CodebasRepository;
import ai.documentation.repos.PullRequestRepository;
import ai.documentation.repos.UserRepository;
import ai.documentation.util.NotFoundException;
import ai.documentation.util.ReferencedException;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class PullRequestService {

    private final PullRequestRepository pullRequestRepository;
    private final CodebasRepository codebasRepository;
    private final UserRepository userRepository;

    public PullRequestService(final PullRequestRepository pullRequestRepository,
            final CodebasRepository codebasRepository, final UserRepository userRepository) {
        this.pullRequestRepository = pullRequestRepository;
        this.codebasRepository = codebasRepository;
        this.userRepository = userRepository;
    }

    public List<PullRequestDTO> findAll() {
        final List<PullRequest> pullRequests = pullRequestRepository.findAll(Sort.by("id"));
        return pullRequests.stream()
                .map(pullRequest -> mapToDTO(pullRequest, new PullRequestDTO()))
                .toList();
    }

    public PullRequestDTO get(final Long id) {
        return pullRequestRepository.findById(id)
                .map(pullRequest -> mapToDTO(pullRequest, new PullRequestDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final PullRequestDTO pullRequestDTO) {
        final PullRequest pullRequest = new PullRequest();
        mapToEntity(pullRequestDTO, pullRequest);
        return pullRequestRepository.save(pullRequest).getId();
    }

    public void update(final Long id, final PullRequestDTO pullRequestDTO) {
        final PullRequest pullRequest = pullRequestRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(pullRequestDTO, pullRequest);
        pullRequestRepository.save(pullRequest);
    }

    public void delete(final Long id) {
        final PullRequest pullRequest = pullRequestRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        pullRequestRepository.delete(pullRequest);
    }

    private PullRequestDTO mapToDTO(final PullRequest pullRequest,
            final PullRequestDTO pullRequestDTO) {
        pullRequestDTO.setId(pullRequest.getId());
        pullRequestDTO.setTitle(pullRequest.getTitle());
        pullRequestDTO.setDescription(pullRequest.getDescription());
        pullRequestDTO.setStatus(pullRequest.getStatus());
        pullRequestDTO.setCreatedAt(pullRequest.getCreatedAt());
        pullRequestDTO.setCodebase(pullRequest.getCodebase() == null ? null : pullRequest.getCodebase().getId());
        pullRequestDTO.setCreatedBy(pullRequest.getCreatedBy() == null ? null : pullRequest.getCreatedBy().getId());
        return pullRequestDTO;
    }

    private PullRequest mapToEntity(final PullRequestDTO pullRequestDTO,
            final PullRequest pullRequest) {
        pullRequest.setTitle(pullRequestDTO.getTitle());
        pullRequest.setDescription(pullRequestDTO.getDescription());
        pullRequest.setStatus(pullRequestDTO.getStatus());
        pullRequest.setCreatedAt(pullRequestDTO.getCreatedAt());
        final Codebas codebase = pullRequestDTO.getCodebase() == null ? null : codebasRepository.findById(pullRequestDTO.getCodebase())
                .orElseThrow(() -> new NotFoundException("codebase not found"));
        pullRequest.setCodebase(codebase);
        final User createdBy = pullRequestDTO.getCreatedBy() == null ? null : userRepository.findById(pullRequestDTO.getCreatedBy())
                .orElseThrow(() -> new NotFoundException("createdBy not found"));
        pullRequest.setCreatedBy(createdBy);
        return pullRequest;
    }

    @EventListener(BeforeDeleteCodebas.class)
    public void on(final BeforeDeleteCodebas event) {
        final ReferencedException referencedException = new ReferencedException();
        final PullRequest codebasePullRequest = pullRequestRepository.findFirstByCodebaseId(event.getId());
        if (codebasePullRequest != null) {
            referencedException.setKey("codebas.pullRequest.codebase.referenced");
            referencedException.addParam(codebasePullRequest.getId());
            throw referencedException;
        }
    }

    @EventListener(BeforeDeleteUser.class)
    public void on(final BeforeDeleteUser event) {
        final ReferencedException referencedException = new ReferencedException();
        final PullRequest createdByPullRequest = pullRequestRepository.findFirstByCreatedById(event.getId());
        if (createdByPullRequest != null) {
            referencedException.setKey("user.pullRequest.createdBy.referenced");
            referencedException.addParam(createdByPullRequest.getId());
            throw referencedException;
        }
    }

}
