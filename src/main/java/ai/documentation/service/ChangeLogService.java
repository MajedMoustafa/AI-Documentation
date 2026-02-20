package ai.documentation.service;

import ai.documentation.domain.ChangeLog;
import ai.documentation.domain.Documentation;
import ai.documentation.domain.User;
import ai.documentation.events.BeforeDeleteDocumentation;
import ai.documentation.events.BeforeDeleteUser;
import ai.documentation.model.ChangeLogDTO;
import ai.documentation.repos.ChangeLogRepository;
import ai.documentation.repos.DocumentationRepository;
import ai.documentation.repos.UserRepository;
import ai.documentation.util.NotFoundException;
import ai.documentation.util.ReferencedException;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class ChangeLogService {

    private final ChangeLogRepository changeLogRepository;
    private final DocumentationRepository documentationRepository;
    private final UserRepository userRepository;

    public ChangeLogService(final ChangeLogRepository changeLogRepository,
            final DocumentationRepository documentationRepository,
            final UserRepository userRepository) {
        this.changeLogRepository = changeLogRepository;
        this.documentationRepository = documentationRepository;
        this.userRepository = userRepository;
    }

    public List<ChangeLogDTO> findAll() {
        final List<ChangeLog> changeLogs = changeLogRepository.findAll(Sort.by("id"));
        return changeLogs.stream()
                .map(changeLog -> mapToDTO(changeLog, new ChangeLogDTO()))
                .toList();
    }

    public ChangeLogDTO get(final Long id) {
        return changeLogRepository.findById(id)
                .map(changeLog -> mapToDTO(changeLog, new ChangeLogDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final ChangeLogDTO changeLogDTO) {
        final ChangeLog changeLog = new ChangeLog();
        mapToEntity(changeLogDTO, changeLog);
        return changeLogRepository.save(changeLog).getId();
    }

    public void update(final Long id, final ChangeLogDTO changeLogDTO) {
        final ChangeLog changeLog = changeLogRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(changeLogDTO, changeLog);
        changeLogRepository.save(changeLog);
    }

    public void delete(final Long id) {
        final ChangeLog changeLog = changeLogRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        changeLogRepository.delete(changeLog);
    }

    private ChangeLogDTO mapToDTO(final ChangeLog changeLog, final ChangeLogDTO changeLogDTO) {
        changeLogDTO.setId(changeLog.getId());
        changeLogDTO.setChangeDescription(changeLog.getChangeDescription());
        changeLogDTO.setCreatedAt(changeLog.getCreatedAt());
        changeLogDTO.setDocumentation(changeLog.getDocumentation() == null ? null : changeLog.getDocumentation().getId());
        changeLogDTO.setCreatedBy(changeLog.getCreatedBy() == null ? null : changeLog.getCreatedBy().getId());
        return changeLogDTO;
    }

    private ChangeLog mapToEntity(final ChangeLogDTO changeLogDTO, final ChangeLog changeLog) {
        changeLog.setChangeDescription(changeLogDTO.getChangeDescription());
        changeLog.setCreatedAt(changeLogDTO.getCreatedAt());
        final Documentation documentation = changeLogDTO.getDocumentation() == null ? null : documentationRepository.findById(changeLogDTO.getDocumentation())
                .orElseThrow(() -> new NotFoundException("documentation not found"));
        changeLog.setDocumentation(documentation);
        final User createdBy = changeLogDTO.getCreatedBy() == null ? null : userRepository.findById(changeLogDTO.getCreatedBy())
                .orElseThrow(() -> new NotFoundException("createdBy not found"));
        changeLog.setCreatedBy(createdBy);
        return changeLog;
    }

    @EventListener(BeforeDeleteDocumentation.class)
    public void on(final BeforeDeleteDocumentation event) {
        final ReferencedException referencedException = new ReferencedException();
        final ChangeLog documentationChangeLog = changeLogRepository.findFirstByDocumentationId(event.getId());
        if (documentationChangeLog != null) {
            referencedException.setKey("documentation.changeLog.documentation.referenced");
            referencedException.addParam(documentationChangeLog.getId());
            throw referencedException;
        }
    }

    @EventListener(BeforeDeleteUser.class)
    public void on(final BeforeDeleteUser event) {
        final ReferencedException referencedException = new ReferencedException();
        final ChangeLog createdByChangeLog = changeLogRepository.findFirstByCreatedById(event.getId());
        if (createdByChangeLog != null) {
            referencedException.setKey("user.changeLog.createdBy.referenced");
            referencedException.addParam(createdByChangeLog.getId());
            throw referencedException;
        }
    }

}
