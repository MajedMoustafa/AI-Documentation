package ai.documentation.service;

import ai.documentation.domain.Documentation;
import ai.documentation.domain.Project;
import ai.documentation.domain.User;
import ai.documentation.events.BeforeDeleteDocumentation;
import ai.documentation.events.BeforeDeleteProject;
import ai.documentation.events.BeforeDeleteUser;
import ai.documentation.model.DocumentationDTO;
import ai.documentation.repos.DocumentationRepository;
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
public class DocumentationService {

    private final DocumentationRepository documentationRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher publisher;

    public DocumentationService(final DocumentationRepository documentationRepository,
            final ProjectRepository projectRepository, final UserRepository userRepository,
            final ApplicationEventPublisher publisher) {
        this.documentationRepository = documentationRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.publisher = publisher;
    }

    public List<DocumentationDTO> findAll() {
        final List<Documentation> documentations = documentationRepository.findAll(Sort.by("id"));
        return documentations.stream()
                .map(documentation -> mapToDTO(documentation, new DocumentationDTO()))
                .toList();
    }

    public DocumentationDTO get(final Long id) {
        return documentationRepository.findById(id)
                .map(documentation -> mapToDTO(documentation, new DocumentationDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final DocumentationDTO documentationDTO) {
        final Documentation documentation = new Documentation();
        mapToEntity(documentationDTO, documentation);
        return documentationRepository.save(documentation).getId();
    }

    public void update(final Long id, final DocumentationDTO documentationDTO) {
        final Documentation documentation = documentationRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(documentationDTO, documentation);
        documentationRepository.save(documentation);
    }

    public void delete(final Long id) {
        final Documentation documentation = documentationRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteDocumentation(id));
        documentationRepository.delete(documentation);
    }

    private DocumentationDTO mapToDTO(final Documentation documentation,
            final DocumentationDTO documentationDTO) {
        documentationDTO.setId(documentation.getId());
        documentationDTO.setContent(documentation.getContent());
        documentationDTO.setVersion(documentation.getVersion());
        documentationDTO.setLastUpdated(documentation.getLastUpdated());
        documentationDTO.setCreatedAt(documentation.getCreatedAt());
        documentationDTO.setProject(documentation.getProject() == null ? null : documentation.getProject().getId());
        documentationDTO.setCreatedBy(documentation.getCreatedBy() == null ? null : documentation.getCreatedBy().getId());
        return documentationDTO;
    }

    private Documentation mapToEntity(final DocumentationDTO documentationDTO,
            final Documentation documentation) {
        documentation.setContent(documentationDTO.getContent());
        documentation.setVersion(documentationDTO.getVersion());
        documentation.setLastUpdated(documentationDTO.getLastUpdated());
        documentation.setCreatedAt(documentationDTO.getCreatedAt());
        final Project project = documentationDTO.getProject() == null ? null : projectRepository.findById(documentationDTO.getProject())
                .orElseThrow(() -> new NotFoundException("project not found"));
        documentation.setProject(project);
        final User createdBy = documentationDTO.getCreatedBy() == null ? null : userRepository.findById(documentationDTO.getCreatedBy())
                .orElseThrow(() -> new NotFoundException("createdBy not found"));
        documentation.setCreatedBy(createdBy);
        return documentation;
    }

    @EventListener(BeforeDeleteProject.class)
    public void on(final BeforeDeleteProject event) {
        final ReferencedException referencedException = new ReferencedException();
        final Documentation projectDocumentation = documentationRepository.findFirstByProjectId(event.getId());
        if (projectDocumentation != null) {
            referencedException.setKey("project.documentation.project.referenced");
            referencedException.addParam(projectDocumentation.getId());
            throw referencedException;
        }
    }

    @EventListener(BeforeDeleteUser.class)
    public void on(final BeforeDeleteUser event) {
        final ReferencedException referencedException = new ReferencedException();
        final Documentation createdByDocumentation = documentationRepository.findFirstByCreatedById(event.getId());
        if (createdByDocumentation != null) {
            referencedException.setKey("user.documentation.createdBy.referenced");
            referencedException.addParam(createdByDocumentation.getId());
            throw referencedException;
        }
    }

}
