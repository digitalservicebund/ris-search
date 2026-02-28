package de.bund.digitalservice.ris.search.mapper;

import static de.bund.digitalservice.ris.search.utils.MappingUtils.nullSafeGet;
import static de.bund.digitalservice.ris.search.utils.MappingUtils.validate;
import static de.bund.digitalservice.ris.search.utils.MappingUtils.validateNotNull;

import de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml.AknKeyword;
import de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml.Analysis;
import de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml.CaseLawLdml;
import de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml.Court;
import de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml.DocumentaryShortTexts;
import de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml.FrbrDate;
import de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml.FrbrElement;
import de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml.JaxbHtml;
import de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml.Judgment;
import de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml.JudgmentBody;
import de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml.Meta;
import de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml.OtherAnalysis;
import de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml.RelatedDecision;
import de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml.RisMeta;
import de.bund.digitalservice.ris.search.exception.OpenSearchMapperException;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.utils.DateUtils;
import de.bund.digitalservice.ris.search.utils.MappingUtils;
import jakarta.xml.bind.DataBindingException;
import jakarta.xml.bind.JAXB;
import jakarta.xml.bind.ValidationException;
import java.io.StringReader;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
    validateNotNull(caseLawLdml.getJudgment(), "Judgment missing");
    Judgment judgment = caseLawLdml.getJudgment();
    validateNotNull(judgment.getMeta(), "Meta missing");
    Meta meta = judgment.getMeta();
    validateNotNull(meta.getProprietary(), "Proprietary missing");
    validateNotNull(meta.getProprietary().getMeta(), "RisMeta missing");
    RisMeta risMeta = meta.getProprietary().getMeta();
    Court court = risMeta.getCourt();
    validateNotNull(court.getGerichtstyp(), "CourtType missing");
    validateNotNull(risMeta.getDocumentType(), "DocumentType missing");
    validate(!risMeta.getFileNumbers().isEmpty(), "FileNumber missing");
    validateNotNull(meta.getIdentification(), "Identification missing");
    validateNotNull(meta.getIdentification().getFrbrWork(), "FrbrWork missing");
    FrbrElement frbrWork = meta.getIdentification().getFrbrWork();
    validateNotNull(frbrWork.getFrbrThis(), "FrbrThis missing");
    validateNotNull(frbrWork.getFrbrThis().getValue(), "Unique identifier missing");
    String uniqueIdentifier = frbrWork.getFrbrThis().getValue();
    validateNotNull(frbrWork.getFrbrAlias(), "FrbrAlias missing");
    validateNotNull(frbrWork.getUuidAliasValue(), "FrbrAlias UUID missing");
    String ecli = frbrWork.getEcliAliasValue();
    validateNotNull(frbrWork.getFrbrDate(), "FrbrDate missing");
    FrbrDate frbrDate = frbrWork.getFrbrDate();
    validateNotNull(frbrDate.getDate(), "DecisionDate missing");

    // visible elements
    validateNotNull(judgment.getHeader(), "Header missing");

    JaxbHtml header = judgment.getHeader();

    validateNotNull(judgment.getJudgmentBody(), "JudgmentBody missing");

    JudgmentBody judgmentBody = judgment.getJudgmentBody();

    // Get data from introductions
    JaxbHtml leitsatz = judgmentBody.getIntroductionEntryContentByName("Leitsatz").orElse(null);
    JaxbHtml gliederung = judgmentBody.getIntroductionEntryContentByName("Gliederung").orElse(null);

    Optional<DocumentaryShortTexts> docShortTexts =
        Optional.ofNullable(meta.getAnalysis())
            .map(Analysis::getOtherAnalysis)
            .map(OtherAnalysis::getDocumentaryShortTexts);

    JaxbHtml headNote =
        docShortTexts
            .map(DocumentaryShortTexts::getHeadNotes)
            .map(note -> note.getContent())
            .orElse(null);

    JaxbHtml otherHeadNote =
        docShortTexts
            .map(DocumentaryShortTexts::getOtherHeadNotes)
            .map(note -> note.getContent())
            .orElse(null);

    JaxbHtml background = judgmentBody.getBackground();

    JaxbHtml decision = judgmentBody.getDecision();
    JaxbHtml dissentingOpinion = null;

    JaxbHtml decisionGrounds =
        judgmentBody.getMotivationEntryContentByName("Entscheidungsgründe").orElse(null);
    JaxbHtml grounds = judgmentBody.getMotivationEntryContentByName("Gründe").orElse(null);
    JaxbHtml otherLongText =
        judgmentBody.getMotivationEntryContentByName("Sonstiger Langtext").orElse(null);
    List<String> previousDecisions =
        nullSafeGet(
            risMeta.getPreviousDecision(),
            e -> e.stream().map(this::relatedDecisionToString).toList());
    List<String> ensuingDecisions =
        nullSafeGet(
            risMeta.getEnsuingDecision(),
            e -> e.stream().map(this::relatedDecisionToString).toList());

    // some fields not in ldml are commented for now
    return CaseLawDocumentationUnit.builder()
        // Meta elements
        .id(uniqueIdentifier)
        .documentNumber(uniqueIdentifier)
        .ecli(ecli)
        .decisionDate(DateUtils.nullSafeParseyyyyMMdd(frbrDate.getDate()))
        .fileNumbers(risMeta.getFileNumbers())
        .courtType(court.getGerichtstyp())
        .location(court.getGerichtsort())
        .documentType(risMeta.getDocumentType())
        .judicialBody(court.getSpruchkoerper().getValue())
        .courtKeyword(risMeta.getCourtKeyword())
        .keywords(
            nullSafeGet(
                meta.getClassification(),
                e ->
                    nullSafeGet(
                        e.getKeyword(), f -> f.stream().map(AknKeyword::getValue).toList())))
        .decisionName(risMeta.getDecisionName())
        .deviatingDocumentNumber(risMeta.getDeviatingDocumentNumber())
        .publicationStatus(risMeta.getPublicationStatus())
        .error(risMeta.getError() == null || risMeta.getError())
        .documentationOffice(risMeta.getDocumentationOffice())
        .procedures(risMeta.getProcedure())
        .legalEffect(risMeta.getLegalEffect())

        // Visible elements (in display order)
        .headline(jaxbToSanitizedHtml(header))
        .guidingPrinciple(jaxbToSanitizedHtml(leitsatz))
        .headnote(jaxbToSanitizedHtml(headNote))
        .otherHeadnote(jaxbToSanitizedHtml(otherHeadNote))
        .outline(jaxbToSanitizedHtml(gliederung))
        .tenor(jaxbToSanitizedHtml(decision))
        .caseFacts(jaxbToSanitizedHtml(background))
        .decisionGrounds(jaxbToSanitizedHtml(decisionGrounds))
        .grounds(jaxbToSanitizedHtml(grounds))
        .otherLongText(jaxbToSanitizedHtml(otherLongText))
        .dissentingOpinion(jaxbToSanitizedHtml(dissentingOpinion))
        .previousDecisions(previousDecisions)
        .ensuingDecisions(ensuingDecisions)

        // Internal (portal team) fields
        .indexedAt(Instant.now().toString())
        .articles(null)
        .build();
  }

  private static String jaxbToSanitizedHtml(JaxbHtml html) {
    if (html == null) {
      return null;
    }
    return MappingUtils.sanitizeHtmlFromString(html.toHtmlString());
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

  private String relatedDecisionToString(RelatedDecision relatedDecision) {
    return Stream.of(
            relatedDecision.getDocumentNumber(),
            relatedDecision.getFileNumber(),
            relatedDecision.getCourtType())
        .filter(Objects::nonNull)
        .collect(Collectors.joining(" "));
  }
}
