package ai.documentation.rest;

import ai.documentation.model.CommitDTO;
import ai.documentation.service.CommitService;
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
@RequestMapping(value = "/api/commits", produces = MediaType.APPLICATION_JSON_VALUE)
public class CommitResource {

    private final CommitService commitService;

    public CommitResource(final CommitService commitService) {
        this.commitService = commitService;
    }

    @GetMapping
    public ResponseEntity<List<CommitDTO>> getAllCommits() {
        return ResponseEntity.ok(commitService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommitDTO> getCommit(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(commitService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createCommit(@RequestBody @Valid final CommitDTO commitDTO) {
        final Long createdId = commitService.create(commitDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateCommit(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final CommitDTO commitDTO) {
        commitService.update(id, commitDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteCommit(@PathVariable(name = "id") final Long id) {
        commitService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
