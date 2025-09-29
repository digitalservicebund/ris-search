package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.models.ldml.literature.Analysis;
import de.bund.digitalservice.ris.search.models.ldml.literature.Classification;
import de.bund.digitalservice.ris.search.models.ldml.literature.Doc;
import de.bund.digitalservice.ris.search.models.ldml.literature.FrbrExpression;
import de.bund.digitalservice.ris.search.models.ldml.literature.FrbrNameValueElement;
import de.bund.digitalservice.ris.search.models.ldml.literature.FrbrWork;
import de.bund.digitalservice.ris.search.models.ldml.literature.Gliederung;
import de.bund.digitalservice.ris.search.models.ldml.literature.Identification;
import de.bund.digitalservice.ris.search.models.ldml.literature.Keyword;
import de.bund.digitalservice.ris.search.models.ldml.literature.LiteratureLdml;
import de.bund.digitalservice.ris.search.models.ldml.literature.MainBody;
import de.bund.digitalservice.ris.search.models.ldml.literature.Meta;
import de.bund.digitalservice.ris.search.models.ldml.literature.Metadata;
import de.bund.digitalservice.ris.search.models.ldml.literature.Proprietary;
import de.bund.digitalservice.ris.search.models.ldml.literature.References;
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
    return Optional.of(
        Literature.builder()
            .id(documentNumber)
            .documentNumber(documentNumber)
            .recordingDate(extractRecordingDate(literatureLdml))
            .yearsOfPublication(extractYearsOfPublication(literatureLdml))
            .documentTypes(extractDocumentTypes(literatureLdml))
            .dependentReferences(extractDependentReferences(literatureLdml))
            .independentReferences(extractIndependentReferences(literatureLdml))
            .mainTitle(extractMainTitle(literatureLdml))
            .documentaryTitle(extractDocumentaryTitle(literatureLdml))
            .authors(extractAuthors(literatureLdml))
            .collaborators(extractCollaborators(literatureLdml))
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
    FrbrWork frbrWork = literatureLdml.getDoc().getMeta().getIdentification().getFrbrWork();
    if (frbrWork == null
        || frbrWork.getFrbrDate() == null
        || frbrWork.getFrbrDate().getDate() == null) {
      return null;
    }
    return DateUtils.nullSafeParseyyyyMMdd(frbrWork.getFrbrDate().getDate());
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
        .map(Analysis::getFundstelleUnselbstaendigList)
        .orElse(Collections.emptyList())
        .stream()
        .map(
            fundstelle ->
                buildFundstelleString(fundstelle.getPeriodikum(), fundstelle.getZitstelle()))
        .toList();
  }

  private static List<String> extractIndependentReferences(LiteratureLdml literatureLdml) {
    return Optional.ofNullable(literatureLdml)
        .map(LiteratureLdml::getDoc)
        .map(Doc::getMeta)
        .map(Meta::getAnalysis)
        .map(Analysis::getFundstelleSelbstaendigList)
        .orElse(Collections.emptyList())
        .stream()
        .map(fundstelle -> buildFundstelleString(fundstelle.getTitel(), fundstelle.getZitstelle()))
        .toList();
  }

  private static String buildFundstelleString(String firstPart, String secondPart) {
    return (Optional.ofNullable(firstPart).orElse(StringUtils.EMPTY)
            + StringUtils.SPACE
            + Optional.ofNullable(secondPart).orElse(StringUtils.EMPTY))
        .strip();
  }

  private static String extractMainTitle(LiteratureLdml literatureLdml) {
    return Optional.ofNullable(literatureLdml)
        .map(LiteratureLdml::getDoc)
        .map(Doc::getMeta)
        .map(Meta::getIdentification)
        .map(Identification::getFrbrWork)
        .map(FrbrWork::getFrbrAliasList)
        .orElse(Collections.emptyList())
        .stream()
        .filter(alias -> Objects.equals(alias.getName(), "haupttitel"))
        .findFirst()
        .map(FrbrNameValueElement::getValue)
        .orElse(null);
  }

  private static String extractDocumentaryTitle(LiteratureLdml literatureLdml) {
    return Optional.ofNullable(literatureLdml)
        .map(LiteratureLdml::getDoc)
        .map(Doc::getMeta)
        .map(Meta::getIdentification)
        .map(Identification::getFrbrWork)
        .map(FrbrWork::getFrbrAliasList)
        .orElse(Collections.emptyList())
        .stream()
        .filter(alias -> Objects.equals(alias.getName(), "dokumentarischerTitel"))
        .findFirst()
        .map(FrbrNameValueElement::getValue)
        .orElse(null);
  }

  private static List<String> extractAuthors(LiteratureLdml literatureLdml) {
    return extractPerson(literatureLdml, "verfasser");
  }

  private static List<String> extractCollaborators(LiteratureLdml literatureLdml) {
    return extractPerson(literatureLdml, "mitarbeiter");
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

  private static List<String> extractPerson(LiteratureLdml literatureLdml, String type) {
    var persons =
        Optional.ofNullable(literatureLdml)
            .map(LiteratureLdml::getDoc)
            .map(Doc::getMeta)
            .map(Meta::getIdentification)
            .map(Identification::getFrbrWork)
            .map(FrbrWork::getFrbrAuthors)
            .orElse(Collections.emptyList())
            .stream()
            .filter(author -> Objects.equals(author.getAs(), "#" + type));

    var personEids =
        persons
            .map(
                author ->
                    Optional.ofNullable(author.getHref())
                        .map(href -> href.replaceFirst("#", ""))
                        .orElse(null))
            .filter(Objects::nonNull)
            .toList();

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
}
