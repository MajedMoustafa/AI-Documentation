package ai.documentation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;


@Entity
@Table(name = "\"user\"")
@Getter
@Setter
public class User {

    @Id
    @Column(nullable = false, updatable = false)
    @SequenceGenerator(
            name = "primary_sequence",
            sequenceName = "primary_sequence",
            allocationSize = 1,
            initialValue = 10000
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "primary_sequence"
    )
    private Long id;

    @Column(nullable = false, columnDefinition = "text")
    private String username;

    @Column(nullable = false, columnDefinition = "text")
    private String email;

    @Column(nullable = false, columnDefinition = "text")
    private String passwordHash;

    @Column
    private OffsetDateTime createdAt;

    @Column
    private OffsetDateTime lastLogin;

    @Column
    @ColumnDefault("true")
    private Boolean presentFlg = true;

    @Column
    @ColumnDefault("true")
    private Boolean activeFlg = true;

    @Column
    @ColumnDefault("false")
    private Boolean lockedFlg = false;

    @OneToMany(mappedBy = "createdBy")
    private Set<Project> createdByProjects = new HashSet<>();

    @OneToMany(mappedBy = "createdBy")
    private Set<Codebas> createdByCodebases = new HashSet<>();

    @OneToMany(mappedBy = "createdBy")
    private Set<PullRequest> createdByPullRequests = new HashSet<>();

    @OneToMany(mappedBy = "createdBy")
    private Set<Commit> createdByCommits = new HashSet<>();

    @OneToMany(mappedBy = "createdBy")
    private Set<Documentation> createdByDocumentations = new HashSet<>();

    @OneToMany(mappedBy = "createdBy")
    private Set<ChangeLog> createdByChangeLogs = new HashSet<>();

    @OneToMany(mappedBy = "createdBy")
    private Set<AiExplanation> createdByAiExplanations = new HashSet<>();

    @OneToMany(mappedBy = "createdBy")
    private Set<Query> createdByQueries = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<UserRole> userUserRoles = new HashSet<>();

}
