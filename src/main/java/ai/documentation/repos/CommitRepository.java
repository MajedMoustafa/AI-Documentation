package ai.documentation.repos;

import ai.documentation.domain.Commit;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CommitRepository extends JpaRepository<Commit, Long> {

    Commit findFirstByCodebaseId(Long id);

    Commit findFirstByCreatedById(Long id);

}
