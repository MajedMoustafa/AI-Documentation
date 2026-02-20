package ai.documentation.rest;

import ai.documentation.model.AiExplanationDTO;
import ai.documentation.service.AiExplanationService;
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
@RequestMapping(value = "/api/aiExplanations", produces = MediaType.APPLICATION_JSON_VALUE)
public class AiExplanationResource {

    private final AiExplanationService aiExplanationService;

    public AiExplanationResource(final AiExplanationService aiExplanationService) {
        this.aiExplanationService = aiExplanationService;
    }

    @GetMapping
    public ResponseEntity<List<AiExplanationDTO>> getAllAiExplanations() {
        return ResponseEntity.ok(aiExplanationService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AiExplanationDTO> getAiExplanation(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(aiExplanationService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createAiExplanation(
            @RequestBody @Valid final AiExplanationDTO aiExplanationDTO) {
        final Long createdId = aiExplanationService.create(aiExplanationDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateAiExplanation(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final AiExplanationDTO aiExplanationDTO) {
        aiExplanationService.update(id, aiExplanationDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteAiExplanation(@PathVariable(name = "id") final Long id) {
        aiExplanationService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
