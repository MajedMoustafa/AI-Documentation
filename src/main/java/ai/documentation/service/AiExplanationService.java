package ai.documentation.service;

import ai.documentation.domain.AiExplanation;
import ai.documentation.domain.Documentation;
import ai.documentation.domain.User;
import ai.documentation.events.BeforeDeleteDocumentation;
import ai.documentation.events.BeforeDeleteUser;
import ai.documentation.model.AiExplanationDTO;
import ai.documentation.repos.AiExplanationRepository;
import ai.documentation.repos.DocumentationRepository;
import ai.documentation.repos.UserRepository;
import ai.documentation.util.NotFoundException;
import ai.documentation.util.ReferencedException;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class AiExplanationService {

    private final AiExplanationRepository aiExplanationRepository;
    private final DocumentationRepository documentationRepository;
    private final UserRepository userRepository;

    public AiExplanationService(final AiExplanationRepository aiExplanationRepository,
            final DocumentationRepository documentationRepository,
            final UserRepository userRepository) {
        this.aiExplanationRepository = aiExplanationRepository;
        this.documentationRepository = documentationRepository;
        this.userRepository = userRepository;
    }

    public List<AiExplanationDTO> findAll() {
        final List<AiExplanation> aiExplanations = aiExplanationRepository.findAll(Sort.by("id"));
        return aiExplanations.stream()
                .map(aiExplanation -> mapToDTO(aiExplanation, new AiExplanationDTO()))
                .toList();
    }

    public AiExplanationDTO get(final Long id) {
        return aiExplanationRepository.findById(id)
                .map(aiExplanation -> mapToDTO(aiExplanation, new AiExplanationDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final AiExplanationDTO aiExplanationDTO) {
        final AiExplanation aiExplanation = new AiExplanation();
        mapToEntity(aiExplanationDTO, aiExplanation);
        return aiExplanationRepository.save(aiExplanation).getId();
    }

    public void update(final Long id, final AiExplanationDTO aiExplanationDTO) {
        final AiExplanation aiExplanation = aiExplanationRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(aiExplanationDTO, aiExplanation);
        aiExplanationRepository.save(aiExplanation);
    }

    public void delete(final Long id) {
        final AiExplanation aiExplanation = aiExplanationRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        aiExplanationRepository.delete(aiExplanation);
    }

    private AiExplanationDTO mapToDTO(final AiExplanation aiExplanation,
            final AiExplanationDTO aiExplanationDTO) {
        aiExplanationDTO.setId(aiExplanation.getId());
        aiExplanationDTO.setExplanation(aiExplanation.getExplanation());
        aiExplanationDTO.setContext(aiExplanation.getContext());
        aiExplanationDTO.setCreatedAt(aiExplanation.getCreatedAt());
        aiExplanationDTO.setDocumentation(aiExplanation.getDocumentation() == null ? null : aiExplanation.getDocumentation().getId());
        aiExplanationDTO.setCreatedBy(aiExplanation.getCreatedBy() == null ? null : aiExplanation.getCreatedBy().getId());
        return aiExplanationDTO;
    }

    private AiExplanation mapToEntity(final AiExplanationDTO aiExplanationDTO,
            final AiExplanation aiExplanation) {
        aiExplanation.setExplanation(aiExplanationDTO.getExplanation());
        aiExplanation.setContext(aiExplanationDTO.getContext());
        aiExplanation.setCreatedAt(aiExplanationDTO.getCreatedAt());
        final Documentation documentation = aiExplanationDTO.getDocumentation() == null ? null : documentationRepository.findById(aiExplanationDTO.getDocumentation())
                .orElseThrow(() -> new NotFoundException("documentation not found"));
        aiExplanation.setDocumentation(documentation);
        final User createdBy = aiExplanationDTO.getCreatedBy() == null ? null : userRepository.findById(aiExplanationDTO.getCreatedBy())
                .orElseThrow(() -> new NotFoundException("createdBy not found"));
        aiExplanation.setCreatedBy(createdBy);
        return aiExplanation;
    }

    @EventListener(BeforeDeleteDocumentation.class)
    public void on(final BeforeDeleteDocumentation event) {
        final ReferencedException referencedException = new ReferencedException();
        final AiExplanation documentationAiExplanation = aiExplanationRepository.findFirstByDocumentationId(event.getId());
        if (documentationAiExplanation != null) {
            referencedException.setKey("documentation.aiExplanation.documentation.referenced");
            referencedException.addParam(documentationAiExplanation.getId());
            throw referencedException;
        }
    }

    @EventListener(BeforeDeleteUser.class)
    public void on(final BeforeDeleteUser event) {
        final ReferencedException referencedException = new ReferencedException();
        final AiExplanation createdByAiExplanation = aiExplanationRepository.findFirstByCreatedById(event.getId());
        if (createdByAiExplanation != null) {
            referencedException.setKey("user.aiExplanation.createdBy.referenced");
            referencedException.addParam(createdByAiExplanation.getId());
            throw referencedException;
        }
    }

}
