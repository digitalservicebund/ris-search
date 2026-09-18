package de.bund.digitalservice.ris.builder.models.preface;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.NoArgsConstructor;

/** Represents the {@code akn:docStage} element, e.g. the norm's promulgation stage marker. */
@NoArgsConstructor
@XmlRootElement(namespace = NormTestDataBuilder.AKN_NS)
public class DocStage extends BaseElement {

  @XmlAttribute private String eId = "einleitung-n1_doktitel-n1_text-n1_docstadium-n1";
}
