package ai.documentation.model;

import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ProjectDTO {

    private Long id;

    @NotNull
    private String name;

    private String description;

    private String repositoryUrl;

    private OffsetDateTime createdAt;

    private Long createdBy;

}
