package de.bund.digitalservice.ris.search.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.transport.http.MessageDispatcherServlet;

/**
 * Configuration class for setting up SOAP-based web services using Spring Web Services.
 *
 * <p>This class is responsible for enabling Spring Web Services functionality and providing
 * necessary configurations required to handle SOAP requests within the application. It registers a
 * {@link MessageDispatcherServlet} to manage incoming SOAP requests and define WSDL-related
 * transformations with a specific URL mapping.
 *
 * <p>The class is activated in the "prototype" and "default" application profiles. It ensures that
 * SOAP service endpoints are correctly exposed and their locations are properly transformed.
 */
@EnableWs
@Configuration
@Profile({"prototype", "default"})
public class SoapWebServiceConfig {

  /**
   * Creates and registers a {@link MessageDispatcherServlet} for handling SOAP-based web service
   * requests. The servlet is configured with the provided application context and is set to
   * transform WSDL locations.
   *
   * @param applicationContext the Spring application context to be used by the {@link
   *     MessageDispatcherServlet}
   * @return a {@link ServletRegistrationBean} instance registering the configured {@link
   *     MessageDispatcherServlet}
   */
  @Bean
  public ServletRegistrationBean<MessageDispatcherServlet> nlexMessageDispatcherServlet(
      ApplicationContext applicationContext) {
    MessageDispatcherServlet servlet = new MessageDispatcherServlet();
    servlet.setApplicationContext(applicationContext);
    servlet.setTransformWsdlLocations(true);
    return new ServletRegistrationBean<>(servlet, "/nlex/*");
  }
}
