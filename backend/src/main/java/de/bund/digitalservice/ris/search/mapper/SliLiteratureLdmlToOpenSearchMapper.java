package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.exception.OpenSearchMapperException;
import de.bund.digitalservice.ris.search.models.ldml.FRBRNumber;
import de.bund.digitalservice.ris.search.models.ldml.FrbrExpression;
import de.bund.digitalservice.ris.search.models.ldml.FrbrLanguage;
import de.bund.digitalservice.ris.search.models.ldml.FrbrNameValueElement;
import de.bund.digitalservice.ris.search.models.ldml.FrbrWork;
import de.bund.digitalservice.ris.search.models.ldml.MainBody;
import de.bund.digitalservice.ris.search.models.ldml.literature.Analysis;
import de.bund.digitalservice.ris.search.models.ldml.literature.Gliederung;
import de.bund.digitalservice.ris.search.models.ldml.literature.Identification;
import de.bund.digitalservice.ris.search.models.ldml.literature.ImplicitReference;
import de.bund.digitalservice.ris.search.models.ldml.literature.RisKongress;
import de.bund.digitalservice.ris.search.models.ldml.literature.RisOrganisation;
import de.bund.digitalservice.ris.search.models.ldml.literature.RisPerson;
import de.bund.digitalservice.ris.search.models.ldml.literature.RisRoleItem;
import de.bund.digitalservice.ris.search.models.ldml.literature.sli.Doc;
import de.bund.digitalservice.ris.search.models.ldml.literature.sli.LiteratureLdml;
import de.bund.digitalservice.ris.search.models.ldml.literature.sli.Meta;
import de.bund.digitalservice.ris.search.models.ldml.literature.sli.Proprietary;
import de.bund.digitalservice.ris.search.models.ldml.literature.sli.RisMeta;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
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
 * representations into {@link de.bund.digitalservice.ris.search.models.opensearch.Literature}
 * entities for indexing and retrieval purposes. The mapping process includes parsing, validation,
 * and transformations to ensure compatibility with the structure used in OpenSearch. This class
 * primarily focuses on extracting various attributes from an LDML input and translating these into
 * the appropriate {@link de.bund.digitalservice.ris.search.models.opensearch.Literature} object
 * format.
 *
 * <p>The class contains a set of private static helper methods to extract, parse, and validate
 * specific fields, such as document number, years of publication, references, titles, authors,
 * collaborators, languages, and more, from the LDML input. These methods support the primary
 * mapping method {@code mapLdml(String ldmlString)}.
 *
 * <p>Note: The class is designed with a private constructor to prevent instantiation, as all
 * functionality is provided through static methods.
 */
public class SliLiteratureLdmlToOpenSearchMapper {

  private SliLiteratureLdmlToOpenSearchMapper() {}

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
        .yearsOfPublication(extractYearsOfPublication(literatureLdml))
        .firstPublicationDate(firstYearOfPublication)
        .documentTypes(extractDocumentTypes(literatureLdml))
        .normReferences(extractNormReferences(literatureLdml))
        .mainTitle(extractMainTitle(literatureLdml))
        .mainTitleAdditions(extractMainTitleAdditions(literatureLdml))
        .documentaryTitle(extractDocumentaryTitle(literatureLdml))
        .authors(extractAuthors(literatureLdml))
        .collaborators(extractCollaborators(literatureLdml))
        .originators(extractOriginators(literatureLdml))
        .conferenceNotes(extractConferenceNotes(literatureLdml))
        .universityNotes(extractUniversityNotes(literatureLdml))
        .editors(extractEditor(literatureLdml))
        .founder(extractFounder(literatureLdml))
        .languages(extractLanguages(literatureLdml))
        .dependentReferences(extractDependentReferences(literatureLdml))
        .independentReferences(extractIndependentReferences(literatureLdml))
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
        .internationalIdentifiers(extractInternationalIdentifiers(literatureLdml))
        .volumes(extractVolumes(literatureLdml))
        .indexedAt(time.toString())
        .build();
  }

  private static List<String> extractDependentReferences(LiteratureLdml literatureLdml) {
    return getImplicitReferences(literatureLdml)
        .filter(implicitReference -> implicitReference.getFundstelleUnselbstaendig() != null)
        .map(ImplicitReference::getShowAs)
        .toList();
  }

  private static List<String> extractIndependentReferences(LiteratureLdml literatureLdml) {
    return getImplicitReferences(literatureLdml)
        .filter(implicitReference -> implicitReference.getFundstelleSelbstaendig() != null)
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
            .map(RisMeta::getDokoumenttypen)
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

  private static List<String> extractConferenceNotes(LiteratureLdml literatureLdml) {
    var eIds = getRoleReferences(literatureLdml, RisMeta::getKongressvermerke);

    return extractRisMeta(literatureLdml).map(RisMeta::getKongresse).orElse(List.of()).stream()
        .filter(kongress -> eIds.contains(kongress.getEId()))
        .map(RisKongress::getShowAs)
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

  private static List<String> extractInternationalIdentifiers(LiteratureLdml ldml) {
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
}
