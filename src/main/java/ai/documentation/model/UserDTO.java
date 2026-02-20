package ai.documentation.model;

import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserDTO {

    private Long id;

    @NotNull
    private String username;

    @NotNull
    private String email;

    @NotNull
    private String passwordHash;

    private OffsetDateTime createdAt;

    private OffsetDateTime lastLogin;

    private Boolean activeFlg;

    private Boolean lockedFlg;

}
