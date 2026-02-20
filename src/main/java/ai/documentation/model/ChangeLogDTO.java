package ai.documentation.model;

import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ChangeLogDTO {

    private Long id;

    @NotNull
    private String changeDescription;

    private OffsetDateTime createdAt;

    private Long documentation;

    private Long createdBy;

}
