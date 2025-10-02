package de.bund.digitalservice.ris.search;

import de.bund.digitalservice.ris.search.importer.ImportTaskProcessor;
import lombok.Generated;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main class for launching the Spring Boot application. This class is annotated with {@link
 * SpringBootApplication} to indicate that it is the entry point for the Spring Boot application and
 * to enable various configuration settings.
 */
@SpringBootApplication(
    exclude = {
      ElasticsearchDataAutoConfiguration.class,
      ElasticsearchRestClientAutoConfiguration.class,
      DataSourceAutoConfiguration.class
    })
@EnableScheduling
public class Application {

  @Generated // Making (mis)use of @lombok.Generated to exclude from JaCoCo report
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  /**
   * The CommandLineRunner returned here checks what arguments the Spring application has been
   * invoked with. Normally, the application will start serving requests when ready, but it can be
   * invoked with arguments defined in {@link ImportTaskProcessor} to run specific tasks instead.
   * Upon completion, the application will exit.
   *
   * @return A CommandLineRunner to be invoked with the given arguments.
   */
  @Bean
  public CommandLineRunner specificTaskRunner(ApplicationContext context) {
    return args -> {
      ImportTaskProcessor taskProcessor = context.getBean(ImportTaskProcessor.class);
      if (taskProcessor.shouldRun(args)) {
        int status = taskProcessor.run(args);
        System.exit(status);
      }
    };
  }
}
