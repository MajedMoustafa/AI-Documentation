package ai.documentation.rest;

import ai.documentation.model.DocumentationDTO;
import ai.documentation.service.DocumentationService;
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
@RequestMapping(value = "/api/documentations", produces = MediaType.APPLICATION_JSON_VALUE)
public class DocumentationResource {

    private final DocumentationService documentationService;

    public DocumentationResource(final DocumentationService documentationService) {
        this.documentationService = documentationService;
    }

    @GetMapping
    public ResponseEntity<List<DocumentationDTO>> getAllDocumentations() {
        return ResponseEntity.ok(documentationService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentationDTO> getDocumentation(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(documentationService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createDocumentation(
            @RequestBody @Valid final DocumentationDTO documentationDTO) {
        final Long createdId = documentationService.create(documentationDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateDocumentation(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final DocumentationDTO documentationDTO) {
        documentationService.update(id, documentationDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteDocumentation(@PathVariable(name = "id") final Long id) {
        documentationService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
