package de.bund.digitalservice.ris.builder;

import de.bund.digitalservice.ris.builder.models.Act;
import de.bund.digitalservice.ris.builder.models.AkomaNtoso;
import de.bund.digitalservice.ris.builder.models.body.Article;
import de.bund.digitalservice.ris.builder.models.body.Chapter;
import de.bund.digitalservice.ris.builder.models.body.Section;
import de.bund.digitalservice.ris.builder.models.meta.Meta;
import de.bund.digitalservice.ris.builder.models.meta.identification.Identification;
import de.bund.digitalservice.ris.builder.models.meta.lifecycle.Lifecycle;
import de.bund.digitalservice.ris.builder.models.preface.LongTitle;
import de.bund.digitalservice.ris.builder.models.preface.ShortTitle;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import java.io.StringWriter;
import org.eclipse.persistence.oxm.NamespacePrefixMapper;

public class NormTestDataBuilder {

  public static final String AKN_NS = "http://Inhaltsdaten.LegalDocML.de/1.8.2/";
  public static final String RIS_NS = "http://MetadatenRIS.LegalDocML.de/1.8.2/";

  private static final String SCHEMA_LOCATION =
      "http://MetadatenRIS.LegalDocML.de/1.8.2/ Grammatiken/Norms/legalDocML.de-metadaten-ris.xsd"
          + " http://MetadatenRegelungstext.LegalDocML.de/1.8.2/ Grammatiken/Norms/legalDocML.de-metadaten-regelungstext.xsd"
          + " http://MetadatenRechtsetzungsdokument.LegalDocML.de/1.8.2/ Grammatiken/Norms/legalDocML.de-metadaten-rechtsetzungsdokument.xsd"
          + " http://Inhaltsdaten.LegalDocML.de/1.8.2/ Grammatiken/Norms/legalDocML.de-regelungstextverkuendungsfassung.xsd";

  private final AkomaNtoso document = new AkomaNtoso();

  public NormTestDataBuilder() {
    this.document.setAct(Act.builder().meta(Meta.builder().build()).build());
  }

  public NormTestDataBuilder eli(String manifestationEli) {
    this.document.getAct().getMeta().setIdentification(Identification.fromEli(manifestationEli));
    return this;
  }

  public NormTestDataBuilder officialTitle(String officalTitle) {
    LongTitle longTitle = this.document.getAct().getPreface().getLongTitle();
    this.document.getAct().getPreface().setLongTitle(longTitle.withOfficialTitle(officalTitle));

    return this;
  }

  public NormTestDataBuilder shortTitle(String title) {
    ShortTitle shortTitle = this.document.getAct().getPreface().getLongTitle().getShortTitle();
    this.document
        .getAct()
        .getPreface()
        .setLongTitle(
            this.document
                .getAct()
                .getPreface()
                .getLongTitle()
                .withShortTitle(shortTitle.withTitle(title)));

    return this;
  }

  public NormTestDataBuilder officialAbbreviation(String abbreviation) {
    ShortTitle shortTitle = this.document.getAct().getPreface().getLongTitle().getShortTitle();
    this.document
        .getAct()
        .getPreface()
        .setLongTitle(
            this.document
                .getAct()
                .getPreface()
                .getLongTitle()
                .withShortTitle(shortTitle.withOfficialAbbreviation(abbreviation)));

    return this;
  }

  public NormTestDataBuilder inForceDate(String inForceDate) {
    this.document.getAct().getMeta().getProprietary().getRisMetadata().setInForce(inForceDate);
    return this;
  }

  public NormTestDataBuilder outOfForceDate(String outOfForceDate) {
    this.document
        .getAct()
        .getMeta()
        .getProprietary()
        .getRisMetadata()
        .setOutOfForce(outOfForceDate);
    return this;
  }

  public NormTestDataBuilder legislationDate(String date) {
    this.document.getAct().getPreface().setLegislationDate(date);
    return this;
  }

  public NormTestDataBuilder datePublished(String date) {
    this.document.getAct().getMeta().getIdentification().getFrbrWork().setDatePublished(date);
    return this;
  }

  public NormTestDataBuilder risAbbreviation(String internalAbbreviation) {
    this.document
        .getAct()
        .getMeta()
        .getProprietary()
        .getRisMetadata()
        .setAbbreviation(internalAbbreviation);
    return this;
  }

  public Chapter chapter(String heading, String num) {
    Chapter chapter = new Chapter().addHeading(heading).addNum(num);
    this.document.getAct().getBody().addChild(chapter);
    return chapter;
  }

  public Section section(String heading, String num) {
    Section section = new Section().addHeading(heading).addNum(num);
    this.document.getAct().getBody().addChild(section);
    return section;
  }

  public NormTestDataBuilder article(Article article) {
    this.document.getAct().getBody().addChild(article);
    return this;
  }

  public Article buildArticle(
      String heading, String num, String startDate, String endDate, String eId) {
    Lifecycle lifecycle = this.document.getAct().getMeta().getLifecycle();
    String inForceEventEId = lifecycle.addInForceEvent(startDate);
    String outOfForceEventEId = lifecycle.addOutOfForce(endDate);

    String temporalGroupEId =
        this.document
            .getAct()
            .getMeta()
            .getTemporalData()
            .addTemporalGroup(inForceEventEId, outOfForceEventEId);

    return Article.builder()
        .eId(eId)
        .period("#" + temporalGroupEId)
        .build()
        .addNum(num)
        .addHeading(heading);
  }

  public NormTestDataBuilder defaultArticle() {
    Article article =
        this.buildArticle("Article number one", "§ 1", "2025-01-01", "2025-07-01", "art-z1")
            .addParagraph("Paragraph one", "(1)");

    this.document.getAct().getBody().addChild(article);
    return this;
  }

  /**
   * Converts the internal JAXB object into the final XML String, validates it against the .xsd
   * files and returns the xml string if valid. Otherwise, throws an Exception.
   */
  public String build() {
    try {
      JAXBContext context = JAXBContext.newInstance(AkomaNtoso.class);
      Marshaller marshaller = context.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, SCHEMA_LOCATION);
      marshaller.setProperty(
          "org.glassfish.jaxb.namespacePrefixMapper",
          new NamespacePrefixMapper() {
            @Override
            public String getPreferredPrefix(
                String namespaceUri, String suggestion, boolean requirePrefix) {
              if (AKN_NS.equals(namespaceUri)) return "akn";
              if (RIS_NS.equals(namespaceUri)) return "ris";
              return suggestion;
            }
          });

      StringWriter writer = new StringWriter();
      marshaller.marshal(this.document, writer);
      String xml = writer.toString();
      //      XmlValidator.validateNormXml(xml, "regelungstext");

      return xml;
    } catch (Exception e) {
      throw new RuntimeException("Failed to generate XML from XSD classes in Test Builder", e);
    }
  }
}
