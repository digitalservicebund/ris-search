package de.bund.digitalservice.ris.search.legacyportal.service;

import de.bund.digitalservice.ris.search.legacyportal.model.LegalDocument;
import de.bund.digitalservice.ris.search.legacyportal.repository.LegalDocumentRepository;
import de.bund.digitalservice.ris.search.legacyportal.transformer.LegalDocMLTransformer;
import de.bund.digitalservice.ris.search.legacyportal.utils.LocalLegalDocumentUtils;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

@EnableAsync
@Service
public class LegalDocMLImportService {

  private final Logger logger = LogManager.getLogger(LegalDocMLImportService.class);

  private final LegalDocumentRepository legalDocumentRepository;

  @Autowired
  public LegalDocMLImportService(LegalDocumentRepository legalDocumentRepository) {
    this.legalDocumentRepository = legalDocumentRepository;
  }

  @Async
  @EventListener(
      value = ApplicationReadyEvent.class,
      condition = "@environment.acceptsProfiles('default', 'staging', 'production')")
  public void loadLegalDocML() {
    legalDocumentRepository.deleteAll();
    this.importLocalLegalDocuments14();
    this.importLocalLegalDocuments16();
  }

  private void importLocalLegalDocuments14() {
    var fileList = LocalLegalDocumentUtils.getFiles(LocalLegalDocumentUtils.LDML_1_4_FOLDER);

    if (fileList.isEmpty()) {
      return;
    }
    List<LegalDocument> documents = new ArrayList<>();
    fileList.get().stream()
        .forEach(
            file -> {
              try (FileInputStream fl =
                  new FileInputStream(
                      LocalLegalDocumentUtils.LDML_1_4_FOLDER.getFile() + "/" + file)) {
                var legalDocument =
                    LegalDocMLTransformer.createLegalDocumentByBytes(fl.readAllBytes(), file);
                if (legalDocument.isPresent()) {
                  documents.add(legalDocument.get());
                }
              } catch (IOException | ParserConfigurationException | SAXException ex) {
                logger.error("Couldn't create legal document by bytes");
              }
            });

    legalDocumentRepository.saveAll(documents);
  }

  private void importLocalLegalDocuments16() {
    var fileList = LocalLegalDocumentUtils.getFiles(LocalLegalDocumentUtils.LDML_1_6_FOLDER);

    if (fileList.isEmpty()) {
      return;
    }
    List<LegalDocument> documents = new ArrayList<>();
    fileList.get().stream()
        .forEach(
            file -> {
              try (FileInputStream fl =
                  new FileInputStream(
                      LocalLegalDocumentUtils.LDML_1_6_FOLDER.getFile() + "/" + file)) {
                var legalDocument =
                    LegalDocMLTransformer.createLegalDocumentByBytes(fl.readAllBytes(), file);
                if (legalDocument.isPresent()) {
                  documents.add(legalDocument.get());
                }
              } catch (IOException | ParserConfigurationException | SAXException ex) {
                logger.error("Couldn't create legal document by bytes");
              }
            });

    legalDocumentRepository.saveAll(documents);
  }
}
