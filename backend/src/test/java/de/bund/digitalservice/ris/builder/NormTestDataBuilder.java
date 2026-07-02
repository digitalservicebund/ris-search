package de.bund.digitalservice.ris.builder;

import de.bund.digitalservice.ris.builder.models.Act;
import de.bund.digitalservice.ris.builder.models.AkomaNtoso;
import de.bund.digitalservice.ris.builder.models.Doc;
import de.bund.digitalservice.ris.builder.models.attachment.Attachments;
import de.bund.digitalservice.ris.builder.models.body.Article;
import de.bund.digitalservice.ris.builder.models.body.Body;
import de.bund.digitalservice.ris.builder.models.body.BodyElement;
import de.bund.digitalservice.ris.builder.models.body.Chapter;
import de.bund.digitalservice.ris.builder.models.body.Section;
import de.bund.digitalservice.ris.builder.models.common.Block;
import de.bund.digitalservice.ris.builder.models.common.Inline;
import de.bund.digitalservice.ris.builder.models.meta.Meta;
import de.bund.digitalservice.ris.builder.models.meta.identification.Identification;
import de.bund.digitalservice.ris.builder.models.meta.lifecycle.Lifecycle;
import de.bund.digitalservice.ris.builder.models.preamble.Preamble;
import de.bund.digitalservice.ris.builder.models.preface.DocTitle;
import de.bund.digitalservice.ris.builder.models.preface.LongTitle;
import de.bund.digitalservice.ris.builder.models.preface.Preface;
import de.bund.digitalservice.ris.builder.models.preface.ShortTitle;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import org.eclipse.persistence.oxm.NamespacePrefixMapper;

/**
 * This class allows to construct norms for testing purposes using the builder pattern.
 *
 * <p><b>Important notes:</b>
 *
 * <ul>
 *   <li>it does not 100% implement the eId logic correctly. E.g. when adding chapter, sections etc.
 *       the eId is not calculated but set to a fixed value (which still fulfills the xsd
 *       validation). This does not cause issues as long as the production logic does not rely on
 *       the eIds. In cases where the eId is important it needs to be set manually when creating the
 *       chapter, section etc.
 * </ul>
 */
public class NormTestDataBuilder {

  public static final String AKN_NS = "http://Inhaltsdaten.LegalDocML.de/1.8.2/";
  public static final String RIS_NS = "http://MetadatenRIS.LegalDocML.de/1.8.2/";

  private static final String COMMON_SCHEMA_LOCATIONS =
      "http://MetadatenRIS.LegalDocML.de/1.8.2/ ../ris-norms-ldml-schema-extensions/1.8.2/legalDocML.de-metadaten-ris.xsd"
          + "http://MetadatenRegelungstext.LegalDocML.de/1.8.2/ Grammatiken/Norms/legalDocML.de-metadaten-regelungstext.xsd"
          + "http://MetadatenRechtsetzungsdokument.LegalDocML.de/1.8.2/ Grammatiken/legalDocML.de-metadaten-rechtsetzungsdokument.xsd";

  private static final String OFFENE_STRUKTUR_SCHEMA_LOCATIONS =
      COMMON_SCHEMA_LOCATIONS
          + "http://Inhaltsdaten.LegalDocML.de/1.8.2/ Grammatiken/legalDocML.de-offenestruktur.xsd";

  private static final String REGELUNGSTEXT_SCHEMA_LOCATION =
      COMMON_SCHEMA_LOCATIONS
          + "http://Inhaltsdaten.LegalDocML.de/1.8.2/ Grammatiken/legalDocML.de-regelungstextverkuendungsfassung.xsd";

  private final AkomaNtoso document = new AkomaNtoso();
  private final List<AkomaNtoso> attachmentsDocs = new ArrayList<>();

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

  public NormTestDataBuilder shortTitle(String title, String suffix) {
    ShortTitle shortTitle = this.document.getAct().getPreface().getLongTitle().getShortTitle();
    this.document
        .getAct()
        .getPreface()
        .setLongTitle(
            this.document
                .getAct()
                .getPreface()
                .getLongTitle()
                .withShortTitle(shortTitle.withTitle(title, suffix)));

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

  public NormTestDataBuilder formula(String text) {
    Preamble preamble =
        Optional.ofNullable(this.document.getAct().getPreamble())
            .orElse(Preamble.builder().build());
    preamble.addFormula(text);
    this.document.getAct().setPreamble(preamble);

    return this;
  }

  public NormTestDataBuilder chapter(
      String heading, String num, Consumer<Chapter> chapterConsumer) {
    Chapter chapter = new Chapter().addHeading(heading).addNum(num);
    chapterConsumer.accept(chapter);
    this.document.getAct().getBody().addChild(chapter);
    return this;
  }

  public NormTestDataBuilder section(
      String heading, String num, Consumer<Section> sectionConsumer) {
    Section section = new Section().addHeading(heading).addNum(num);
    sectionConsumer.accept(section);
    this.document.getAct().getBody().addChild(section);
    return this;
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

  public NormTestDataBuilder attachment(
      String manifestationEli,
      String num,
      String bezug,
      String heading,
      List<BodyElement> mainBodyChildren) {
    DocTitle attachmentTitle =
        DocTitle.builder()
            .eId("einleitung-n1_block-n1_doctitel-n1")
            .children(
                List.of(
                    Inline.builder()
                        .eId("einleitung-n1_block-n1_doctitel-n1_inline-n1")
                        .refersTo("anlageregelungstext-num")
                        .content(num)
                        .build(),
                    Inline.builder()
                        .eId("einleitung-n1_block-n1_doctitel-n1_inline-n2")
                        .refersTo("anlageregelungstext-bezug")
                        .content(bezug)
                        .build(),
                    Inline.builder()
                        .eId("einleitung-n1_block-n1_doctitel-n1_inline-n3")
                        .refersTo("anlageregelungstext-heading")
                        .content(heading)
                        .build()))
            .build();

    AkomaNtoso attachment = new AkomaNtoso();
    attachment.setDoc(
        Doc.builder()
            .meta(
                Meta.builder()
                    .lifecycle(null)
                    .temporalData(null)
                    .proprietary(null)
                    .identification(Identification.fromEli(manifestationEli))
                    .build())
            .preface(
                Preface.builder()
                    .longTitle(null)
                    .block(
                        Block.builder()
                            .eId("einleitung-n1_block-n1")
                            .children(List.of(attachmentTitle))
                            .build())
                    .build())
            .body(Body.builder().children(mainBodyChildren).build())
            .build());

    Attachments attachments =
        Optional.ofNullable(this.document.getAct().getAttachments())
            .orElse(Attachments.builder().build());

    attachments.addAttachment(
        manifestationEli, String.valueOf(attachments.getAttachmentCount() + 1));
    this.document.getAct().setAttachments(attachments);
    attachmentsDocs.add(attachment);

    return this;
  }

  public String buildNormXml() {
    return this.marshallDocument(this.document, REGELUNGSTEXT_SCHEMA_LOCATION, "regelungstext");
  }

  public List<String> buildAttachmentXmls() {

    return this.attachmentsDocs.stream()
        .map(doc -> marshallDocument(doc, OFFENE_STRUKTUR_SCHEMA_LOCATIONS, "anlage-regelungstext"))
        .toList();
  }

  /**
   * Converts the internal JAXB object into the final XML String, validates it against the .xsd
   * files and returns the xml string if valid. Otherwise, throws an Exception.
   */
  private String marshallDocument(AkomaNtoso document, String schemaLocations, String docType) {
    try {
      JAXBContext context = JAXBContext.newInstance(AkomaNtoso.class);
      Marshaller marshaller = context.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, schemaLocations);
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
      marshaller.marshal(document, writer);
      String xml = writer.toString();
      XmlValidator.validateNormXml(xml, docType);

      return xml;
    } catch (Exception e) {
      throw new RuntimeException("Failed to generate XML from XSD classes in Test Builder", e);
    }
  }
}
