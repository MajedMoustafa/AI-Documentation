package ai.documentation.model;

import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class DocumentationDTO {

    private Long id;

    @NotNull
    private String content;

    private String version;

    private OffsetDateTime lastUpdated;

    private OffsetDateTime createdAt;

    private Long project;

    private Long createdBy;

}
