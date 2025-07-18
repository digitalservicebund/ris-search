package de.bund.digitalservice.ris.search.config;

import lombok.RequiredArgsConstructor;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FlyWayConfig {
  @Value("${db.user:test}")
  private String user;

  @Value("${db.password:test}")
  private String password;

  @Value("${db.host:localhost}")
  private String host;

  @Value("${db.port:5432}")
  private String port;

  @Value("${db.database:neuris}")
  private String database;

  @Bean(initMethod = "migrate")
  public Flyway flyway() {
    final String url = "jdbc:postgresql://" + host + ":" + port + "/" + database;
    return new Flyway(
        Flyway.configure()
            .baselineOnMigrate(true)
            .schemas("portal")
            .locations("classpath:db/migration")
            .createSchemas(true)
            .dataSource(url, user, password));
  }
}
