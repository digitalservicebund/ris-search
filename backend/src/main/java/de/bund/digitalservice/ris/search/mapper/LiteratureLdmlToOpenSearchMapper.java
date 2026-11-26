package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.exception.OpenSearchMapperException;
import de.bund.digitalservice.ris.search.models.ldml.FRBRNumber;
import de.bund.digitalservice.ris.search.models.ldml.FrbrExpression;
import de.bund.digitalservice.ris.search.models.ldml.FrbrLanguage;
import de.bund.digitalservice.ris.search.models.ldml.FrbrNameValueElement;
import de.bund.digitalservice.ris.search.models.ldml.FrbrWork;
import de.bund.digitalservice.ris.search.models.ldml.MainBody;
import de.bund.digitalservice.ris.search.models.ldml.literature.Analysis;
import de.bund.digitalservice.ris.search.models.ldml.literature.Classification;
import de.bund.digitalservice.ris.search.models.ldml.literature.Doc;
import de.bund.digitalservice.ris.search.models.ldml.literature.Gliederung;
import de.bund.digitalservice.ris.search.models.ldml.literature.Identification;
import de.bund.digitalservice.ris.search.models.ldml.literature.ImplicitReference;
import de.bund.digitalservice.ris.search.models.ldml.literature.Keyword;
import de.bund.digitalservice.ris.search.models.ldml.literature.LiteratureLdml;
import de.bund.digitalservice.ris.search.models.ldml.literature.LiteratureType;
import de.bund.digitalservice.ris.search.models.ldml.literature.Meta;
import de.bund.digitalservice.ris.search.models.ldml.literature.OtherReferences;
import de.bund.digitalservice.ris.search.models.ldml.literature.Proprietary;
import de.bund.digitalservice.ris.search.models.ldml.literature.RisDefinition;
import de.bund.digitalservice.ris.search.models.ldml.literature.RisKongress;
import de.bund.digitalservice.ris.search.models.ldml.literature.RisMeta;
import de.bund.digitalservice.ris.search.models.ldml.literature.RisOrganisation;
import de.bund.digitalservice.ris.search.models.ldml.literature.RisPerson;
import de.bund.digitalservice.ris.search.models.ldml.literature.RisPersonInstitution;
import de.bund.digitalservice.ris.search.models.ldml.literature.RisRoleItem;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import de.bund.digitalservice.ris.search.models.opensearch.LiteratureUnindexed;
import jakarta.xml.bind.DataBindingException;
import jakarta.xml.bind.JAXB;
import jakarta.xml.bind.ValidationException;
import java.io.StringReader;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.eclipse.persistence.exceptions.DescriptorException;

/**
 * Utility class responsible for mapping LDML (Legal Document Markup Language) string
 * representations of both selbstständige (SLI) and unselbstständige (ULI) literature into {@link
 * Literature} entities for indexing and retrieval purposes. The mapping process includes parsing,
 * validation, and transformations to ensure compatibility with the structure used in OpenSearch.
 *
 * <p>Both literature types share one LDML schema. Elements that only occur in one type (e.g.
 * ausgabe or verlagsangaben for selbstständige literature) are simply mapped to empty values for
 * the other type.
 *
 * <p>The class contains a set of private static helper methods to extract, parse, and validate
 * specific fields, such as document number, years of publication, references, titles, authors,
 * collaborators, languages, and more, from the LDML input. These methods support the primary
 * mapping method {@code mapLdml(String ldmlString, Instant time)}.
 *
 * <p>Note: The class is designed with a private constructor to prevent instantiation, as all
 * functionality is provided through static methods.
 */
public class LiteratureLdmlToOpenSearchMapper {

  private LiteratureLdmlToOpenSearchMapper() {}

  /**
   * Maps a given LDML (Legal Document Markup Language) string representation to a {@link
   * de.bund.digitalservice.ris.search.models.opensearch.Literature} entity. The method unmarshals
   * the input string into a {@code LiteratureLdml} object and converts it into a {@code Literature}
   * record. In case of errors during parsing or validation, a custom exception is thrown.
   *
   * @param ldmlString the LDML string representation of a literature document, expected to conform
   *     to the predefined schema
   * @param time Instant of when the file was mapped to infer the index time
   * @return an instance of {@link de.bund.digitalservice.ris.search.models.opensearch.Literature}
   *     containing the mapped data from the input LDML string
   * @throws de.bund.digitalservice.ris.search.exception.OpenSearchMapperException if the LDML
   *     string cannot be parsed or mapped to a {@code Literature} entity
   */
  public static Literature mapLdml(String ldmlString, Instant time) {
    try {
      StreamSource ldmlStreamSource = new StreamSource(new StringReader(ldmlString));
      var literatureLdml = JAXB.unmarshal(ldmlStreamSource, LiteratureLdml.class);
      return mapToEntity(literatureLdml, time);
    } catch (DescriptorException | DataBindingException | ValidationException e) {
      throw new OpenSearchMapperException("unable to parse file to Literature", e);
    }
  }

  private static Literature mapToEntity(LiteratureLdml literatureLdml, Instant time)
      throws ValidationException {
    var documentNumber = extractDocumentNumber(literatureLdml);
    var yearsOfPublication = extractYearsOfPublication(literatureLdml);
    var firstYearOfPublication =
        LiteratureLdmlMappingUtils.extractFirstYearOfPublication(yearsOfPublication);
    return Literature.builder()
        .id(documentNumber)
        .documentNumber(documentNumber)
        .yearsOfPublication(yearsOfPublication)
        .firstPublicationDate(firstYearOfPublication)
        .documentTypes(extractDocumentTypes(literatureLdml))
        .mainTitle(extractMainTitle(literatureLdml))
        .mainTitleAdditions(extractMainTitleAdditions(literatureLdml))
        .documentaryTitle(extractDocumentaryTitle(literatureLdml))
        .authors(extractAuthors(literatureLdml))
        .collaborators(extractCollaborators(literatureLdml))
        .originators(extractOriginators(literatureLdml))
        .conferenceNotes(extractKongresse(literatureLdml, RisMeta::getKongressvermerke))
        .universityNotes(extractUniversityNotes(literatureLdml))
        .editors(extractEditor(literatureLdml))
        .founder(extractFounder(literatureLdml))
        .languages(extractLanguages(literatureLdml))
        .fundstellenUnselbststaendig(extractFundstellenUnselbststaendig(literatureLdml))
        .fundstellenSelbststaendig(extractFundstellenSelbststaendig(literatureLdml))
        .normReferences(extractNormReferences(literatureLdml))
        .shortReport(extractShortReport(literatureLdml))
        .outline((extractOutline(literatureLdml)))
        .publisherOrganizations(extractPublisherOrganization(literatureLdml))
        .publisherPersons(extractPublisherPersons(literatureLdml))
        .shortTitles(extractShortTitles(literatureLdml))
        .additionalTitles(extractSonstigeSachtitel(literatureLdml))
        .fullTitleAdditions(extractFullTitleAdditions(literatureLdml))
        .footnotes(extractFootnotes(literatureLdml))
        .publisherInformation(extractPublisherinformation(literatureLdml))
        .edition(extractEdition(literatureLdml))
        .internationalIdentifiers(extractInternationalIdentifiers(literatureLdml, documentNumber))
        .volumes(extractVolumes(literatureLdml))
        .titelAspekte(extractTitelAspekte(literatureLdml))
        .schlagworte(extractSchlagworte(literatureLdml))
        .sachgebiete(extractSachgebiete(literatureLdml))
        .bibliothekarischeVerweisungen(extractBibliothekarischeVerweisungen(literatureLdml))
        .kollationsvermerk(extractKollationsvermerk(literatureLdml))
        .vorauflagen(extractVorauflagen(literatureLdml))
        .regionen(extractStrings(literatureLdml, RisMeta::getRegionen))
        .berufsbilder(extractStrings(literatureLdml, RisMeta::getBerufsbilder))
        .definitionen(extractDefinitionen(literatureLdml))
        .kuendigungsgruende(extractStrings(literatureLdml, RisMeta::getKuendigungsgruende))
        .tarifvertraege(extractStrings(literatureLdml, RisMeta::getTarifvertraege))
        .ueberPersonenInstitutionen(
            extractPersonenInstitutionen(literatureLdml, RisMeta::getUeberPersonenInstitutionen))
        .fuerPersonenInstitutionen(
            extractPersonenInstitutionen(literatureLdml, RisMeta::getFuerPersonenInstitutionen))
        .ueberKongresse(extractKongresse(literatureLdml, RisMeta::getUeberKongresse))
        .unindexed(extractUnindexed(literatureLdml))
        .indexedAt(time.toString())
        .build();
  }

  private static List<String> extractFundstellenUnselbststaendig(LiteratureLdml literatureLdml) {
    return getImplicitReferences(literatureLdml)
        .filter(implicitReference -> implicitReference.getFundstelle() != null)
        .map(ImplicitReference::getShowAs)
        .toList();
  }

  private static List<String> extractFundstellenSelbststaendig(LiteratureLdml literatureLdml) {
    return getImplicitReferences(literatureLdml)
        .filter(implicitReference -> implicitReference.getFundstelleSelbststaendig() != null)
        .map(ImplicitReference::getShowAs)
        .toList();
  }

  private static Optional<RisMeta> extractRisMeta(LiteratureLdml ldml) {
    return Optional.ofNullable(ldml)
        .map(LiteratureLdml::getDoc)
        .map(Doc::getMeta)
        .map(Meta::getProprietary)
        .map(Proprietary::getMeta);
  }

  private static List<String> extractShortTitles(LiteratureLdml literatureLdml) {
    return extractRisMeta(literatureLdml)
        .map(RisMeta::getTitelKurzformen)
        .orElse(Collections.emptyList());
  }

  private static String extractDocumentNumber(LiteratureLdml literatureLdml)
      throws ValidationException {
    var documentNumber =
        Optional.ofNullable(literatureLdml)
            .map(LiteratureLdml::getDoc)
            .map(Doc::getMeta)
            .map(Meta::getIdentification)
            .map(Identification::getFrbrWork)
            .map(FrbrWork::getFrbrAliasList)
            .orElse(Collections.emptyList())
            .stream()
            .filter(alias -> Objects.equals(alias.getName(), "dokumentnummer"))
            .findFirst()
            .map(FrbrNameValueElement::getValue)
            .orElse(null);

    if (documentNumber == null) {
      throw new ValidationException("Literature ldml has no documentNumber");
    }

    return documentNumber;
  }

  private static List<String> extractYearsOfPublication(LiteratureLdml literatureLdml)
      throws ValidationException {
    List<String> yearsOfPublication =
        extractRisMeta(literatureLdml)
            .map(RisMeta::getVeroeffentlichungsJahre)
            .orElse(Collections.emptyList());

    if (yearsOfPublication.isEmpty()) {
      throw new ValidationException("Missing years of publication");
    }

    return yearsOfPublication;
  }

  private static List<String> extractDocumentTypes(LiteratureLdml literatureLdml)
      throws ValidationException {
    final List<String> documentTypes =
        extractRisMeta(literatureLdml)
            .map(RisMeta::getDokumenttypen)
            .orElse(Collections.emptyList());

    if (documentTypes.isEmpty()) {
      throw new ValidationException("No document types found in LDML metadata.");
    }

    return documentTypes;
  }

  private static List<String> extractNormReferences(LiteratureLdml literatureLdml) {
    return getImplicitReferences(literatureLdml)
        .filter(implicitReference -> implicitReference.getNormReference() != null)
        .map(ImplicitReference::getShowAs)
        .toList();
  }

  private static String extractMainTitle(LiteratureLdml literatureLdml) {
    return extractFrbrAlias(literatureLdml, "haupttitel");
  }

  private static String extractMainTitleAdditions(LiteratureLdml literatureLdml) {
    return extractFrbrAlias(literatureLdml, "haupttitelzusatz");
  }

  private static String extractDocumentaryTitle(LiteratureLdml literatureLdml) {
    return extractFrbrAlias(literatureLdml, "dokumentarischerTitel");
  }

  private static List<String> extractAuthors(LiteratureLdml literatureLdml) {
    return extractPersonName(literatureLdml, RisMeta::getVerfasserListe);
  }

  private static List<String> extractPublisherPersons(LiteratureLdml literatureLdml) {
    var personEids = getRoleReferences(literatureLdml, RisMeta::getHerausgeberPersonen);

    return extractPersonen(literatureLdml).stream()
        .filter(person -> personEids.contains(person.getEId()))
        .map(RisPerson::getShowAs)
        .filter(StringUtils::isNotBlank)
        .toList();
  }

  private static List<String> extractCollaborators(LiteratureLdml literatureLdml) {
    return extractPersonName(literatureLdml, RisMeta::getMitarbeiterListe);
  }

  private static List<String> extractOriginators(LiteratureLdml literatureLdml) {
    var originatorEids = getRoleReferences(literatureLdml, RisMeta::getUrheberListe);

    return extractOrganisationen(literatureLdml).stream()
        .filter(orga -> originatorEids.contains(orga.getEId()))
        .map(RisOrganisation::getName)
        .filter(Objects::nonNull)
        .toList();
  }

  private static List<String> extractFounder(LiteratureLdml literatureLdml) {
    return extractPersonName(literatureLdml, RisMeta::getBegruenderListe);
  }

  private static List<String> extractEditor(LiteratureLdml literatureLdml) {
    return extractPersonName(literatureLdml, RisMeta::getBearbeiterListe);
  }

  private static List<String> extractKongresse(
      LiteratureLdml literatureLdml, Function<RisMeta, List<RisRoleItem>> roleList) {
    var eIds = getRoleReferences(literatureLdml, roleList);

    return extractRisMeta(literatureLdml).map(RisMeta::getKongresse).orElse(List.of()).stream()
        .filter(kongress -> eIds.contains(kongress.getEId()))
        .map(RisKongress::getShowAs)
        .filter(Objects::nonNull)
        .toList();
  }

  private static List<String> extractPersonenInstitutionen(
      LiteratureLdml literatureLdml, Function<RisMeta, List<RisRoleItem>> roleList) {
    var eIds = getRoleReferences(literatureLdml, roleList);

    return extractRisMeta(literatureLdml)
        .map(RisMeta::getPersonenInstitutionen)
        .orElse(List.of())
        .stream()
        .filter(personInstitution -> eIds.contains(personInstitution.getEId()))
        .map(RisPersonInstitution::getShowAs)
        .filter(Objects::nonNull)
        .toList();
  }

  private static List<String> extractUniversityNotes(LiteratureLdml literatureLdml) {
    return extractOrganization(literatureLdml, RisMeta::getHochschulschriftenvermerke);
  }

  private static List<String> extractPublisherOrganization(LiteratureLdml literatureLdml) {
    return extractOrganization(literatureLdml, RisMeta::getHerausgeberInstitutionen);
  }

  private static List<String> extractOrganization(
      LiteratureLdml literatureLdml, Function<RisMeta, List<RisRoleItem>> roleList) {
    var eIds = getRoleReferences(literatureLdml, roleList);
    return extractOrganisationen(literatureLdml).stream()
        .filter(orga -> eIds.contains(orga.getEId()))
        .map(RisOrganisation::getShowAs)
        .filter(Objects::nonNull)
        .toList();
  }

  private static List<RisOrganisation> extractOrganisationen(LiteratureLdml literatureLdml) {
    return extractRisMeta(literatureLdml)
        .map(RisMeta::getOrganisationen)
        .orElse(Collections.emptyList());
  }

  private static List<String> extractLanguages(LiteratureLdml literatureLdml) {
    return Optional.ofNullable(literatureLdml)
        .map(LiteratureLdml::getDoc)
        .map(Doc::getMeta)
        .map(Meta::getIdentification)
        .map(Identification::getFrbrExpression)
        .map(FrbrExpression::getFrbrLanguages)
        .orElse(Collections.emptyList())
        .stream()
        .map(FrbrLanguage::getLanguage)
        .toList();
  }

  private static String extractShortReport(LiteratureLdml literatureLdml) {
    return Optional.ofNullable(literatureLdml)
        .map(LiteratureLdml::getDoc)
        .map(Doc::getMainBody)
        .map(MainBody::getNormalizedTextContent)
        .filter(s -> !s.isEmpty())
        .orElse(null);
  }

  private static String extractOutline(LiteratureLdml literatureLdml) {
    var outline =
        String.join(
            StringUtils.SPACE,
            extractRisMeta(literatureLdml)
                .map(RisMeta::getGliederung)
                .map(Gliederung::getGliederungEntry)
                .orElse(Collections.emptyList()));

    return outline.isEmpty() ? null : outline;
  }

  private static LiteratureUnindexed extractUnindexed(LiteratureLdml literatureLdml) {
    List<ImplicitReference> references = getAllImplicitReferences(literatureLdml);
    return LiteratureUnindexed.builder()
        .zitierungenLiteraturSelbststaendig(
            ZitierungenMapper.mapLiteraturSelbststaendig(
                references.stream()
                    .map(ImplicitReference::getReferenzSelbststaendigeLiteratur)
                    .toList()))
        .zitierungenLiteraturUnselbststaendig(
            ZitierungenMapper.mapLiteraturUnselbststaendig(
                references.stream()
                    .map(ImplicitReference::getReferenzUnselbststaendigeLiteratur)
                    .toList()))
        .zitierungenRechtsprechung(
            ZitierungenMapper.mapRechtsprechung(
                references.stream().map(ImplicitReference::getReferenzRechtsprechung).toList()))
        .zitierungenVerwaltungsvorschriften(
            ZitierungenMapper.mapVerwaltungsvorschriften(
                references.stream()
                    .map(ImplicitReference::getReferenzVerwaltungsvorschrift)
                    .toList()))
        .build();
  }

  /**
   * Returns the implicit references of all otherReferences blocks, regardless of their source.
   * Zitierungen are listed under the "active" and "passive" sources.
   */
  private static List<ImplicitReference> getAllImplicitReferences(LiteratureLdml literatureLdml) {
    return Optional.ofNullable(literatureLdml)
        .map(LiteratureLdml::getDoc)
        .map(Doc::getMeta)
        .map(Meta::getAnalysis)
        .map(Analysis::getOtherReferences)
        .orElse(Collections.emptyList())
        .stream()
        .filter(Objects::nonNull)
        .map(OtherReferences::getImplicitReferences)
        .filter(Objects::nonNull)
        .flatMap(List::stream)
        .filter(Objects::nonNull)
        .toList();
  }

  private static Stream<ImplicitReference> getImplicitReferences(LiteratureLdml literatureLdml) {
    return Optional.ofNullable(literatureLdml)
        .map(LiteratureLdml::getDoc)
        .map(Doc::getMeta)
        .map(Meta::getAnalysis)
        .map(Analysis::getOtherReferences)
        .orElse(Collections.emptyList())
        .stream()
        .flatMap(otherReferences -> otherReferences.getImplicitReferences().stream());
  }

  /**
   * Returns the eIds the entries of a role list (e.g. ris:verfasserListe) refer to, without the
   * leading "#".
   */
  private static List<String> getRoleReferences(
      LiteratureLdml literatureLdml, Function<RisMeta, List<RisRoleItem>> roleList) {
    return extractRisMeta(literatureLdml).map(roleList).orElse(Collections.emptyList()).stream()
        .map(RisRoleItem::getRefersTo)
        .filter(Objects::nonNull)
        .map(refersTo -> Strings.CS.removeStart(refersTo, "#"))
        .toList();
  }

  private static List<String> extractPersonName(
      LiteratureLdml literatureLdml, Function<RisMeta, List<RisRoleItem>> roleList) {
    var personEids = getRoleReferences(literatureLdml, roleList);

    return extractPersonen(literatureLdml).stream()
        .filter(person -> personEids.contains(person.getEId()))
        .map(RisPerson::getName)
        .filter(Objects::nonNull)
        .toList();
  }

  private static List<RisPerson> extractPersonen(LiteratureLdml literatureLdml) {
    return extractRisMeta(literatureLdml).map(RisMeta::getPersonen).orElse(Collections.emptyList());
  }

  private static String extractFrbrAlias(LiteratureLdml literatureLdml, String aliasType) {
    return Optional.ofNullable(literatureLdml)
        .map(LiteratureLdml::getDoc)
        .map(Doc::getMeta)
        .map(Meta::getIdentification)
        .map(Identification::getFrbrWork)
        .map(FrbrWork::getFrbrAliasList)
        .orElse(Collections.emptyList())
        .stream()
        .filter(alias -> Objects.equals(alias.getName(), aliasType))
        .findFirst()
        .map(FrbrNameValueElement::getValue)
        .orElse(null);
  }

  private static List<String> extractSonstigeSachtitel(LiteratureLdml ldml) {
    return extractRisMeta(ldml).map(RisMeta::getSonstigeSachtitel).orElse(List.of());
  }

  private static List<String> extractFullTitleAdditions(LiteratureLdml ldml) {
    return extractRisMeta(ldml).map(RisMeta::getGesamttitelangaben).orElse(List.of()).stream()
        .map(titel ->
            Stream.of(titel.getTitel(), titel.getBandbezeichnung())
                .filter(StringUtils::isNotBlank)
                .map(String::trim)
                .collect(Collectors.joining(", ")))
        .filter(StringUtils::isNotBlank)
        .toList();
  }

  private static List<String> extractFootnotes(LiteratureLdml ldml) {
    return extractRisMeta(ldml).map(RisMeta::getGesamtfussnoten).orElse(List.of()).stream()
        .filter(StringUtils::isNotBlank)
        .toList();
  }

  private static List<String> extractPublisherinformation(LiteratureLdml ldml) {
    return extractOrganization(ldml, RisMeta::getVerlagsangaben);
  }

  private static String extractEdition(LiteratureLdml ldml) {
    return extractRisMeta(ldml).map(RisMeta::getAusgabe).orElse(null);
  }

  private static List<String> extractVolumes(LiteratureLdml ldml) {
    return extractRisMeta(ldml).map(RisMeta::getTeilbaende).orElse(List.of());
  }

  /**
   * ISBN/ISSN numbers of selbstständige literature. Unselbstständige literature carries a
   * placeholder FRBRnumber (the element is mandatory in akn:FRBRWork), which must not be indexed.
   */
  private static List<String> extractInternationalIdentifiers(
      LiteratureLdml ldml, String documentNumber) {
    if (LiteratureType.getByDocumentNumber(documentNumber) != LiteratureType.SLI) {
      return List.of();
    }
    return Optional.ofNullable(ldml)
        .map(LiteratureLdml::getDoc)
        .map(Doc::getMeta)
        .map(Meta::getIdentification)
        .map(Identification::getFrbrWork)
        .stream()
        .filter(w -> Objects.nonNull(w.getFrbrNumbers()))
        .flatMap(work -> work.getFrbrNumbers().stream().map(FRBRNumber::getValue))
        .toList();
  }

  private static List<String> extractTitelAspekte(LiteratureLdml ldml) {
    return extractRisMeta(ldml).map(RisMeta::getTitelAspekte).orElse(List.of());
  }

  private static List<String> extractBibliothekarischeVerweisungen(LiteratureLdml ldml) {
    return extractRisMeta(ldml)
        .map(RisMeta::getBibliothekarischeVerweisungen)
        .orElse(List.of())
        .stream()
        .filter(StringUtils::isNotBlank)
        .toList();
  }

  private static String extractKollationsvermerk(LiteratureLdml ldml) {
    return extractRisMeta(ldml)
        .map(RisMeta::getKollation)
        .filter(StringUtils::isNotBlank)
        .orElse(null);
  }

  private static List<String> extractVorauflagen(LiteratureLdml ldml) {
    return extractRisMeta(ldml).map(RisMeta::getVorauflagen).orElse(List.of()).stream()
        .filter(StringUtils::isNotBlank)
        .toList();
  }

  /** Returns the non-blank values of a simple string list in ris:meta. */
  private static List<String> extractStrings(
      LiteratureLdml ldml, Function<RisMeta, List<String>> getter) {
    return extractRisMeta(ldml).map(getter).orElse(List.of()).stream()
        .filter(StringUtils::isNotBlank)
        .toList();
  }

  private static List<String> extractDefinitionen(LiteratureLdml ldml) {
    return extractRisMeta(ldml).map(RisMeta::getDefinitionen).orElse(List.of()).stream()
        .filter(Objects::nonNull)
        .map(RisDefinition::getDefinierterBegriff)
        .filter(StringUtils::isNotBlank)
        .toList();
  }

  private static List<String> extractSachgebiete(LiteratureLdml ldml) {
    return extractRisMeta(ldml).map(RisMeta::getSachgebiete).orElse(List.of()).stream()
        .map(sachgebiet ->
            Stream.of(sachgebiet.getSachgebieteId(), sachgebiet.getValue())
                .filter(StringUtils::isNotBlank)
                .map(String::trim)
                .collect(Collectors.joining(StringUtils.SPACE)))
        .filter(StringUtils::isNotBlank)
        .toList();
  }

  private static List<String> extractSchlagworte(LiteratureLdml ldml) {
    return Optional.ofNullable(ldml)
        .map(LiteratureLdml::getDoc)
        .map(Doc::getMeta)
        .map(Meta::getClassifications)
        .orElse(List.of())
        .stream()
        .map(Classification::getKeywords)
        .filter(Objects::nonNull)
        .flatMap(List::stream)
        .filter(keyword -> "Schlagwort".equals(keyword.getDomainTerm()))
        .map(Keyword::getValue)
        .filter(StringUtils::isNotBlank)
        .toList();
  }
}
