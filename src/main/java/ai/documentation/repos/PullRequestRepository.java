package ai.documentation.repos;

import ai.documentation.domain.PullRequest;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PullRequestRepository extends JpaRepository<PullRequest, Long> {

    PullRequest findFirstByCodebaseId(Long id);

    PullRequest findFirstByCreatedById(Long id);

}
