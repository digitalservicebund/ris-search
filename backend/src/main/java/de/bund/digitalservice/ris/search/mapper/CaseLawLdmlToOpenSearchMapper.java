package de.bund.digitalservice.ris.search.mapper;

import static de.bund.digitalservice.ris.search.utils.MappingUtils.nullSafeGet;
import static de.bund.digitalservice.ris.search.utils.MappingUtils.validateNotNull;

import de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml.AknKeyword;
import de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml.Analysis;
import de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml.CaseLawLdml;
import de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml.DocumentaryShortTexts;
import de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml.FrbrDate;
import de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml.FrbrElement;
import de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml.FrbrThis;
import de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml.Identification;
import de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml.ImplicitReference;
import de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml.JaxbHtml;
import de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml.Judgment;
import de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml.JudgmentBody;
import de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml.LinkedJudgement;
import de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml.Meta;
import de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml.OtherAnalysis;
import de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml.OtherReferences;
import de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml.Proprietary;
import de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml.RisGericht;
import de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml.RisMeta;
import de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml.Spruchkoerper;
import de.bund.digitalservice.ris.search.exception.OpenSearchMapperException;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.utils.DateUtils;
import de.bund.digitalservice.ris.search.utils.MappingUtils;
import jakarta.xml.bind.DataBindingException;
import jakarta.xml.bind.JAXB;
import jakarta.xml.bind.ValidationException;
import java.io.StringReader;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.xml.transform.stream.StreamSource;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.springframework.stereotype.Service;

/**
 * This service class is responsible for mapping `CaseLawLdml` instances to
 * `CaseLawDocumentationUnit` entities, ensuring that data from the LDML format is accurately
 * converted into the OpenSearch domain model.
 *
 * <p>The class contains methods for performing validation, data transformation, and consistent
 * object creation necessary for indexing legal case data.
 */
@Service
public class CaseLawLdmlToOpenSearchMapper {

  /**
   * Maps a {@link CaseLawLdml} object to a {@link CaseLawDocumentationUnit} entity. The method
   * performs validation checks on mandatory fields and ensures all necessary data is present.
   *
   * @param caseLawLdml the input object containing case law data in LDML format
   * @return an entity of type {@link CaseLawDocumentationUnit} containing the mapped information
   * @throws ValidationException if any required fields in the input object are missing or invalid
   */
  public CaseLawDocumentationUnit mapToEntity(CaseLawLdml caseLawLdml) throws ValidationException {

    // Judgment
    validateNotNull(caseLawLdml.getJudgment(), "Judgment missing");
    Judgment judgment = caseLawLdml.getJudgment();
    validateNotNull(judgment.getMeta(), "Meta missing");
    Meta meta = judgment.getMeta();

    // Judgement Body
    validateNotNull(judgment.getJudgmentBody(), "JudgmentBody missing");
    JudgmentBody judgmentBody = judgment.getJudgmentBody();

    return CaseLawDocumentationUnit.builder()
        // Meta elements
        .id(extractUniqueIdentifier(caseLawLdml))
        .documentNumber(extractUniqueIdentifier(caseLawLdml))
        .ecli(extractEcli(caseLawLdml).orElse(null))
        .decisionDate(DateUtils.nullSafeParseyyyyMMdd(extractFrbrDate(caseLawLdml).getDate()))
        .fileNumbers(getFileNumbers(caseLawLdml))
        .courtType(getCourtType(caseLawLdml))
        .location(getCourtLocation(caseLawLdml))
        .documentType(getDocumentType(caseLawLdml))
        .judicialBody(getJudicialBody(caseLawLdml))
        .courtKeyword(getCourtKeyword(caseLawLdml))
        .keywords(
            nullSafeGet(
                meta.getClassification(),
                e ->
                    nullSafeGet(
                        e.getKeyword(), f -> f.stream().map(AknKeyword::getValue).toList())))
        .decisionName(extractDecisionNames(caseLawLdml))
        .deviatingDocumentNumber(getDeviatingDocumentNumber(caseLawLdml))
        .documentationOffice(getDocumentationOffice(caseLawLdml))
        .legalEffect(getLegalEffect(caseLawLdml))

        // Visible elements (in display order)
        .headline(jaxbToSanitizedHtml(getShortTitle(caseLawLdml)))
        .guidingPrinciple(
            jaxbToSanitizedHtml(
                judgmentBody.getIntroductionEntryContentByName("Leitsatz").orElse(null)))
        .headnote(jaxbToSanitizedHtml(extractHeadnote(caseLawLdml).orElse(null)))
        .otherHeadnote(jaxbToSanitizedHtml(extractOtherHeadnote(caseLawLdml).orElse(null)))
        .outline(
            jaxbToSanitizedHtml(
                judgmentBody.getIntroductionEntryContentByName("Gliederung").orElse(null)))
        .tenor(jaxbToSanitizedHtml(judgmentBody.getDecision()))
        .caseFacts(jaxbToSanitizedHtml(judgmentBody.getBackground()))
        .decisionGrounds(
            jaxbToSanitizedHtml(
                judgmentBody.getMotivationEntryContentByName("Entscheidungsgründe").orElse(null)))
        .grounds(
            jaxbToSanitizedHtml(
                judgmentBody.getMotivationEntryContentByName("Gründe").orElse(null)))
        .otherLongText(
            jaxbToSanitizedHtml(
                judgmentBody.getMotivationEntryContentByName("Sonstiger Langtext").orElse(null)))
        .dissentingOpinion(jaxbToSanitizedHtml(null))
        .previousDecisions(
            getLinkedJudgements(
                caseLawLdml,
                refs -> refs.getReferencesByType(ImplicitReference::getPrecedingJudgement)))
        .ensuingDecisions(
            getLinkedJudgements(
                caseLawLdml,
                refs -> refs.getReferencesByType(ImplicitReference::getEnsuingJudgement)))
        // Internal (portal team) fields
        .indexedAt(Instant.now().toString())
        .articles(null)
        .build();
  }

  /**
   * Converts a given LDML file content represented as a string into a {@link
   * CaseLawDocumentationUnit}.
   *
   * @param ldmlFile the string representation of the LDML file to be converted
   * @return a {@link CaseLawDocumentationUnit} instance created from the provided LDML file string
   * @throws OpenSearchMapperException if the LDML file cannot be parsed into a {@link
   *     CaseLawDocumentationUnit}
   */
  public CaseLawDocumentationUnit fromString(String ldmlFile) {
    try {
      StreamSource ldmlStreamSource = new StreamSource(new StringReader(ldmlFile));
      CaseLawLdml ldml = JAXB.unmarshal(ldmlStreamSource, CaseLawLdml.class);
      return mapToEntity(ldml);
    } catch (DescriptorException | DataBindingException | ValidationException e) {
      throw new OpenSearchMapperException("unable to parse file to DocumentationUnit", e);
    }
  }

  private static String extractUniqueIdentifier(CaseLawLdml caseLawLdml)
      throws ValidationException {
    return Optional.ofNullable(caseLawLdml)
        .map(CaseLawLdml::getJudgment)
        .map(Judgment::getMeta)
        .map(Meta::getIdentification)
        .map(Identification::getFrbrWork)
        .map(FrbrElement::getFrbrThis)
        .map(FrbrThis::getValue)
        .filter(s -> !s.isBlank())
        .orElseThrow(() -> new ValidationException("Case Law LDML has no documentNumber"));
  }

  private static Optional<String> extractEcli(CaseLawLdml caseLawLdml) {
    return Optional.ofNullable(caseLawLdml)
        .map(CaseLawLdml::getJudgment)
        .map(Judgment::getMeta)
        .map(Meta::getIdentification)
        .map(Identification::getFrbrWork)
        .map(FrbrElement::getEcliAliasValue);
  }

  private static FrbrDate extractFrbrDate(CaseLawLdml caseLawLdml) throws ValidationException {
    return Optional.ofNullable(caseLawLdml)
        .map(CaseLawLdml::getJudgment)
        .map(Judgment::getMeta)
        .map(Meta::getIdentification)
        .map(Identification::getFrbrWork)
        .map(FrbrElement::getFrbrDate)
        .filter(frbrDate -> !frbrDate.getDate().isBlank())
        .orElseThrow(() -> new ValidationException("Case Law LDML has no date"));
  }

  private static Optional<DocumentaryShortTexts> extractDocumentaryShortTexts(
      CaseLawLdml caseLawLdml) {
    return Optional.ofNullable(caseLawLdml)
        .map(CaseLawLdml::getJudgment)
        .map(Judgment::getMeta)
        .map(Meta::getAnalysis)
        .map(Analysis::getOtherAnalysis)
        .map(OtherAnalysis::getDocumentaryShortTexts);
  }

  private static Optional<JaxbHtml> extractHeadnote(CaseLawLdml caseLawLdml) {
    Optional<DocumentaryShortTexts> docShortTexts = extractDocumentaryShortTexts(caseLawLdml);
    return Optional.ofNullable(
        docShortTexts
            .map(DocumentaryShortTexts::getRisOrientierungssatz)
            .map(note -> note.getContent())
            .orElse(null));
  }

  private static Optional<JaxbHtml> extractOtherHeadnote(CaseLawLdml caseLawLdml) {
    Optional<DocumentaryShortTexts> docShortTexts = extractDocumentaryShortTexts(caseLawLdml);
    return Optional.ofNullable(
        docShortTexts
            .map(DocumentaryShortTexts::getRisSonstigerOrientierungssatz)
            .map(note -> note.getContent())
            .orElse(null));
  }

  private static List<String> extractDecisionNames(CaseLawLdml caseLawLdml) {
    Optional<DocumentaryShortTexts> docShortTexts = extractDocumentaryShortTexts(caseLawLdml);
    return docShortTexts
        .map(DocumentaryShortTexts::getRisEntscheidungsNames)
        .orElse(Collections.emptyList())
        .stream()
        .map(DocumentaryShortTexts.RisEntscheidungsName::getName)
        .toList();
  }

  private static List<String> getLinkedJudgements(
      CaseLawLdml caseLawLdml, Function<OtherReferences, List<LinkedJudgement>> extractor) {
    return Optional.ofNullable(caseLawLdml)
        .map(CaseLawLdml::getJudgment)
        .map(Judgment::getMeta)
        .map(Meta::getAnalysis)
        .map(Analysis::getOtherReferences)
        .map(extractor)
        .stream()
        .flatMap(Collection::stream)
        .filter(Objects::nonNull)
        .map(LinkedJudgement::asString)
        .collect(Collectors.toList());
  }

  private static JaxbHtml getShortTitle(CaseLawLdml caseLawLdml) throws ValidationException {
    return Optional.ofNullable(caseLawLdml)
        .map(CaseLawLdml::getJudgment)
        .map(Judgment::getHeader)
        .flatMap(header -> Optional.ofNullable(header.findShortTitle()))
        .orElseThrow(() -> new ValidationException("Header or Short title missing"));
  }

  private static RisMeta getRisMeta(CaseLawLdml ldml) throws ValidationException {
    return Optional.ofNullable(ldml)
        .map(CaseLawLdml::getJudgment)
        .map(Judgment::getMeta)
        .map(Meta::getProprietary)
        .map(Proprietary::getMeta)
        .orElseThrow(
            () -> new ValidationException("Metadata structure (Proprietary/RisMeta) is missing"));
  }

  private static String getDocumentType(CaseLawLdml ldml) throws ValidationException {
    return Optional.of(getRisMeta(ldml).getRisDokumentTyp())
        .orElseThrow(() -> new ValidationException("DocumentType missing"));
  }

  private static String getCourtType(CaseLawLdml ldml) throws ValidationException {
    return Optional.of(getRisMeta(ldml))
        .map(RisMeta::getRisGericht)
        .map(RisGericht::getGerichtstyp)
        .orElseThrow(() -> new ValidationException("CourtType missing"));
  }

  private static String getCourtKeyword(CaseLawLdml ldml) throws ValidationException {
    return Optional.of(getRisMeta(ldml)).map(RisMeta::getCourtKeyword).orElse(null);
  }

  private static List<String> getDeviatingDocumentNumber(CaseLawLdml ldml)
      throws ValidationException {
    return Optional.of(getRisMeta(ldml))
        .map(RisMeta::getRisAbweichendeDokumentnummern)
        .orElse(null);
  }

  private static String getCourtLocation(CaseLawLdml ldml) throws ValidationException {
    return Optional.of(getRisMeta(ldml))
        .map(RisMeta::getRisGericht)
        .map(RisGericht::getGerichtsort)
        .orElse(null);
  }

  private static String getJudicialBody(CaseLawLdml ldml) throws ValidationException {
    return Optional.of(getRisMeta(ldml))
        .map(RisMeta::getRisGericht)
        .map(RisGericht::getSpruchkoerper)
        .map(Spruchkoerper::getValue)
        .orElse(null);
  }

  private static List<String> getFileNumbers(CaseLawLdml ldml) throws ValidationException {
    List<String> fileNumbers = getRisMeta(ldml).getAktenzeichen();
    if (fileNumbers == null || fileNumbers.isEmpty()) {
      throw new ValidationException("FileNumber missing");
    }
    return fileNumbers;
  }

  private static String getDocumentationOffice(CaseLawLdml ldml) throws ValidationException {
    return getRisMeta(ldml).getRisDokumentationsstelle();
  }

  private static String getLegalEffect(CaseLawLdml ldml) throws ValidationException {
    return getRisMeta(ldml).getRisRechtskraft();
  }

  private static String jaxbToSanitizedHtml(JaxbHtml html) {
    if (html == null) {
      return null;
    }
    return MappingUtils.sanitizeHtmlFromString(html.toHtmlString());
  }
}
