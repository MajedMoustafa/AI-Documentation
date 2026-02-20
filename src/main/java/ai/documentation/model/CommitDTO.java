package ai.documentation.model;

import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CommitDTO {

    private Long id;

    @NotNull
    private String message;

    private OffsetDateTime createdAt;

    private String changes;

    private Long codebase;

    private Long createdBy;

}
