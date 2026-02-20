package ai.documentation.config;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@EntityScan("ai.documentation.domain")
@EnableJpaRepositories("ai.documentation.repos")
@EnableTransactionManagement
public class DomainConfig {
}
