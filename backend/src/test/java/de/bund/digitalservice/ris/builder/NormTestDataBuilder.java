package de.bund.digitalservice.ris.builder;

import de.bund.digitalservice.ris.builder.models.Act;
import de.bund.digitalservice.ris.builder.models.AkomaNtoso;
import de.bund.digitalservice.ris.builder.models.Conclusions;
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
import de.bund.digitalservice.ris.builder.models.meta.identification.FRBRWork;
import de.bund.digitalservice.ris.builder.models.meta.identification.Identification;
import de.bund.digitalservice.ris.builder.models.meta.lifecycle.Lifecycle;
import de.bund.digitalservice.ris.builder.models.preamble.Preamble;
import de.bund.digitalservice.ris.builder.models.preamble.Toc;
import de.bund.digitalservice.ris.builder.models.preface.DocTitle;
import de.bund.digitalservice.ris.builder.models.preface.LongTitle;
import de.bund.digitalservice.ris.builder.models.preface.Preface;
import de.bund.digitalservice.ris.builder.models.preface.ShortTitle;
import de.bund.digitalservice.ris.utils.NormXmlValidator;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.eclipse.persistence.oxm.NamespacePrefixMapper;

/**
 * This class allows to construct norms for testing purposes using the builder pattern.
 *
 * <p><b>Usage notes:</b>
 *
 * <ul>
 *   <li>The builder validates the created xml string using the norm xsd files. If the validation
 *       fails the error message says what element/attribtue is causing the issue. For debugging, it
 *       helps to comment out the line where the validation is done and then look at the generated
 *       xml to see what is wrong.
 *   <li>It does not 100% implement the eId logic correctly. E.g. in many places the eId is not
 *       constructed based in the parent eId but simply set to the minimum required string to
 *       fulfill the schema validation. The application does not rely on the eId for almost all the
 *       field extractions, so this is fine. In places where the eId is important (e.g. Articles and
 *       Temporal Data) it is automatically constructed or the builder gives the option to manually
 *       set the eId.
 * </ul>
 */
public class NormTestDataBuilder {

  public static final String AKN_NS = "http://Inhaltsdaten.LegalDocML.de/1.8.2/";
  public static final String RIS_NS = "http://MetadatenRIS.LegalDocML.de/1.8.2/";

  private static final String COMMON_SCHEMA_LOCATIONS =
      "http://MetadatenRIS.LegalDocML.de/1.8.2/ ../ris-norms-ldml-schema-extensions/1.8.2/legalDocML.de-metadaten-ris.xsd"
          + "http://MetadatenRegelungstext.LegalDocML.de/1.8.2/ Grammatiken/legalDocML.de-metadaten-regelungstext.xsd"
          + "http://MetadatenRechtsetzungsdokument.LegalDocML.de/1.8.2/ Grammatiken/legalDocML.de-metadaten-rechtsetzungsdokument.xsd";

  private static final String OFFENE_STRUKTUR_SCHEMA_LOCATIONS =
      COMMON_SCHEMA_LOCATIONS
          + "http://Inhaltsdaten.LegalDocML.de/1.8.2/ Grammatiken/legalDocML.de-offenestruktur.xsd";

  private static final String REGELUNGSTEXT_SCHEMA_LOCATION =
      COMMON_SCHEMA_LOCATIONS
          + "http://Inhaltsdaten.LegalDocML.de/1.8.2/ Grammatiken/legalDocML.de-regelungstextverkuendungsfassung.xsd";

  private final AkomaNtoso document = new AkomaNtoso();
  private final Map<String, AkomaNtoso> attachmentsDocs = new HashMap<>();
  private boolean enforceValidation;

  private NormTestDataBuilder(boolean enforceValidation) {
    this.enforceValidation = enforceValidation;
    this.document.setAct(Act.builder().meta(Meta.builder().build()).build());
  }

  public static NormTestDataBuilder builder() {
    return new NormTestDataBuilder(true);
  }

  /**
   * Disables schema validation on the generated xml. ONLY use this for test cases that require
   * invalid xml. E.g. to test that extraction fails on certain elements missing.
   *
   * @return builder
   */
  public NormTestDataBuilder disableValidation() {
    enforceValidation = false;
    return this;
  }

  /**
   * Sets the norm's ELI-based identification (FRBRWork, FRBRExpression and FRBRManifestation).
   *
   * @param manifestationEli the manifestation ELI, e.g.
   *     "eli/bund/bgbl-1/1991/s102/1991-01-01/1/deu/1991-01-01/regelungstext-1.xml"
   * @return this builder for chaining
   */
  public NormTestDataBuilder eli(String manifestationEli) {
    this.document.getAct().getMeta().setIdentification(Identification.fromEli(manifestationEli));
    return this;
  }

  /**
   * Sets the FRBRWork's FRBRUri field
   *
   * @param workEli the work ELI, e.g. "eli/bund/bgbl-1/1991/s102/"
   * @return this builder for chaining
   */
  public NormTestDataBuilder workEli(String workEli) {
    this.document
        .getAct()
        .getMeta()
        .getIdentification()
        .getFrbrWork()
        .getFrbrUri()
        .setValue(workEli);
    return this;
  }

  /**
   * Sets the FRBRExpression's FRBRUri field
   *
   * @param expressionEli the expression ELI, e.g. "eli/bund/bgbl-1/1991/s102/2025-11-18/1/deu"
   * @return this builder for chaining
   */
  public NormTestDataBuilder expressionEli(String expressionEli) {
    this.document.getAct().getMeta().getIdentification().getFrbrExpression().setUri(expressionEli);
    return this;
  }

  /**
   * Sets the FRBRManifestation's FRBRUri field
   *
   * @param manifestationEli the manifestation ELI, e.g.
   *     "eli/bund/bgbl-1/1991/s102/2025-11-18/1/deu/2025-11-26/regelungstext-verkuendung-1.xml"
   * @return this builder for chaining
   */
  public NormTestDataBuilder manifestationEli(String manifestationEli) {
    this.document
        .getAct()
        .getMeta()
        .getIdentification()
        .getFrbrManifestation()
        .setThis(manifestationEli);
    return this;
  }

  /**
   * Can be used to make individual settings on the @FRBRWork
   *
   * @param workConsumer that provides the document FRBRWork object
   * @return this builder for chaining
   */
  public NormTestDataBuilder frbrWork(Consumer<FRBRWork> workConsumer) {
    FRBRWork work = this.document.getAct().getMeta().getIdentification().getFrbrWork();
    workConsumer.accept(work);
    return this;
  }

  /**
   * Sets the norm's official title (Langtitel) shown in the preface.
   *
   * @param officialTitle the official title text
   * @return this builder for chaining
   */
  public NormTestDataBuilder officialTitle(String officialTitle) {
    this.officialTitle(officialTitle, null);

    return this;
  }

  /**
   * Sets the norm's official title (Langtitel) shown in the preface and provides the title for
   * further actions.
   *
   * @param officialTitle the official title text
   * @param longTitleConsumer callback which provided the LongTitle object for fruther actions
   * @return this builder for chaining
   */
  public NormTestDataBuilder officialTitle(
      String officialTitle, Consumer<LongTitle> longTitleConsumer) {
    LongTitle longTitle = this.document.getAct().getPreface().getLongTitle();

    longTitle.setOfficialTitle(officialTitle);
    if (longTitleConsumer != null) longTitleConsumer.accept(longTitle);

    return this;
  }

  /**
   * Sets the norm's short title (Kurztitel) in the preface.
   *
   * @param title the short title text
   * @param suffix optional suffix appended after the title, or {@code null} for none
   * @return this builder for chaining
   */
  public NormTestDataBuilder shortTitle(String title, String suffix) {
    ShortTitle shortTitle = this.document.getAct().getPreface().getLongTitle().getShortTitle();
    this.document
        .getAct()
        .getPreface()
        .getLongTitle()
        .setShortTitle(shortTitle.withTitle(title, suffix));

    return this;
  }

  /**
   * Sets the norm's official abbreviation (amtliche Abkürzung) in the preface.
   *
   * @param abbreviation the official abbreviation text
   * @return this builder for chaining
   */
  public NormTestDataBuilder officialAbbreviation(String abbreviation) {
    ShortTitle shortTitle = this.document.getAct().getPreface().getLongTitle().getShortTitle();
    this.document
        .getAct()
        .getPreface()
        .getLongTitle()
        .setShortTitle(shortTitle.withOfficialAbbreviation(abbreviation));

    return this;
  }

  /**
   * Sets the norm's RIS in-force date ({@code ris:inkraft}).
   *
   * @param inForceDate date in the format YYYY-MM-DD
   * @return this builder for chaining
   */
  public NormTestDataBuilder inForceDate(String inForceDate) {
    this.document.getAct().getMeta().getProprietary().getRisMetadata().setInForce(inForceDate);
    return this;
  }

  /**
   * Sets the norm's RIS out-of-force date ({@code ris:ausserkraft}).
   *
   * @param outOfForceDate date in the format YYYY-MM-DD
   * @return this builder for chaining
   */
  public NormTestDataBuilder outOfForceDate(String outOfForceDate) {
    this.document
        .getAct()
        .getMeta()
        .getProprietary()
        .getRisMetadata()
        .setOutOfForce(outOfForceDate);
    return this;
  }

  /**
   * Sets the norms Ausfertigungsdatum
   *
   * @param date date in the format YYYY-MM-DD
   * @return this builder for chaining
   */
  public NormTestDataBuilder legislationDate(String date) {
    this.document.getAct().getPreface().setLegislationDate(date);
    return this;
  }

  /**
   * Sets the norms Verkuendungsdatum
   *
   * @param date date in the format YYYY-MM-DD
   * @return this builder for chaining
   */
  public NormTestDataBuilder datePublished(String date) {
    this.document.getAct().getMeta().getIdentification().getFrbrWork().setDatePublished(date);
    return this;
  }

  /**
   * Sets the norm's internal RIS abbreviation ({@code ris:abkuerzung}).
   *
   * @param internalAbbreviation the internal abbreviation text
   * @return this builder for chaining
   */
  public NormTestDataBuilder risAbbreviation(String internalAbbreviation) {
    this.document
        .getAct()
        .getMeta()
        .getProprietary()
        .getRisMetadata()
        .setAbbreviation(internalAbbreviation);
    return this;
  }

  /**
   * Sets the norm's full citation ({@code ris:vollzitat}).
   *
   * @param citation the full citation text
   * @return this builder for chaining
   */
  public NormTestDataBuilder fullCitation(String citation) {
    this.document.getAct().getMeta().getProprietary().getRisMetadata().setFullCitation(citation);
    return this;
  }

  /**
   * Adds a ris metadata ({@code ris:bedingtesInkrafttreten}) element.
   *
   * @return this builder for chaining
   */
  public NormTestDataBuilder bedingtesInkrafttreten() {
    this.document.getAct().getMeta().getProprietary().getRisMetadata().setBedingtesInkrafttreten();
    return this;
  }

  /**
   * Adds a ris metadata ({@code ris:gegenstandslos}) element.
   *
   * @return this builder for chaining
   */
  public NormTestDataBuilder gegenstandslos() {
    this.document.getAct().getMeta().getProprietary().getRisMetadata().setGegenstandslos();
    return this;
  }

  /**
   * Adds an enacting formula (Eingangsformel) to the norm's preamble.
   *
   * @param text the formula text
   * @return this builder for chaining
   */
  public NormTestDataBuilder formula(String text) {
    Preamble preamble =
        Optional.ofNullable(this.document.getAct().getPreamble())
            .orElse(Preamble.builder().build());
    preamble.addFormula(text);
    this.document.getAct().setPreamble(preamble);

    return this;
  }

  /**
   * Adds a table of contents to the norm's preamble and lets the caller populate it.
   *
   * @param tocConsumer callback used to add entries to the created {@link Toc}
   * @return this builder for chaining
   */
  public NormTestDataBuilder toc(Consumer<Toc> tocConsumer) {
    Preamble preamble =
        Optional.ofNullable(this.document.getAct().getPreamble())
            .orElse(Preamble.builder().build());
    Toc toc = preamble.addToc();
    tocConsumer.accept(toc);

    this.document.getAct().setPreamble(preamble);

    return this;
  }

  /**
   * Adds a chapter to the norm's body and lets the caller populate it.
   *
   * @param heading the chapter heading text
   * @param num the chapter number, e.g. "Kapitel 1"
   * @param chapterConsumer callback used to populate the created {@link Chapter}
   * @return this builder for chaining
   */
  public NormTestDataBuilder chapter(
      String heading, String num, Consumer<Chapter> chapterConsumer) {
    Chapter chapter = new Chapter().addHeading(heading).addNum(num);
    chapterConsumer.accept(chapter);
    this.document.getAct().getBody().addChild(chapter);
    return this;
  }

  /**
   * Adds a section to the norm's body and lets the caller populate it.
   *
   * @param heading the section heading text
   * @param num the section number, e.g. "Abschnitt 1"
   * @param sectionConsumer callback used to populate the created {@link Section}
   * @return this builder for chaining
   */
  public NormTestDataBuilder section(
      String heading, String num, Consumer<Section> sectionConsumer) {
    Section section = new Section().addHeading(heading).addNum(num);
    sectionConsumer.accept(section);
    this.document.getAct().getBody().addChild(section);
    return this;
  }

  /**
   * Adds an article to the norm's body and lets the caller populate it.
   *
   * @param num the article number, e.g. "§ 1"
   * @param startDate date the article starts being valid
   * @param endDate date the article stops being valid
   * @param eId eId of the article, e.g. "art-z1"
   * @param articleConsumer callback used to populate the created {@link Article}
   * @return this builder for chaining
   */
  public NormTestDataBuilder article(
      String num, String startDate, String endDate, String eId, Consumer<Article> articleConsumer) {
    Article article = buildArticle(num, startDate, endDate, eId);
    articleConsumer.accept(article);
    this.document.getAct().getBody().addChild(article);
    return this;
  }

  /**
   * Creates the article element and the separate temporal group and lifecycle events and links them
   * together.
   *
   * @param num e.g. "§ 1"
   * @param startDate date the Article starts being valid
   * @param endDate date the Article stops being valid
   * @param eId eId of the article e.g. "art-z1"
   * @return Article
   */
  public Article buildArticle(String num, String startDate, String endDate, String eId) {
    Lifecycle lifecycle = this.document.getAct().getMeta().getLifecycle();
    String inForceEventEId = lifecycle.addInForceEvent(startDate);
    String outOfForceEventEId = lifecycle.addOutOfForce(endDate);

    String temporalGroupEId =
        this.document
            .getAct()
            .getMeta()
            .getTemporalData()
            .addTemporalGroup(inForceEventEId, outOfForceEventEId);

    return Article.builder().eId(eId).period("#" + temporalGroupEId).build().addNum(num);
  }

  /**
   * Adds a single default article with one heading and one paragraph to the norm's body.
   *
   * @return this builder for chaining
   */
  public NormTestDataBuilder defaultArticle() {
    Article article =
        this.buildArticle("§ 1", "2025-01-01", "2025-07-01", "art-z1")
            .addHeading("Article number one", null)
            .addParagraph("Paragraph one", "(1)");

    this.document.getAct().getBody().addChild(article);
    return this;
  }

  /**
   * Adds an attachment (Anlage) to the norm, together with its own AkomaNtoso document.
   *
   * @param manifestationEli the manifestation ELI of the attachment
   * @param num the attachment number shown in its title
   * @param bezug the reference text shown in its title
   * @param heading the heading text shown in its title
   * @param mainBodyChildren the body content of the attachment document
   * @return this builder for chaining
   */
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
    attachmentsDocs.put(manifestationEli, attachment);

    return this;
  }

  /**
   * Sets the norm's closing formula (Schlussformel).
   *
   * @param text the conclusion text
   * @return this builder for chaining
   */
  public NormTestDataBuilder conclusion(String text) {
    this.document.getAct().setConclusions(Conclusions.withText(text));
    return this;
  }

  /**
   * Marshals the norm into its validated Regelungstext XML representation.
   *
   * @return the norm as an XML string
   */
  public String buildNormXml() {
    return this.marshallDocument(
        this.document, REGELUNGSTEXT_SCHEMA_LOCATION, NormXmlValidator.Type.REGELUNGSTEXT);
  }

  /**
   * Returns a map with the attachment XMLs where the keys are the manifestationElis and the values
   * are the xml strings.
   *
   * @return map of manifestationElis to attachments
   */
  public Map<String, String> buildAttachmentXmls() {

    return this.attachmentsDocs.entrySet().stream()
        .collect(
            Collectors.toMap(
                Map.Entry::getKey,
                entry ->
                    marshallDocument(
                        entry.getValue(),
                        OFFENE_STRUKTUR_SCHEMA_LOCATIONS,
                        NormXmlValidator.Type.ANLAGE)));
  }

  /**
   * Converts the internal JAXB object into the final XML String, validates it against the .xsd
   * files and returns the xml string if valid. Otherwise, throws an Exception.
   */
  private String marshallDocument(
      AkomaNtoso document, String schemaLocations, NormXmlValidator.Type docType) {
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
      if (enforceValidation) {
        NormXmlValidator.validateContent(xml, docType);
      }

      return xml;
    } catch (Exception e) {
      throw new IllegalArgumentException("Failed to generate XML", e);
    }
  }
}
