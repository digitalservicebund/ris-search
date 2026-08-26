package de.bund.digitalservice.ris.search.mapper;

import static de.bund.digitalservice.ris.search.utils.MappingUtils.validate;
import static de.bund.digitalservice.ris.search.utils.MappingUtils.validateNotNull;

import de.bund.digitalservice.ris.search.exception.OpenSearchMapperException;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.AknKeyword;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.Analysis;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.CaseLawLdml;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.DocumentaryShortTexts;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.DomainTerm;
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
import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
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
    FrbrElement manifestation = meta.getIdentification().getFrbrManifestation();
    String uniqueId = work.getFrbrThis().getValue();
    RisGericht risGericht = risMeta.getRisGericht();
    JudgmentBody judgmentBody = judgment.getJudgmentBody();

    return CaseLawDocumentationUnit.builder()
        .id(uniqueId)
        .documentNumber(uniqueId)
        .ecli(work.getEcliAliasValue())
        .celex(work.getCelexAliasValue())
        .decisionDate(DateUtils.nullSafeParseyyyyMMdd(work.getEntscheidungsdatumValue()))
        .fileNumber(work.getAktenzeichenAliasValue())
        .fileNumbers(risMeta.getAktenzeichen())
        .abweichendeAktenzeichen(risMeta.getAbweichendeAktenzeichen())
        .courtType(risGericht.getGerichtstyp())
        .location(risGericht.getGerichtsort())
        .gerichtsbarkeit(risGericht.getGerichtsbarkeit())
        .documentType(risMeta.getRisDokumentTyp())
        .judicialBody(risGericht.getSpruchkoerperValue())
        .courtKeyword(risMeta.getCourtKeyword())
        .keywords(extractKeywords(meta))
        .decisionName(extractDecisionNames(meta))
        .deviatingDocumentNumber(risMeta.getRisAbweichendeDokumentnummern())
        .documentationOffice(risMeta.getRisDokumentationsstelle())
        .legalEffect(risMeta.getRisRechtskraft())
        .headline(sanitize(judgment.getHeader().findShortTitle()))
        .titleLine(extractTitleLine(meta))
        .guidingPrinciple(extractContent(judgmentBody, DomainTerm.GUIDING_PRINCIPLE))
        .headnote(sanitize(extractHeadnote(meta).orElse(null)))
        .otherHeadnote(sanitize(extractOtherHeadnote(meta).orElse(null)))
        .outline(extractContent(judgmentBody, DomainTerm.OUTLINE))
        .tenor(sanitize(judgmentBody.getDecision()))
        .caseFacts(sanitize(judgmentBody.getBackground()))
        .decisionGrounds(sanitize(judgmentBody.getEntscheidungsgruende()))
        .grounds(sanitize(judgmentBody.getGruende()))
        .otherLongText(sanitize(judgmentBody.getSonstigerLangtext()))
        .rechtsfrageGesamt(sanitize(judgmentBody.getRechtsfrageGesamt()))
        .dissentingOpinion(judgmentBody.getFormattedAbweichendeMeinung(risMeta).orElse(null))
        .abweichendeDaten(risMeta.getRisAbweichendeDaten())
        .abweichendeEclis(risMeta.getRisAbweichendeEclis())
        .berufsbilder(risMeta.getRisBerufsbilder())
        .kuendigungsarten(risMeta.getRisKuendigungsarten())
        .herkunftslaender(risMeta.getRisHerkunftslaender())
        .regionen(risMeta.getRisRegionen())
        .tarifvertraege(risMeta.getRisTarifvertraege())
        .kuendigungsgruende(risMeta.getRisKuendigungsgruende())
        .mitwirkendeRichter(risMeta.getRisMitwirkendeRichter())
        .sachgebiete(risMeta.getRisSachgebiete())
        .streitjahre(risMeta.getRisStreitjahre())
        .fehlerhafteGerichte(risMeta.getRisFehlerhafteGerichte())
        .datenDerMuendlichenVerhandlung(risMeta.getRisDatenDerMuendlichenVerhandlung())
        .definitionen(risMeta.getDefinitionen())
        .erledigung(risMeta.getRisErledigung())
        .hasLegislativeMandate(risMeta.getRisGesetzgebungsauftrag())
        .langtextdatum(risMeta.getRisLangtextdatum())
        .rechtsmittelfuehrer(risMeta.getRisRechtsmittelfuehrer())
        .rechtsmittelzulassung(risMeta.getRisRechtsmittelzulassung())
        .revision(risMeta.getRisRevision())
        .letzteVeroeffentlichung(
            manifestation == null
                ? null
                : DateUtils.nullSafeParseyyyyMMdd(manifestation.getLetzteVeroeffentlichungValue()))
        .erledigungsvermerk(extractErledigungsvermerk(meta))
        .rechtsfrage(extractRechtsfrage(meta))
        .erstveroeffentlichung(
            manifestation == null
                ? null
                : DateUtils.nullSafeParseyyyyMMdd(manifestation.getErstveroeffentlichungValue()))
        .mitteilungsdatum(DateUtils.nullSafeParseyyyyMMdd(work.getMitteilungsdatumValue()))
        .previousDecisions(
            getLinkedJudgements(
                meta,
                refs -> refs.getReferencesByType(ImplicitReference::getPrecedingJudgement),
                LinkedJudgement::asString))
        .ensuingDecisions(
            getLinkedJudgements(
                meta,
                refs -> refs.getReferencesByType(ImplicitReference::getEnsuingJudgement),
                LinkedJudgement::getEnsuingDecisionFormatted))
        .aktivzitierungLiteraturUnselbstaendig(extractAktivzitierungLiteraturUnselbstaendig(meta))
        .passivzitierungLiteraturUnselbstaendig(extractPassivzitierungLiteraturUnselbstaendig(meta))
        .aktivzitierungLiteraturSelbstaendig(extractAktivzitierungLiteraturSelbstaendig(meta))
        .passivzitierungLiteraturSelbstaendig(extractPassivzitierungLiteraturSelbstaendig(meta))
        .aktivzitierungRechtsprechung(extractAktivzitierungRechtsprechung(meta))
        .passivzitierungRechtsprechung(extractPassivzitierungRechtsprechung(meta))
        .aktivzitierungVerwaltungsvorschriften(extractAktivzitierungVerwaltungsvorschriften(meta))
        .passivzitierungVerwaltungsvorschriften(extractPassivzitierungVerwaltungsvorschriften(meta))
        .amtlicheFundstellen(extractAmtlicheFundstellen(meta))
        .nichtamtlicheFundstellen(extractNichtamtlicheFundstellen(meta))
        .gesetzeskraft(extractGesetzeskraft(meta))
        .normenkette(extractNormenkette(meta))
        // Internal (portal team) fields
        .indexedAt(Instant.now().toString())
        .vorabdokument(isVorabdokument(judgmentBody))
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

  /**
   * Converts a given LDML file content bytearray into a {@link CaseLawDocumentationUnit}.
   *
   * @param ldmlFile the byteArray representation of the LDML file to be converted
   * @return a {@link CaseLawDocumentationUnit} instance created from the provided LDML file string
   * @throws OpenSearchMapperException if the LDML file cannot be parsed into a {@link
   *     CaseLawDocumentationUnit}
   */
  public CaseLawDocumentationUnit fromByteArray(byte[] ldmlFile) {
    try {
      StreamSource ldmlStreamSource = new StreamSource(new ByteArrayInputStream(ldmlFile));
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

    validateNotNull(meta.getProprietary(), "Proprietary missing");
    validateNotNull(meta.getProprietary().getRisMeta(), "RisMeta missing");
    validate(!meta.getProprietary().getRisMeta().getAktenzeichen().isEmpty(), "FileNumber missing");
    validateNotNull(meta.getProprietary().getRisMeta().getRisDokumentTyp(), "DocumentType missing");
    validateNotNull(meta.getProprietary().getRisMeta().getRisGericht(), "RisGericht missing");
    validateNotNull(
        meta.getProprietary().getRisMeta().getRisGericht().getGerichtstyp(), "CourtType missing");

    if (judgment.getHeader() == null || judgment.getHeader().findShortTitle() == null) {
      throw new ValidationException("Short Title missing");
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

  private static String extractTitleLine(Meta meta) {
    return extractDocumentaryShortTexts(meta)
        .map(DocumentaryShortTexts::getRisTitelzeile)
        .map(DocumentaryShortTexts.RisTitelzeile::getContent)
        .map(CaseLawLdmlToOpenSearchMapper::sanitize)
        .orElse(null);
  }

  private static String extractErledigungsvermerk(Meta meta) {
    return extractDocumentaryShortTexts(meta)
        .map(DocumentaryShortTexts::getRisErledigungsvermerk)
        .map(DocumentaryShortTexts.RisErledigungsvermerk::getContent)
        .map(CaseLawLdmlToOpenSearchMapper::sanitize)
        .orElse(null);
  }

  private static String extractRechtsfrage(Meta meta) {
    return extractDocumentaryShortTexts(meta)
        .map(DocumentaryShortTexts::getRisRechtsfrage)
        .map(DocumentaryShortTexts.RisRechtsfrage::getContent)
        .map(CaseLawLdmlToOpenSearchMapper::sanitize)
        .orElse(null);
  }

  private static List<String> getLinkedJudgements(
      Meta meta,
      Function<OtherReferences, List<LinkedJudgement>> extractor,
      Function<LinkedJudgement, String> formatter) {
    return Optional.ofNullable(meta.getAnalysis())
        .map(Analysis::getOtherReferences)
        .map(extractor)
        .stream()
        .flatMap(Collection::stream)
        .filter(Objects::nonNull)
        .map(formatter)
        .toList();
  }

  private static List<String> extractAktivzitierungLiteraturUnselbstaendig(Meta meta) {
    return Optional.ofNullable(meta.getAnalysis())
        .map(Analysis::getOtherReferences)
        .map(
            refs ->
                refs.getReferencesByType(
                    ImplicitReference::getAktivzitierteUnselbststaendigeLiteraturDokumentnummer))
        .orElse(List.of());
  }

  private static List<String> extractAktivzitierungLiteraturSelbstaendig(Meta meta) {
    return Optional.ofNullable(meta.getAnalysis())
        .map(Analysis::getOtherReferences)
        .map(
            refs ->
                refs.getReferencesByType(
                    ImplicitReference::getAktivzitierteSelbststaendigeLiteraturDokumentnummer))
        .orElse(List.of());
  }

  private static List<String> extractPassivzitierungLiteraturSelbstaendig(Meta meta) {
    return Optional.ofNullable(meta.getAnalysis())
        .map(Analysis::getOtherReferences)
        .map(
            refs ->
                refs.getReferencesByType(
                    ImplicitReference::getPassivzitierteSelbststaendigeLiteraturDokumentnummer))
        .orElse(List.of());
  }

  private static List<String> extractPassivzitierungLiteraturUnselbstaendig(Meta meta) {
    return Optional.ofNullable(meta.getAnalysis())
        .map(Analysis::getOtherReferences)
        .map(
            refs ->
                refs.getReferencesByType(
                    ImplicitReference::getPassivzitierteUnselbststaendigeLiteraturDokumentnummer))
        .orElse(List.of());
  }

  private static List<String> extractAktivzitierungRechtsprechung(Meta meta) {
    return Optional.ofNullable(meta.getAnalysis())
        .map(Analysis::getOtherReferences)
        .map(
            refs ->
                refs.getReferencesByType(
                    ImplicitReference::getAktivzitierteRechtsprechungDokumentnummer))
        .orElse(List.of());
  }

  private static List<String> extractPassivzitierungRechtsprechung(Meta meta) {
    return Optional.ofNullable(meta.getAnalysis())
        .map(Analysis::getOtherReferences)
        .map(
            refs ->
                refs.getReferencesByType(
                    ImplicitReference::getPassivzitierteRechtsprechungDokumentnummer))
        .orElse(List.of());
  }

  private static List<String> extractAktivzitierungVerwaltungsvorschriften(Meta meta) {
    return Optional.ofNullable(meta.getAnalysis())
        .map(Analysis::getOtherReferences)
        .map(
            refs ->
                refs.getReferencesByType(
                    ImplicitReference::getAktivzitierteVerwaltungsvorschriftDokumentnummer))
        .orElse(List.of());
  }

  private static List<String> extractPassivzitierungVerwaltungsvorschriften(Meta meta) {
    return Optional.ofNullable(meta.getAnalysis())
        .map(Analysis::getOtherReferences)
        .map(
            refs ->
                refs.getReferencesByType(
                    ImplicitReference::getPassivzitierteVerwaltungsvorschriftDokumentnummer))
        .orElse(List.of());
  }

  private static List<String> extractAmtlicheFundstellen(Meta meta) {
    return Optional.ofNullable(meta.getAnalysis())
        .map(Analysis::getOtherReferences)
        .map(refs -> refs.getReferencesByType(ImplicitReference::getAmtlicheFundstelleFormatted))
        .orElse(List.of());
  }

  private static List<String> extractNichtamtlicheFundstellen(Meta meta) {
    return Optional.ofNullable(meta.getAnalysis())
        .map(Analysis::getOtherReferences)
        .map(
            refs ->
                refs.getReferencesByType(ImplicitReference::getNichtamtlicheFundstelleFormatted))
        .orElse(List.of());
  }

  private static List<String> extractGesetzeskraft(Meta meta) {
    return Optional.ofNullable(meta.getAnalysis())
        .map(Analysis::getOtherReferences)
        .map(OtherReferences::getImplicitReferences)
        .stream()
        .flatMap(Collection::stream)
        .filter(Objects::nonNull)
        .flatMap(ref -> ref.getGesetzeskraftFormattedList().stream())
        .toList();
  }

  private static List<String> extractNormenkette(Meta meta) {
    return Optional.ofNullable(meta.getAnalysis())
        .map(Analysis::getOtherReferences)
        .map(OtherReferences::getImplicitReferences)
        .stream()
        .flatMap(Collection::stream)
        .filter(Objects::nonNull)
        .flatMap(ref -> ref.getNormenketteFormattedList().stream())
        .toList();
  }

  private List<String> extractKeywords(Meta meta) {
    return Optional.ofNullable(meta.getClassification())
        .map(c -> c.getKeyword().stream().map(AknKeyword::getValue).toList())
        .orElse(Collections.emptyList());
  }

  private String extractContent(JudgmentBody judgmentBody, DomainTerm term) {
    return judgmentBody
        .getContentByDomainTerm(term)
        .map(CaseLawLdmlToOpenSearchMapper::sanitize)
        .orElse(null);
  }

  private static boolean isVorabdokument(JudgmentBody judgmentBody) {
    return Objects.equals(judgmentBody.getStatus(), "incomplete");
  }

  private static String sanitize(JaxbHtml html) {
    if (html == null) {
      return null;
    }

    return MappingUtils.sanitizeHtmlFromString(html.toHtmlString());
  }
}
