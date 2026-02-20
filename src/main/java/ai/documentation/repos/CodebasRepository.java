package ai.documentation.repos;

import ai.documentation.domain.Codebas;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CodebasRepository extends JpaRepository<Codebas, Long> {

    Codebas findFirstByProjectId(Long id);

    Codebas findFirstByCreatedById(Long id);

}
