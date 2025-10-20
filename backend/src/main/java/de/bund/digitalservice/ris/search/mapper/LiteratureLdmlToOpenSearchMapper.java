package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml.FrbrLanguage;
import de.bund.digitalservice.ris.search.models.ldml.literature.Analysis;
import de.bund.digitalservice.ris.search.models.ldml.literature.Classification;
import de.bund.digitalservice.ris.search.models.ldml.literature.Doc;
import de.bund.digitalservice.ris.search.models.ldml.literature.FrbrDate;
import de.bund.digitalservice.ris.search.models.ldml.literature.FrbrExpression;
import de.bund.digitalservice.ris.search.models.ldml.literature.FrbrNameValueElement;
import de.bund.digitalservice.ris.search.models.ldml.literature.FrbrWork;
import de.bund.digitalservice.ris.search.models.ldml.literature.Gliederung;
import de.bund.digitalservice.ris.search.models.ldml.literature.Identification;
import de.bund.digitalservice.ris.search.models.ldml.literature.ImplicitReference;
import de.bund.digitalservice.ris.search.models.ldml.literature.Keyword;
import de.bund.digitalservice.ris.search.models.ldml.literature.LiteratureLdml;
import de.bund.digitalservice.ris.search.models.ldml.literature.MainBody;
import de.bund.digitalservice.ris.search.models.ldml.literature.Meta;
import de.bund.digitalservice.ris.search.models.ldml.literature.Metadata;
import de.bund.digitalservice.ris.search.models.ldml.literature.OtherReferences;
import de.bund.digitalservice.ris.search.models.ldml.literature.Proprietary;
import de.bund.digitalservice.ris.search.models.ldml.literature.References;
import de.bund.digitalservice.ris.search.models.ldml.literature.TlcEvent;
import de.bund.digitalservice.ris.search.models.ldml.literature.TlcOrganization;
import de.bund.digitalservice.ris.search.models.ldml.literature.TlcPerson;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import de.bund.digitalservice.ris.search.utils.DateUtils;
import jakarta.xml.bind.DataBindingException;
import jakarta.xml.bind.JAXB;
import jakarta.xml.bind.ValidationException;
import java.io.StringReader;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.persistence.exceptions.DescriptorException;

public class LiteratureLdmlToOpenSearchMapper {

  private static final Logger logger = LogManager.getLogger(LiteratureLdmlToOpenSearchMapper.class);

  private LiteratureLdmlToOpenSearchMapper() {}

  public static Optional<Literature> mapLdml(String ldmlString) {
    try {
      StreamSource ldmlStreamSource = new StreamSource(new StringReader(ldmlString));
      var literatureLdml = JAXB.unmarshal(ldmlStreamSource, LiteratureLdml.class);
      return mapToEntity(literatureLdml);
    } catch (DescriptorException | DataBindingException | ValidationException e) {
      logger.warn("Error creating literature opensearch entity.", e);
      return Optional.empty();
    }
  }

  private static Optional<Literature> mapToEntity(LiteratureLdml literatureLdml)
      throws ValidationException {
    var documentNumber = extractDocumentNumber(literatureLdml);
    var yearsOfPublication = extractYearsOfPublication(literatureLdml);
    return Optional.of(
        Literature.builder()
            .id(documentNumber)
            .documentNumber(documentNumber)
            .recordingDate(extractRecordingDate(literatureLdml))
            .yearsOfPublication(extractYearsOfPublication(literatureLdml))
            .firstPublicationDate(
                yearsOfPublication.isEmpty()
                    ? LocalDate.MIN
                    : LocalDate.of(Integer.parseInt(yearsOfPublication.getFirst()), 1, 1))
            .documentTypes(extractDocumentTypes(literatureLdml))
            .dependentReferences(extractDependentReferences(literatureLdml))
            .independentReferences(extractIndependentReferences(literatureLdml))
            .normReferences(extractNormReferences(literatureLdml))
            .mainTitle(extractMainTitle(literatureLdml))
            .mainTitleAdditions(extractMainTitleAdditions(literatureLdml))
            .documentaryTitle(extractDocumentaryTitle(literatureLdml))
            .authors(extractAuthors(literatureLdml))
            .collaborators(extractCollaborators(literatureLdml))
            .originators(extractOriginators(literatureLdml))
            .conferenceNotes(extractConferenceNotes(literatureLdml))
            .languages(extractLanguages(literatureLdml))
            .shortReport(extractShortReport(literatureLdml))
            .outline((extractOutline(literatureLdml)))
            .indexedAt(Instant.now().toString())
            .build());
  }

  private static String extractDocumentNumber(LiteratureLdml literatureLdml)
      throws ValidationException {
    var documentNumber =
        Optional.ofNullable(literatureLdml)
            .map(LiteratureLdml::getDoc)
            .map(Doc::getMeta)
            .map(Meta::getIdentification)
            .map(Identification::getFrbrExpression)
            .map(FrbrExpression::getFrbrAlias)
            .orElse(Collections.emptyList())
            .stream()
            .filter(alias -> Objects.equals(alias.getName(), "documentNumber"))
            .findFirst()
            .map(FrbrNameValueElement::getValue)
            .orElse(null);

    if (documentNumber == null) {
      throw new ValidationException("Literature ldml has no documentNumber");
    }

    return documentNumber;
  }

  private static LocalDate extractRecordingDate(LiteratureLdml literatureLdml) {
    return Optional.ofNullable(literatureLdml)
        .map(LiteratureLdml::getDoc)
        .map(Doc::getMeta)
        .map(Meta::getIdentification)
        .map(Identification::getFrbrWork)
        .map(FrbrWork::getFrbrDate)
        .map(FrbrDate::getDate)
        .map(DateUtils::nullSafeParseyyyyMMdd)
        .orElse(null);
  }

  private static List<String> extractYearsOfPublication(LiteratureLdml literatureLdml) {
    return Optional.ofNullable(literatureLdml)
        .map(LiteratureLdml::getDoc)
        .map(Doc::getMeta)
        .map(Meta::getProprietary)
        .map(Proprietary::getMetadata)
        .map(Metadata::getYearsOfPublication)
        .orElse(Collections.emptyList());
  }

  private static List<String> extractDocumentTypes(LiteratureLdml literatureLdml) {
    return Optional.ofNullable(literatureLdml)
        .map(LiteratureLdml::getDoc)
        .map(Doc::getMeta)
        .map(Meta::getClassifications)
        .orElse(Collections.emptyList())
        .stream()
        .filter(classification -> Objects.equals(classification.getSource(), "doktyp"))
        .findFirst()
        .map(Classification::getKeywords)
        .orElse(Collections.emptyList())
        .stream()
        .map(Keyword::getValue)
        .toList();
  }

  private static List<String> extractDependentReferences(LiteratureLdml literatureLdml) {
    return Optional.ofNullable(literatureLdml)
        .map(LiteratureLdml::getDoc)
        .map(Doc::getMeta)
        .map(Meta::getAnalysis)
        .map(Analysis::getOtherReferences)
        .map(OtherReferences::getImplicitReferences)
        .orElse(Collections.emptyList())
        .stream()
        .filter(implicitReference -> implicitReference.getFundstelleUnselbstaendig() != null)
        .map(ImplicitReference::getShowAs)
        .toList();
  }

  private static List<String> extractIndependentReferences(LiteratureLdml literatureLdml) {
    return Optional.ofNullable(literatureLdml)
        .map(LiteratureLdml::getDoc)
        .map(Doc::getMeta)
        .map(Meta::getAnalysis)
        .map(Analysis::getOtherReferences)
        .map(OtherReferences::getImplicitReferences)
        .orElse(Collections.emptyList())
        .stream()
        .filter(implicitReference -> implicitReference.getFundstelleSelbstaendig() != null)
        .map(ImplicitReference::getShowAs)
        .toList();
  }

  private static List<String> extractNormReferences(LiteratureLdml literatureLdml) {
    return Optional.ofNullable(literatureLdml)
        .map(LiteratureLdml::getDoc)
        .map(Doc::getMeta)
        .map(Meta::getAnalysis)
        .map(Analysis::getOtherReferences)
        .map(OtherReferences::getImplicitReferences)
        .orElse(Collections.emptyList())
        .stream()
        .filter(implicitReference -> implicitReference.getNormReference() != null)
        .map(ImplicitReference::getShowAs)
        .toList();
  }

  private static String extractMainTitle(LiteratureLdml literatureLdml) {
    return extractFrbrAlias(literatureLdml, "haupttitel");
  }

  private static String extractMainTitleAdditions(LiteratureLdml literatureLdml) {
    return extractFrbrAlias(literatureLdml, "hauptsachtitelZusatz");
  }

  private static String extractDocumentaryTitle(LiteratureLdml literatureLdml) {
    return extractFrbrAlias(literatureLdml, "dokumentarischerTitel");
  }

  private static List<String> extractAuthors(LiteratureLdml literatureLdml) {
    return extractPerson(literatureLdml, "#verfasser");
  }

  private static List<String> extractCollaborators(LiteratureLdml literatureLdml) {
    return extractPerson(literatureLdml, "#mitarbeiter");
  }

  private static List<String> extractOriginators(LiteratureLdml literatureLdml) {
    var originatorEids = getFrbrAuthorReferenceLinks(literatureLdml, "#urheber");

    return Optional.ofNullable(literatureLdml)
        .map(LiteratureLdml::getDoc)
        .map(Doc::getMeta)
        .map(Meta::getReferences)
        .map(References::getTlcOrganizations)
        .orElse(Collections.emptyList())
        .stream()
        .filter(orga -> originatorEids.contains(orga.getEId()))
        .map(TlcOrganization::getName)
        .toList();
  }

  private static List<String> extractConferenceNotes(LiteratureLdml literatureLdml) {
    var conferenceEids = getFrbrAuthorReferenceLinks(literatureLdml, "#kongress");

    return Optional.ofNullable(literatureLdml)
        .map(LiteratureLdml::getDoc)
        .map(Doc::getMeta)
        .map(Meta::getReferences)
        .map(References::getTlcEvents)
        .orElse(Collections.emptyList())
        .stream()
        .filter(event -> conferenceEids.contains(event.getEId()))
        .map(TlcEvent::getShowAs)
        .toList();
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
            Optional.ofNullable(literatureLdml)
                .map(LiteratureLdml::getDoc)
                .map(Doc::getMeta)
                .map(Meta::getProprietary)
                .map(Proprietary::getMetadata)
                .map(Metadata::getGliederung)
                .map(Gliederung::getGliederungEntry)
                .orElse(Collections.emptyList()));

    return outline.isEmpty() ? null : outline;
  }

  private static List<String> getFrbrAuthorReferenceLinks(
      LiteratureLdml literatureLdml, String type) {

    return Optional.ofNullable(literatureLdml)
        .map(LiteratureLdml::getDoc)
        .map(Doc::getMeta)
        .map(Meta::getIdentification)
        .map(Identification::getFrbrWork)
        .map(FrbrWork::getFrbrAuthors)
        .orElse(Collections.emptyList())
        .stream()
        .filter(author -> Objects.equals(author.getAs(), type))
        .map(
            author ->
                Optional.ofNullable(author.getHref())
                    .map(href -> href.replaceFirst("#", ""))
                    .orElse(null))
        .filter(Objects::nonNull)
        .toList();
  }

  private static List<String> extractPerson(LiteratureLdml literatureLdml, String authorType) {
    var personEids = getFrbrAuthorReferenceLinks(literatureLdml, authorType);

    return Optional.ofNullable(literatureLdml)
        .map(LiteratureLdml::getDoc)
        .map(Doc::getMeta)
        .map(Meta::getReferences)
        .map(References::getTlcPersons)
        .orElse(Collections.emptyList())
        .stream()
        .filter(person -> personEids.contains(person.getEId()))
        .map(TlcPerson::getName)
        .toList();
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
}
