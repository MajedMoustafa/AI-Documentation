package ai.documentation.repos;

import ai.documentation.domain.AiExplanation;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AiExplanationRepository extends JpaRepository<AiExplanation, Long> {

    AiExplanation findFirstByDocumentationId(Long id);

    AiExplanation findFirstByCreatedById(Long id);

}
