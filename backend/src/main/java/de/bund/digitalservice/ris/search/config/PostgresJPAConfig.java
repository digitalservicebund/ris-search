package de.bund.digitalservice.ris.search.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
    basePackages = "de.bund.digitalservice.ris.search.sitemap.eclicrawler.repository")
public class PostgresJPAConfig {
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

  @Bean
  public DataSource dataSource() {
    return DataSourceBuilder.create()
        .driverClassName("org.postgresql.Driver")
        .url("jdbc:postgresql://" + host + ":" + port + "/" + database)
        .username(user)
        .password(password)
        .build();
  }
}
