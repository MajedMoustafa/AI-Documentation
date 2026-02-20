package ai.documentation.rest;

import ai.documentation.model.UserRoleDTO;
import ai.documentation.service.UserRoleService;
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
@RequestMapping(value = "/api/userRoles", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserRoleResource {

    private final UserRoleService userRoleService;

    public UserRoleResource(final UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    @GetMapping
    public ResponseEntity<List<UserRoleDTO>> getAllUserRoles() {
        return ResponseEntity.ok(userRoleService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserRoleDTO> getUserRole(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(userRoleService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createUserRole(@RequestBody @Valid final UserRoleDTO userRoleDTO) {
        final Long createdId = userRoleService.create(userRoleDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateUserRole(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final UserRoleDTO userRoleDTO) {
        userRoleService.update(id, userRoleDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteUserRole(@PathVariable(name = "id") final Long id) {
        userRoleService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
