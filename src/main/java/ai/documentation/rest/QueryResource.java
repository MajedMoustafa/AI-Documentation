package ai.documentation.rest;

import ai.documentation.model.QueryDTO;
import ai.documentation.service.QueryService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/queries", produces = MediaType.APPLICATION_JSON_VALUE)
public class QueryResource {

    private final QueryService queryService;

    public QueryResource(final QueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping
    public ResponseEntity<List<QueryDTO>> getAllQueries() {
        return ResponseEntity.ok(queryService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<QueryDTO> getQuery(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(queryService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createQuery(@RequestBody @Valid final QueryDTO queryDTO) {
        final Long createdId = queryService.create(queryDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateQuery(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final QueryDTO queryDTO) {
        queryService.update(id, queryDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteQuery(@PathVariable(name = "id") final Long id) {
        queryService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
