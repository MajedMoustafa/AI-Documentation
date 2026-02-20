package ai.documentation.service;

import ai.documentation.domain.Codebas;
import ai.documentation.domain.Commit;
import ai.documentation.domain.User;
import ai.documentation.events.BeforeDeleteCodebas;
import ai.documentation.events.BeforeDeleteUser;
import ai.documentation.model.CommitDTO;
import ai.documentation.repos.CodebasRepository;
import ai.documentation.repos.CommitRepository;
import ai.documentation.repos.UserRepository;
import ai.documentation.util.NotFoundException;
import ai.documentation.util.ReferencedException;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class CommitService {

    private final CommitRepository commitRepository;
    private final CodebasRepository codebasRepository;
    private final UserRepository userRepository;

    public CommitService(final CommitRepository commitRepository,
            final CodebasRepository codebasRepository, final UserRepository userRepository) {
        this.commitRepository = commitRepository;
        this.codebasRepository = codebasRepository;
        this.userRepository = userRepository;
    }

    public List<CommitDTO> findAll() {
        final List<Commit> commits = commitRepository.findAll(Sort.by("id"));
        return commits.stream()
                .map(commit -> mapToDTO(commit, new CommitDTO()))
                .toList();
    }

    public CommitDTO get(final Long id) {
        return commitRepository.findById(id)
                .map(commit -> mapToDTO(commit, new CommitDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final CommitDTO commitDTO) {
        final Commit commit = new Commit();
        mapToEntity(commitDTO, commit);
        return commitRepository.save(commit).getId();
    }

    public void update(final Long id, final CommitDTO commitDTO) {
        final Commit commit = commitRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(commitDTO, commit);
        commitRepository.save(commit);
    }

    public void delete(final Long id) {
        final Commit commit = commitRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        commitRepository.delete(commit);
    }

    private CommitDTO mapToDTO(final Commit commit, final CommitDTO commitDTO) {
        commitDTO.setId(commit.getId());
        commitDTO.setMessage(commit.getMessage());
        commitDTO.setCreatedAt(commit.getCreatedAt());
        commitDTO.setChanges(commit.getChanges());
        commitDTO.setCodebase(commit.getCodebase() == null ? null : commit.getCodebase().getId());
        commitDTO.setCreatedBy(commit.getCreatedBy() == null ? null : commit.getCreatedBy().getId());
        return commitDTO;
    }

    private Commit mapToEntity(final CommitDTO commitDTO, final Commit commit) {
        commit.setMessage(commitDTO.getMessage());
        commit.setCreatedAt(commitDTO.getCreatedAt());
        commit.setChanges(commitDTO.getChanges());
        final Codebas codebase = commitDTO.getCodebase() == null ? null : codebasRepository.findById(commitDTO.getCodebase())
                .orElseThrow(() -> new NotFoundException("codebase not found"));
        commit.setCodebase(codebase);
        final User createdBy = commitDTO.getCreatedBy() == null ? null : userRepository.findById(commitDTO.getCreatedBy())
                .orElseThrow(() -> new NotFoundException("createdBy not found"));
        commit.setCreatedBy(createdBy);
        return commit;
    }

    @EventListener(BeforeDeleteCodebas.class)
    public void on(final BeforeDeleteCodebas event) {
        final ReferencedException referencedException = new ReferencedException();
        final Commit codebaseCommit = commitRepository.findFirstByCodebaseId(event.getId());
        if (codebaseCommit != null) {
            referencedException.setKey("codebas.commit.codebase.referenced");
            referencedException.addParam(codebaseCommit.getId());
            throw referencedException;
        }
    }

    @EventListener(BeforeDeleteUser.class)
    public void on(final BeforeDeleteUser event) {
        final ReferencedException referencedException = new ReferencedException();
        final Commit createdByCommit = commitRepository.findFirstByCreatedById(event.getId());
        if (createdByCommit != null) {
            referencedException.setKey("user.commit.createdBy.referenced");
            referencedException.addParam(createdByCommit.getId());
            throw referencedException;
        }
    }

}
