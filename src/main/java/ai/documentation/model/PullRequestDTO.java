package ai.documentation.model;

import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PullRequestDTO {

    private Long id;

    @NotNull
    private String title;

    private String description;

    private String status;

    private OffsetDateTime createdAt;

    private Long codebase;

    private Long createdBy;

}
