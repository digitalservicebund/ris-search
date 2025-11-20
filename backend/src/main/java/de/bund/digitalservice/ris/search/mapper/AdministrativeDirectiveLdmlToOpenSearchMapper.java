package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.exception.OpenSearchMapperException;
import de.bund.digitalservice.ris.search.models.ldml.directive.ActiveReference;
import de.bund.digitalservice.ris.search.models.ldml.directive.AdministrativeDirectiveLdml;
import de.bund.digitalservice.ris.search.models.ldml.directive.Analysis;
import de.bund.digitalservice.ris.search.models.ldml.directive.Block;
import de.bund.digitalservice.ris.search.models.ldml.directive.Doc;
import de.bund.digitalservice.ris.search.models.ldml.directive.DocumentType;
import de.bund.digitalservice.ris.search.models.ldml.directive.FieldOfLaw;
import de.bund.digitalservice.ris.search.models.ldml.directive.FrbrNameValueElement;
import de.bund.digitalservice.ris.search.models.ldml.directive.FrbrWork;
import de.bund.digitalservice.ris.search.models.ldml.directive.Identification;
import de.bund.digitalservice.ris.search.models.ldml.directive.ImplicitReference;
import de.bund.digitalservice.ris.search.models.ldml.directive.Keyword;
import de.bund.digitalservice.ris.search.models.ldml.directive.LongTitle;
import de.bund.digitalservice.ris.search.models.ldml.directive.MainBody;
import de.bund.digitalservice.ris.search.models.ldml.directive.Meta;
import de.bund.digitalservice.ris.search.models.ldml.directive.OtherReferences;
import de.bund.digitalservice.ris.search.models.ldml.directive.Preface;
import de.bund.digitalservice.ris.search.models.ldml.directive.Proprietary;
import de.bund.digitalservice.ris.search.models.ldml.directive.RisMeta;
import de.bund.digitalservice.ris.search.models.ldml.directive.Zuordnung;
import de.bund.digitalservice.ris.search.models.opensearch.AdministrativeDirective;
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
import org.eclipse.persistence.exceptions.DescriptorException;
import org.springframework.lang.Nullable;

public class AdministrativeDirectiveLdmlToOpenSearchMapper {
  private AdministrativeDirectiveLdmlToOpenSearchMapper() {}

  public static AdministrativeDirective map(AdministrativeDirectiveLdml ldml, Instant now) {
    try {

      String documentNumber = getDocumentNumber(ldml);

      return AdministrativeDirective.builder()
          .id(documentNumber)
          .documentNumber(documentNumber)
          .longTitle(getLongTitle(ldml))
          .documentCategory(getDocumentCategory(ldml))
          .documentType(getDocumentType(ldml))
          .content(getTextContent(ldml))
          .normgeber(getNormgeber(ldml))
          .entryIntoEffectDate(getEntryIntoEffect(ldml))
          .expiryDate(getExpiryDate(ldml))
          .normReferences(getNormReferences(ldml))
          .fundstelleReferences(getFundstelleReferences(ldml))
          .zitierdatumItems(getZitierdatumItems(ldml))
          .referenceNumbers(getReferenceNumbers(ldml))
          .caselawReferences(getCaselawReferences(ldml))
          .activeAdministrativeReferences(getActiveAdministrativeReferences(ldml))
          .activeNormReferences(getActiveNormReferences(ldml))
          .keywords(getKeywords(ldml))
          .fieldsOfLaw(getFieldsOfLaw(ldml))
          .zuordnungen(getZuordnungen(ldml))
          .indexedAt(now.toString())
          .build();
    } catch (ValidationException e) {
      throw new OpenSearchMapperException(e.getMessage());
    }
  }

  public static AdministrativeDirective map(String ldmlString, Instant now) {
    try {
      StreamSource ldmlStreamSource = new StreamSource(new StringReader(ldmlString));
      var ldml = JAXB.unmarshal(ldmlStreamSource, AdministrativeDirectiveLdml.class);
      return map(ldml, now);
    } catch (DescriptorException | DataBindingException e) {
      throw new OpenSearchMapperException("unable to parse file to administrative directive", e);
    }
  }

  private static @Nullable String getTextContent(AdministrativeDirectiveLdml ldml) {
    return Optional.ofNullable(ldml)
        .map(AdministrativeDirectiveLdml::getDoc)
        .map(Doc::getMainBody)
        .map(MainBody::getNormalizedTextContent)
        .orElse(null);
  }

  private static @Nullable String getLongTitle(AdministrativeDirectiveLdml ldml) {
    return Optional.ofNullable(ldml)
        .map(AdministrativeDirectiveLdml::getDoc)
        .map(Doc::getPreface)
        .map(Preface::getLongTitle)
        .map(LongTitle::getBlock)
        .map(Block::getValue)
        .map(String::trim)
        .orElse(null);
  }

  private static Optional<RisMeta> getRisMeta(AdministrativeDirectiveLdml ldml) {
    return Optional.ofNullable(ldml)
        .map(AdministrativeDirectiveLdml::getDoc)
        .map(Doc::getMeta)
        .map(Meta::getProprietary)
        .map(Proprietary::getMeta);
  }

  private static String getDocumentCategory(AdministrativeDirectiveLdml ldml)
      throws ValidationException {
    return getRisMeta(ldml)
        .map(RisMeta::getDocumentType)
        .map(DocumentType::getCategory)
        .orElseThrow(() -> new ValidationException("missing documentCategory"));
  }

  private static @Nullable String getDocumentType(AdministrativeDirectiveLdml ldml) {

    return getRisMeta(ldml)
        .map(RisMeta::getDocumentType)
        .map(DocumentType::getLongTitle)
        .orElse(null);
  }

  private static @Nullable String getNormgeber(AdministrativeDirectiveLdml ldml)
      throws ValidationException {

    var normgeberOption = getRisMeta(ldml).map(RisMeta::getNormgeber);
    if (normgeberOption.isPresent()) {
      StringBuilder sb =
          new StringBuilder(
              Optional.ofNullable(normgeberOption.get().getStaat())
                  .orElseThrow(() -> new ValidationException("not staat given in normgeber")));

      Optional.ofNullable(normgeberOption.get().getOrgan())
          .ifPresent(organ -> sb.append(" ").append(organ));
      return sb.toString();
    }
    return null;
  }

  private static @Nullable String getEntryIntoEffect(AdministrativeDirectiveLdml ldml) {

    return getRisMeta(ldml)
        .map(RisMeta::getEntryIntoEffectDate)
        .map(LocalDate::toString)
        .orElse(null);
  }

  private static @Nullable String getExpiryDate(AdministrativeDirectiveLdml ldml) {

    return getRisMeta(ldml).map(RisMeta::getExpiryDate).map(LocalDate::toString).orElse(null);
  }

  private static List<String> getNormReferences(AdministrativeDirectiveLdml ldml) {
    return Optional.ofNullable(ldml)
        .map(AdministrativeDirectiveLdml::getDoc)
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

  private static List<String> getCaselawReferences(AdministrativeDirectiveLdml ldml) {
    return Optional.ofNullable(ldml)
        .map(AdministrativeDirectiveLdml::getDoc)
        .map(Doc::getMeta)
        .map(Meta::getAnalysis)
        .map(Analysis::getOtherReferences)
        .map(OtherReferences::getImplicitReferences)
        .orElse(Collections.emptyList())
        .stream()
        .filter(implicitReference -> implicitReference.getCaselawReference() != null)
        .map(ImplicitReference::getShowAs)
        .toList();
  }

  private static List<String> getFundstelleReferences(AdministrativeDirectiveLdml ldml) {
    return Optional.ofNullable(ldml)
        .map(AdministrativeDirectiveLdml::getDoc)
        .map(Doc::getMeta)
        .map(Meta::getAnalysis)
        .map(Analysis::getOtherReferences)
        .map(OtherReferences::getImplicitReferences)
        .orElse(Collections.emptyList())
        .stream()
        .filter(
            implicitReference ->
                implicitReference.getNormReference() == null
                    && implicitReference.getCaselawReference() == null)
        .map(ImplicitReference::getShowAs)
        .toList();
  }

  private static List<String> getZitierdatumItems(AdministrativeDirectiveLdml ldml) {
    return getRisMeta(ldml).map(RisMeta::getDateToQuoteList).orElse(List.of()).stream()
        .filter(Objects::nonNull)
        .map(LocalDate::toString)
        .toList();
  }

  private static List<String> getReferenceNumbers(AdministrativeDirectiveLdml ldml) {
    return getRisMeta(ldml).map(RisMeta::getReferenceNumbers).orElse(List.of());
  }

  private static List<String> getActiveAdministrativeReferences(AdministrativeDirectiveLdml ldml) {
    return getRisMeta(ldml).map(RisMeta::getActiveReferences).orElse(List.of()).stream()
        .filter(ref -> ref.getParagraph() == null)
        .map(ActiveReference::getReference)
        .toList();
  }

  private static List<String> getActiveNormReferences(AdministrativeDirectiveLdml ldml) {
    return getRisMeta(ldml).map(RisMeta::getActiveReferences).orElse(List.of()).stream()
        .filter(ref -> ref.getParagraph() != null)
        .map(
            ref ->
                String.format(
                    "%s %s %s", ref.getReference(), ref.getParagraph(), ref.getPosition()))
        .toList();
  }

  private static List<String> getKeywords(AdministrativeDirectiveLdml ldml) {
    return Optional.ofNullable(ldml)
        .map(AdministrativeDirectiveLdml::getDoc)
        .map(Doc::getMeta)
        .map(Meta::getClassifications)
        .orElse(List.of())
        .stream()
        .flatMap(c -> c.getKeywords().stream())
        .map(Keyword::getValue)
        .toList();
  }

  private static String getDocumentNumber(AdministrativeDirectiveLdml ldml)
      throws ValidationException {
    var documentNumber =
        Optional.ofNullable(ldml)
            .map(AdministrativeDirectiveLdml::getDoc)
            .map(Doc::getMeta)
            .map(Meta::getIdentification)
            .map(Identification::getFrbrWork)
            .map(FrbrWork::getFrbrAliasList)
            .orElse(Collections.emptyList())
            .stream()
            .filter(alias -> Objects.equals(alias.getName(), "Dokumentnummer"))
            .findFirst()
            .map(FrbrNameValueElement::getValue)
            .orElse(null);

    if (documentNumber == null) {
      throw new ValidationException("ldml has no documentNumber");
    }

    return documentNumber;
  }

  private static List<String> getFieldsOfLaw(AdministrativeDirectiveLdml ldml)
      throws ValidationException {
    List<FieldOfLaw> fieldsOfLaw = getRisMeta(ldml).map(RisMeta::getFieldsOfLaw).orElse(List.of());

    for (FieldOfLaw law : fieldsOfLaw) {
      if (Objects.isNull(law.getValue())) {
        throw new ValidationException("field of law value is null");
      }
    }
    return fieldsOfLaw.stream().map(FieldOfLaw::getValue).toList();
  }

  private static List<String> getZuordnungen(AdministrativeDirectiveLdml ldml)
      throws ValidationException {
    List<Zuordnung> zuordnungen = getRisMeta(ldml).map(RisMeta::getZuordnungen).orElse(List.of());

    for (Zuordnung zuordnung : zuordnungen) {
      if (Objects.isNull(zuordnung.getAspekt()) || Objects.isNull(zuordnung.getBegriff())) {
        throw new ValidationException("invalid zuordnung");
      }
    }
    return zuordnungen.stream()
        .map(z -> String.format("%s %s", z.getAspekt(), z.getBegriff()))
        .toList();
  }
}
