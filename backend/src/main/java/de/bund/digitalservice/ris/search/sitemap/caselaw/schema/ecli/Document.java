package de.bund.digitalservice.ris.search.sitemap.caselaw.schema.ecli;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Document {
  @XmlElement private Metadata metadata;
}
