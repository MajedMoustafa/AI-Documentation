package ai.documentation.model;

import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CodebasDTO {

    private Long id;
    private String structure;
    private String patterns;
    private OffsetDateTime createdAt;
    private Long project;
    private Long createdBy;

}
