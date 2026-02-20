package ai.documentation.service;

import ai.documentation.domain.Project;
import ai.documentation.domain.User;
import ai.documentation.events.BeforeDeleteProject;
import ai.documentation.events.BeforeDeleteUser;
import ai.documentation.model.ProjectDTO;
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
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher publisher;

    public ProjectService(final ProjectRepository projectRepository,
            final UserRepository userRepository, final ApplicationEventPublisher publisher) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.publisher = publisher;
    }

    public List<ProjectDTO> findAll() {
        final List<Project> projects = projectRepository.findAll(Sort.by("id"));
        return projects.stream()
                .map(project -> mapToDTO(project, new ProjectDTO()))
                .toList();
    }

    public ProjectDTO get(final Long id) {
        return projectRepository.findById(id)
                .map(project -> mapToDTO(project, new ProjectDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final ProjectDTO projectDTO) {
        final Project project = new Project();
        mapToEntity(projectDTO, project);
        return projectRepository.save(project).getId();
    }

    public void update(final Long id, final ProjectDTO projectDTO) {
        final Project project = projectRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(projectDTO, project);
        projectRepository.save(project);
    }

    public void delete(final Long id) {
        final Project project = projectRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteProject(id));
        projectRepository.delete(project);
    }

    private ProjectDTO mapToDTO(final Project project, final ProjectDTO projectDTO) {
        projectDTO.setId(project.getId());
        projectDTO.setName(project.getName());
        projectDTO.setDescription(project.getDescription());
        projectDTO.setRepositoryUrl(project.getRepositoryUrl());
        projectDTO.setCreatedAt(project.getCreatedAt());
        projectDTO.setCreatedBy(project.getCreatedBy() == null ? null : project.getCreatedBy().getId());
        return projectDTO;
    }

    private Project mapToEntity(final ProjectDTO projectDTO, final Project project) {
        project.setName(projectDTO.getName());
        project.setDescription(projectDTO.getDescription());
        project.setRepositoryUrl(projectDTO.getRepositoryUrl());
        project.setCreatedAt(projectDTO.getCreatedAt());
        final User createdBy = projectDTO.getCreatedBy() == null ? null : userRepository.findById(projectDTO.getCreatedBy())
                .orElseThrow(() -> new NotFoundException("createdBy not found"));
        project.setCreatedBy(createdBy);
        return project;
    }

    @EventListener(BeforeDeleteUser.class)
    public void on(final BeforeDeleteUser event) {
        final ReferencedException referencedException = new ReferencedException();
        final Project createdByProject = projectRepository.findFirstByCreatedById(event.getId());
        if (createdByProject != null) {
            referencedException.setKey("user.project.createdBy.referenced");
            referencedException.addParam(createdByProject.getId());
            throw referencedException;
        }
    }

}
