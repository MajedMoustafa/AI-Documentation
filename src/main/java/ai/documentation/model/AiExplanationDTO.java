package ai.documentation.model;

import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AiExplanationDTO {

    private Long id;

    @NotNull
    private String explanation;

    private String context;

    private OffsetDateTime createdAt;

    private Long documentation;

    private Long createdBy;

}
