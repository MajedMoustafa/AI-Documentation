package ai.documentation.model;

import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserRoleDTO {

    private Long id;
    private OffsetDateTime createdAt;
    private Long user;
    private Long role;

}
