package de.bund.digitalservice.ris.search.service.xslt;

import de.bund.digitalservice.ris.html.service.xslt.CaselawXsltTransformer;
import de.bund.digitalservice.ris.html.service.xslt.LiteratureXsltTransformer;
import de.bund.digitalservice.ris.html.service.xslt.SliLiteratureXsltTransformer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class XsltTransformerConfiguration {

  @Bean
  public CaselawXsltTransformer caselawXsltTransformer() {
    return new CaselawXsltTransformer();
  }

  @Bean
  public LiteratureXsltTransformer literatureXsltTransformer() {
    return new LiteratureXsltTransformer();
  }

  @Bean
  public SliLiteratureXsltTransformer sliLiteratureXsltTransformer() {
    return new SliLiteratureXsltTransformer();
  }
}
