package de.bund.digitalservice.ris.search.service.xslt;

import de.bund.digitalservice.ris.html.service.xslt.CaselawXsltTransformer;
import de.bund.digitalservice.ris.html.service.xslt.SliLiteratureV1XsltTransformer;
import de.bund.digitalservice.ris.html.service.xslt.UliLiteratureV1XsltTransformer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** XsltTransformerConfiguration to configure Xslt transformers */
@Configuration
public class XsltTransformerConfiguration {

  @Bean
  public CaselawXsltTransformer caselawXsltTransformer() {
    return new CaselawXsltTransformer();
  }

  @Bean
  public UliLiteratureV1XsltTransformer literatureXsltTransformer() {
    return new UliLiteratureV1XsltTransformer();
  }

  @Bean
  public SliLiteratureV1XsltTransformer sliLiteratureXsltTransformer() {
    return new SliLiteratureV1XsltTransformer();
  }
}
