package de.bund.digitalservice.ris.builder.models.common;

import de.bund.digitalservice.ris.builder.models.preface.DocStage;
import de.bund.digitalservice.ris.builder.models.preface.DocTitle;
import de.bund.digitalservice.ris.builder.models.preface.ShortTitle;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@XmlSeeAlso({DocStage.class, DocTitle.class, ShortTitle.class})
public class AknP {

  @Builder.Default
  @XmlAttribute(name = "GUID")
  private String guid = UUID.randomUUID().toString();

  @XmlAttribute(name = "eId")
  private String eId;

  @Builder.Default @XmlAnyElement private List<Object> children = new ArrayList<>();
}
