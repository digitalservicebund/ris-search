package de.bund.digitalservice.ris.search;

import lombok.Generated;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main class for launching the Spring Boot application. This class is annotated with {@link
 * SpringBootApplication} to indicate that it is the entry point for the Spring Boot application and
 * to enable various configuration settings.
 */
@SpringBootApplication(
    exclude = {ElasticsearchRestClientAutoConfiguration.class, DataSourceAutoConfiguration.class})
@EnableScheduling
public class Application {

  @Generated // Making (mis)use of @lombok.Generated to exclude from JaCoCo report
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
