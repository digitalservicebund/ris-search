package de.bund.digitalservice.ris.search.mapper;

import static de.bund.digitalservice.ris.search.utils.MappingUtils.validate;
import static de.bund.digitalservice.ris.search.utils.MappingUtils.validateNotNull;

import de.bund.digitalservice.ris.search.exception.OpenSearchMapperException;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.AknKeyword;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.AknMainContentIntroduction;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.AknMainContentMotivation;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.Analysis;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.CaseLawLdml;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.DocumentaryShortTexts;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.FrbrElement;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.ImplicitReference;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.JaxbHtml;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.Judgment;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.JudgmentBody;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.LinkedJudgement;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.Meta;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.OtherAnalysis;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.OtherReferences;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.RisGericht;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.RisMeta;
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

    validateCaseLawLdml(caseLawLdml);

    Judgment judgment = caseLawLdml.getJudgment();
    Meta meta = judgment.getMeta();
    RisMeta risMeta = meta.getProprietary().getRisMeta();
    FrbrElement work = meta.getIdentification().getFrbrWork();
    String uniqueId = work.getFrbrThis().getValue();
    RisGericht risGericht = risMeta.getRisGericht();
    JudgmentBody judgmentBody = judgment.getJudgmentBody();

    return CaseLawDocumentationUnit.builder()
        .id(uniqueId)
        .documentNumber(uniqueId)
        .ecli(work.getEcliAliasValue())
        .decisionDate(DateUtils.nullSafeParseyyyyMMdd(work.getFrbrDate().getDate()))
        .fileNumbers(risMeta.getAktenzeichen())
        .courtType(risGericht.getGerichtstyp())
        .location(risGericht.getGerichtsort())
        .documentType(judgment.getName())
        .judicialBody(risGericht.getSpruchkoerper().getValue())
        .courtKeyword(risMeta.getCourtKeyword())
        .keywords(extractKeywords(meta))
        .decisionName(extractDecisionNames(meta))
        .deviatingDocumentNumber(risMeta.getRisAbweichendeDokumentnummern())
        .documentationOffice(risMeta.getRisDokumentationsstelle())
        .legalEffect(risMeta.getRisRechtskraft())
        .headline(sanitize(judgment.getHeader().findShortTitle()))
        .guidingPrinciple(
            extractContent(judgmentBody, AknMainContentIntroduction.GuidingPrinciple.NAME))
        .headnote(sanitize(extractHeadnote(meta).orElse(null)))
        .otherHeadnote(sanitize(extractOtherHeadnote(meta).orElse(null)))
        .outline(extractContent(judgmentBody, AknMainContentIntroduction.Outline.NAME))
        .tenor(sanitize(judgmentBody.getDecision()))
        .caseFacts(sanitize(judgmentBody.getBackground()))
        .decisionGrounds(
            extractContent(judgmentBody, AknMainContentMotivation.DecisionGrounds.NAME))
        .grounds(extractContent(judgmentBody, AknMainContentMotivation.Grounds.NAME))
        .otherLongText(extractContent(judgmentBody, AknMainContentMotivation.OtherLongText.NAME))
        .dissentingOpinion(
            extractContent(judgmentBody, AknMainContentMotivation.DissentingOpinion.NAME))
        .previousDecisions(
            getLinkedJudgements(
                meta, refs -> refs.getReferencesByType(ImplicitReference::getPrecedingJudgement)))
        .ensuingDecisions(
            getLinkedJudgements(
                meta, refs -> refs.getReferencesByType(ImplicitReference::getEnsuingJudgement)))
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

  private static void validateCaseLawLdml(CaseLawLdml ldml) throws ValidationException {
    if (ldml == null) throw new ValidationException("LDML root is null");
    validateNotNull(ldml.getJudgment(), "Judgment missing");
    Judgment judgment = ldml.getJudgment();

    validateNotNull(judgment.getMeta(), "Meta missing");
    validateNotNull(judgment.getJudgmentBody(), "JudgmentBody missing");

    Meta meta = judgment.getMeta();
    validateNotNull(meta.getIdentification(), "Identification missing");
    validateNotNull(meta.getIdentification().getFrbrWork(), "FrbrWork missing");
    FrbrElement work = meta.getIdentification().getFrbrWork();
    validateNotNull(work.getFrbrThis(), "FrbrThis missing");

    if (work.getFrbrDate() == null || work.getFrbrDate().getDate().isBlank()) {
      throw new ValidationException("Decision date is missing");
    }
    validateNotNull(meta.getProprietary(), "Proprietary missing");
    validateNotNull(meta.getProprietary().getRisMeta(), "RisMeta missing");
    validate(!meta.getProprietary().getRisMeta().getAktenzeichen().isEmpty(), "FileNumber missing");
    validateNotNull(meta.getProprietary().getRisMeta().getRisDokumentTyp(), "DocumentType missing");
    validateNotNull(meta.getProprietary().getRisMeta().getRisGericht(), "RisGericht missing");
    validateNotNull(
        meta.getProprietary().getRisMeta().getRisGericht().getGerichtstyp(), "CourtType missing");

    if (judgment.getHeader() == null || judgment.getHeader().findShortTitle() == null) {
      throw new ValidationException("Header or Short Title is missing");
    }
  }

  private static Optional<DocumentaryShortTexts> extractDocumentaryShortTexts(Meta meta) {
    return Optional.ofNullable(meta.getAnalysis())
        .map(Analysis::getOtherAnalysis)
        .map(OtherAnalysis::getDocumentaryShortTexts);
  }

  private static Optional<JaxbHtml> extractHeadnote(Meta meta) {
    Optional<DocumentaryShortTexts> docShortTexts = extractDocumentaryShortTexts(meta);
    return Optional.ofNullable(
        docShortTexts
            .map(DocumentaryShortTexts::getRisOrientierungssatz)
            .map(note -> note.getContent())
            .orElse(null));
  }

  private static Optional<JaxbHtml> extractOtherHeadnote(Meta meta) {
    Optional<DocumentaryShortTexts> docShortTexts = extractDocumentaryShortTexts(meta);
    return Optional.ofNullable(
        docShortTexts
            .map(DocumentaryShortTexts::getRisSonstigerOrientierungssatz)
            .map(note -> note.getContent())
            .orElse(null));
  }

  private static List<String> extractDecisionNames(Meta meta) {
    Optional<DocumentaryShortTexts> docShortTexts = extractDocumentaryShortTexts(meta);
    return docShortTexts
        .map(DocumentaryShortTexts::getRisEntscheidungsNames)
        .orElse(Collections.emptyList())
        .stream()
        .map(DocumentaryShortTexts.RisEntscheidungsName::getName)
        .toList();
  }

  private static List<String> getLinkedJudgements(
      Meta meta, Function<OtherReferences, List<LinkedJudgement>> extractor) {
    return Optional.ofNullable(meta.getAnalysis())
        .map(Analysis::getOtherReferences)
        .map(extractor)
        .stream()
        .flatMap(Collection::stream)
        .filter(Objects::nonNull)
        .map(LinkedJudgement::asString)
        .collect(Collectors.toList());
  }

  private List<String> extractKeywords(Meta meta) {
    return Optional.ofNullable(meta.getClassification())
        .map(c -> c.getKeyword().stream().map(AknKeyword::getValue).toList())
        .orElse(Collections.emptyList());
  }

  private String extractContent(JudgmentBody judgmentBody, String name) {
    return judgmentBody
        .getIntroductionEntryContentByName(name)
        .or(() -> judgmentBody.getMotivationEntryContentByName(name))
        .map(this::sanitize)
        .orElse(null);
  }

  private String sanitize(JaxbHtml html) {
    if (html == null) {
      return null;
    }

    return MappingUtils.sanitizeHtmlFromString(html.toHtmlString());
  }
}
