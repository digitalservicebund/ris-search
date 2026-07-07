package de.bund.digitalservice.ris.search.xsd;

import org.springdoc.core.customizers.PropertyCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

@Configuration
@EnableConfigurationProperties(XSDDescriptionProperties.class)
public class XSDDescriptionConfiguration {
  @Bean
  XSDDescriptionParser xsdDescriptionParser(XSDDescriptionProperties properties, ResourceLoader resourceLoader) {
    return new XSDDescriptionParser(properties, resourceLoader);
  }

  @Bean
  PropertyCustomizer propertyCustomizer(XSDDescriptionParser parser) {
    return new XSDPropertyCustomizer(parser);
  }
}
