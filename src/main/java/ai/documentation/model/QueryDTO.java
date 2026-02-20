package ai.documentation.model;

import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class QueryDTO {

    private Long id;

    @NotNull
    private String question;

    private String answer;

    private OffsetDateTime createdAt;

    private Long documentation;

    private Long createdBy;

}
