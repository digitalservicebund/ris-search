package de.bund.digitalservice.ris.search.mapper;

import static de.bund.digitalservice.ris.search.utils.MappingUtils.nullSafeGet;
import static de.bund.digitalservice.ris.search.utils.MappingUtils.validate;
import static de.bund.digitalservice.ris.search.utils.MappingUtils.validateNotNull;

import de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml.AknBlock;
import de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml.AknKeyword;
import de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml.AknMultipleBlock;
import de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml.CaseLawLdml;
import de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml.FrbrDate;
import de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml.FrbrElement;
import de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml.JaxbHtml;
import de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml.Judgment;
import de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml.JudgmentBody;
import de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml.Meta;
import de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml.RisMeta;
import de.bund.digitalservice.ris.search.exception.OpenSearchMapperException;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.utils.DateUtils;
import de.bund.digitalservice.ris.search.utils.MappingUtils;
import jakarta.xml.bind.DataBindingException;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.ValidationException;
import java.io.StringReader;
import java.time.Instant;
import javax.xml.transform.stream.StreamSource;
import org.eclipse.persistence.exceptions.DescriptorException;

public class CaseLawLdmlToOpenSearchMapper {

  private CaseLawLdmlToOpenSearchMapper() {}

  public static CaseLawDocumentationUnit mapToEntity(CaseLawLdml caseLawLdml)
      throws ValidationException {
    validateNotNull(caseLawLdml.getJudgment(), "Judgment missing");
    Judgment judgment = caseLawLdml.getJudgment();
    validateNotNull(judgment.getMeta(), "Meta missing");
    Meta meta = judgment.getMeta();
    validateNotNull(meta.getProprietary(), "Proprietary missing");
    validateNotNull(meta.getProprietary().getMeta(), "RisMeta missing");
    RisMeta risMeta = meta.getProprietary().getMeta();
    validateNotNull(risMeta.getCourtType(), "CourtType missing");
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
    JaxbHtml motivation = judgmentBody.getMotivation();

    AknMultipleBlock introduction = judgmentBody.getIntroduction();
    AknBlock headnoteBlock = nullSafeGet(introduction, e -> e.getBlock("Orientierungssatz"));
    JaxbHtml headnote = nullSafeGet(headnoteBlock, AknBlock::getContent);
    AknBlock otherHeadnoteBlock =
        nullSafeGet(introduction, e -> e.getBlock("Sonstiger Orientierungssatz"));
    JaxbHtml otherHeadnote = nullSafeGet(otherHeadnoteBlock, AknBlock::getContent);
    AknBlock outlineBlock = nullSafeGet(introduction, e -> e.getBlock("Gliederung"));
    JaxbHtml outline = nullSafeGet(outlineBlock, AknBlock::getContent);
    AknBlock tenorBlock = nullSafeGet(introduction, e -> e.getBlock("Tenor"));
    JaxbHtml tenor = nullSafeGet(tenorBlock, AknBlock::getContent);

    JaxbHtml background = judgmentBody.getBackground();

    AknMultipleBlock decision = judgmentBody.getDecision();
    AknBlock dissentingOpinionBlock = nullSafeGet(decision, e -> e.getBlock("Abweichende Meinung"));
    JaxbHtml dissentingOpinion = nullSafeGet(dissentingOpinionBlock, AknBlock::getContent);

    AknBlock decisionGroundsBlock = nullSafeGet(decision, e -> e.getBlock("Entscheidungsgründe"));
    JaxbHtml decisionGrounds = nullSafeGet(decisionGroundsBlock, AknBlock::getContent);
    AknBlock groundsBlock = nullSafeGet(decision, e -> e.getBlock("Gründe"));
    JaxbHtml grounds = nullSafeGet(groundsBlock, AknBlock::getContent);
    AknBlock otherLongTextBlock = nullSafeGet(decision, e -> e.getBlock("Sonstiger Langtext"));
    JaxbHtml otherLongText = nullSafeGet(otherLongTextBlock, AknBlock::getContent);

    // some fields not in ldml are commented for now
    return CaseLawDocumentationUnit.builder()
        // Meta elements
        .id(uniqueIdentifier)
        .documentNumber(uniqueIdentifier)
        .ecli(ecli)
        .decisionDate(DateUtils.nullSafeParseyyyyMMdd(frbrDate.getDate()))
        .fileNumbers(risMeta.getFileNumbers())
        .courtType(risMeta.getCourtType())
        .location(risMeta.getCourtLocation())
        .documentType(risMeta.getDocumentType())
        .judicialBody(risMeta.getJudicialBody())
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
        .guidingPrinciple(jaxbToSanitizedHtml(motivation))
        .headnote(jaxbToSanitizedHtml(headnote))
        .otherHeadnote(jaxbToSanitizedHtml(otherHeadnote))
        .outline(jaxbToSanitizedHtml(outline))
        .tenor(jaxbToSanitizedHtml(tenor))
        .caseFacts(jaxbToSanitizedHtml(background))
        .decisionGrounds(jaxbToSanitizedHtml(decisionGrounds))
        .grounds(jaxbToSanitizedHtml(grounds))
        .otherLongText(jaxbToSanitizedHtml(otherLongText))
        .dissentingOpinion(jaxbToSanitizedHtml(dissentingOpinion))

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

  public static CaseLawDocumentationUnit fromString(String ldmlFile) {
    try {
      StreamSource ldmlStreamSource = new StreamSource(new StringReader(ldmlFile));
      JAXBContext jaxbContext = JAXBContext.newInstance(CaseLawLdml.class);
      Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

      CaseLawLdml ldml = (CaseLawLdml) unmarshaller.unmarshal(ldmlStreamSource);
      return CaseLawLdmlToOpenSearchMapper.mapToEntity(ldml);
    } catch (DescriptorException | DataBindingException | JAXBException e) {
      throw new OpenSearchMapperException("unable to parse file to DocumentationUnit", e);
    }
  }
}
