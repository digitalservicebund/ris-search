package de.bund.digitalservice.ris.search.legacyportal.transformer;

import de.bund.digitalservice.ris.search.legacyportal.enums.LegalDocumentType;
import de.bund.digitalservice.ris.search.legacyportal.model.ContentItem;
import de.bund.digitalservice.ris.search.legacyportal.model.LegalDocument;
import de.bund.digitalservice.ris.search.legacyportal.utils.xml.XmlParser;
import de.bund.digitalservice.xml.parser.XmlDocument;
import java.io.IOException;
import java.util.Optional;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

public class LegalDocMLTransformer {

  private LegalDocMLTransformer() {}

  public static Optional<LegalDocument> createLegalDocumentByBytes(byte[] data, String xmlFilePath)
      throws ParserConfigurationException, SAXException, IOException {
    var xmlParser = new XmlParser(data);
    var xmlDocument = new XmlDocument(data);

    var legislationImporterDTO =
        LegislationImporterDTOTransformer.createLegislationImporterDTO(
            xmlDocument, xmlFilePath, xmlParser);

    var contentItemList =
        legislationImporterDTO.contentItems().stream()
            .map(
                contentItem ->
                    ContentItem.builder()
                        .href(contentItem.href())
                        .markerNumber(contentItem.markerNumber())
                        .description(contentItem.description())
                        .build())
            .toList();

    return Optional.of(
        LegalDocument.builder()
            .identifier(legislationImporterDTO.identifier())
            .alternateName(legislationImporterDTO.alternateName())
            .documenttype(LegalDocumentType.LEGISLATION)
            .documentUri(legislationImporterDTO.documentUri())
            .name(legislationImporterDTO.name())
            .docTitle(legislationImporterDTO.docTitle())
            .text(legislationImporterDTO.text())
            .xmlFilePath(legislationImporterDTO.xmlFilePath())
            .printAnnouncementGazette(legislationImporterDTO.printAnnouncementGazette())
            .printAnnouncementYear(legislationImporterDTO.printAnnouncementYear())
            .printAnnouncementPage(legislationImporterDTO.printAnnouncementPage())
            .listOfContentItem(contentItemList)
            .globalUID(legislationImporterDTO.globalUID())
            .version(legislationImporterDTO.version())
            .build());
  }
}
