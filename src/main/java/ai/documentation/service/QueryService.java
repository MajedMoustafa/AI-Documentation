package ai.documentation.service;

import ai.documentation.domain.Documentation;
import ai.documentation.domain.Query;
import ai.documentation.domain.User;
import ai.documentation.events.BeforeDeleteDocumentation;
import ai.documentation.events.BeforeDeleteUser;
import ai.documentation.model.QueryDTO;
import ai.documentation.repos.DocumentationRepository;
import ai.documentation.repos.QueryRepository;
import ai.documentation.repos.UserRepository;
import ai.documentation.util.NotFoundException;
import ai.documentation.util.ReferencedException;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class QueryService {

    private final QueryRepository queryRepository;
    private final DocumentationRepository documentationRepository;
    private final UserRepository userRepository;

    public QueryService(final QueryRepository queryRepository,
            final DocumentationRepository documentationRepository,
            final UserRepository userRepository) {
        this.queryRepository = queryRepository;
        this.documentationRepository = documentationRepository;
        this.userRepository = userRepository;
    }

    public List<QueryDTO> findAll() {
        final List<Query> queries = queryRepository.findAll(Sort.by("id"));
        return queries.stream()
                .map(query -> mapToDTO(query, new QueryDTO()))
                .toList();
    }

    public QueryDTO get(final Long id) {
        return queryRepository.findById(id)
                .map(query -> mapToDTO(query, new QueryDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final QueryDTO queryDTO) {
        final Query query = new Query();
        mapToEntity(queryDTO, query);
        return queryRepository.save(query).getId();
    }

    public void update(final Long id, final QueryDTO queryDTO) {
        final Query query = queryRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(queryDTO, query);
        queryRepository.save(query);
    }

    public void delete(final Long id) {
        final Query query = queryRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        queryRepository.delete(query);
    }

    private QueryDTO mapToDTO(final Query query, final QueryDTO queryDTO) {
        queryDTO.setId(query.getId());
        queryDTO.setQuestion(query.getQuestion());
        queryDTO.setAnswer(query.getAnswer());
        queryDTO.setCreatedAt(query.getCreatedAt());
        queryDTO.setDocumentation(query.getDocumentation() == null ? null : query.getDocumentation().getId());
        queryDTO.setCreatedBy(query.getCreatedBy() == null ? null : query.getCreatedBy().getId());
        return queryDTO;
    }

    private Query mapToEntity(final QueryDTO queryDTO, final Query query) {
        query.setQuestion(queryDTO.getQuestion());
        query.setAnswer(queryDTO.getAnswer());
        query.setCreatedAt(queryDTO.getCreatedAt());
        final Documentation documentation = queryDTO.getDocumentation() == null ? null : documentationRepository.findById(queryDTO.getDocumentation())
                .orElseThrow(() -> new NotFoundException("documentation not found"));
        query.setDocumentation(documentation);
        final User createdBy = queryDTO.getCreatedBy() == null ? null : userRepository.findById(queryDTO.getCreatedBy())
                .orElseThrow(() -> new NotFoundException("createdBy not found"));
        query.setCreatedBy(createdBy);
        return query;
    }

    @EventListener(BeforeDeleteDocumentation.class)
    public void on(final BeforeDeleteDocumentation event) {
        final ReferencedException referencedException = new ReferencedException();
        final Query documentationQuery = queryRepository.findFirstByDocumentationId(event.getId());
        if (documentationQuery != null) {
            referencedException.setKey("documentation.query.documentation.referenced");
            referencedException.addParam(documentationQuery.getId());
            throw referencedException;
        }
    }

    @EventListener(BeforeDeleteUser.class)
    public void on(final BeforeDeleteUser event) {
        final ReferencedException referencedException = new ReferencedException();
        final Query createdByQuery = queryRepository.findFirstByCreatedById(event.getId());
        if (createdByQuery != null) {
            referencedException.setKey("user.query.createdBy.referenced");
            referencedException.addParam(createdByQuery.getId());
            throw referencedException;
        }
    }

}
