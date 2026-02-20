package ai.documentation.rest;

import ai.documentation.model.ChangeLogDTO;
import ai.documentation.service.ChangeLogService;
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
@RequestMapping(value = "/api/changeLogs", produces = MediaType.APPLICATION_JSON_VALUE)
public class ChangeLogResource {

    private final ChangeLogService changeLogService;

    public ChangeLogResource(final ChangeLogService changeLogService) {
        this.changeLogService = changeLogService;
    }

    @GetMapping
    public ResponseEntity<List<ChangeLogDTO>> getAllChangeLogs() {
        return ResponseEntity.ok(changeLogService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChangeLogDTO> getChangeLog(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(changeLogService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createChangeLog(
            @RequestBody @Valid final ChangeLogDTO changeLogDTO) {
        final Long createdId = changeLogService.create(changeLogDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateChangeLog(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final ChangeLogDTO changeLogDTO) {
        changeLogService.update(id, changeLogDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteChangeLog(@PathVariable(name = "id") final Long id) {
        changeLogService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
