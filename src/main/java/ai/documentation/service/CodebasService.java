package ai.documentation.service;

import ai.documentation.domain.Codebas;
import ai.documentation.domain.Project;
import ai.documentation.domain.User;
import ai.documentation.events.BeforeDeleteCodebas;
import ai.documentation.events.BeforeDeleteProject;
import ai.documentation.events.BeforeDeleteUser;
import ai.documentation.model.CodebasDTO;
import ai.documentation.repos.CodebasRepository;
import ai.documentation.repos.ProjectRepository;
import ai.documentation.repos.UserRepository;
import ai.documentation.util.NotFoundException;
import ai.documentation.util.ReferencedException;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class CodebasService {

    private final CodebasRepository codebasRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher publisher;

    public CodebasService(final CodebasRepository codebasRepository,
            final ProjectRepository projectRepository, final UserRepository userRepository,
            final ApplicationEventPublisher publisher) {
        this.codebasRepository = codebasRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.publisher = publisher;
    }

    public List<CodebasDTO> findAll() {
        final List<Codebas> codebases = codebasRepository.findAll(Sort.by("id"));
        return codebases.stream()
                .map(codebas -> mapToDTO(codebas, new CodebasDTO()))
                .toList();
    }

    public CodebasDTO get(final Long id) {
        return codebasRepository.findById(id)
                .map(codebas -> mapToDTO(codebas, new CodebasDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final CodebasDTO codebasDTO) {
        final Codebas codebas = new Codebas();
        mapToEntity(codebasDTO, codebas);
        return codebasRepository.save(codebas).getId();
    }

    public void update(final Long id, final CodebasDTO codebasDTO) {
        final Codebas codebas = codebasRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(codebasDTO, codebas);
        codebasRepository.save(codebas);
    }

    public void delete(final Long id) {
        final Codebas codebas = codebasRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteCodebas(id));
        codebasRepository.delete(codebas);
    }

    private CodebasDTO mapToDTO(final Codebas codebas, final CodebasDTO codebasDTO) {
        codebasDTO.setId(codebas.getId());
        codebasDTO.setStructure(codebas.getStructure());
        codebasDTO.setPatterns(codebas.getPatterns());
        codebasDTO.setCreatedAt(codebas.getCreatedAt());
        codebasDTO.setProject(codebas.getProject() == null ? null : codebas.getProject().getId());
        codebasDTO.setCreatedBy(codebas.getCreatedBy() == null ? null : codebas.getCreatedBy().getId());
        return codebasDTO;
    }

    private Codebas mapToEntity(final CodebasDTO codebasDTO, final Codebas codebas) {
        codebas.setStructure(codebasDTO.getStructure());
        codebas.setPatterns(codebasDTO.getPatterns());
        codebas.setCreatedAt(codebasDTO.getCreatedAt());
        final Project project = codebasDTO.getProject() == null ? null : projectRepository.findById(codebasDTO.getProject())
                .orElseThrow(() -> new NotFoundException("project not found"));
        codebas.setProject(project);
        final User createdBy = codebasDTO.getCreatedBy() == null ? null : userRepository.findById(codebasDTO.getCreatedBy())
                .orElseThrow(() -> new NotFoundException("createdBy not found"));
        codebas.setCreatedBy(createdBy);
        return codebas;
    }

    @EventListener(BeforeDeleteProject.class)
    public void on(final BeforeDeleteProject event) {
        final ReferencedException referencedException = new ReferencedException();
        final Codebas projectCodebas = codebasRepository.findFirstByProjectId(event.getId());
        if (projectCodebas != null) {
            referencedException.setKey("project.codebas.project.referenced");
            referencedException.addParam(projectCodebas.getId());
            throw referencedException;
        }
    }

    @EventListener(BeforeDeleteUser.class)
    public void on(final BeforeDeleteUser event) {
        final ReferencedException referencedException = new ReferencedException();
        final Codebas createdByCodebas = codebasRepository.findFirstByCreatedById(event.getId());
        if (createdByCodebas != null) {
            referencedException.setKey("user.codebas.createdBy.referenced");
            referencedException.addParam(createdByCodebas.getId());
            throw referencedException;
        }
    }

}
