package de.bund.digitalservice.ris.search.config;

import de.bund.digitalservice.ris.search.service.ConnectorSoapImpl;
import jakarta.xml.ws.Endpoint;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebserviceConfig {

  @Bean(name = Bus.DEFAULT_BUS_ID)
  public SpringBus springBus() {
    SpringBus springBus = new SpringBus();
    return springBus;
  }

  @Bean
  public ServletRegistrationBean cxfServlet() {
    return new ServletRegistrationBean<>(new CXFServlet(), "/internal/soap/*");
  }

  @Bean
  public Endpoint endpoint() {
    EndpointImpl endpoint = new EndpointImpl(springBus(), new ConnectorSoapImpl());
    endpoint.publish("/");
    return endpoint;
  }
}
