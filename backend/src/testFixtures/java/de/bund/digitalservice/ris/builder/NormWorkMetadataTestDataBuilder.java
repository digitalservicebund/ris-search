package de.bund.digitalservice.ris.builder;

import de.bund.digitalservice.ris.search.models.ldml.norm.NormWorkMetadata;
import de.bund.digitalservice.ris.search.models.ldml.norm.StandDerDokumentarischenBearbeitung;
import de.bund.digitalservice.ris.search.models.ldml.norm.Standangabe;
import de.bund.digitalservice.ris.utils.NormXmlValidator;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.List;
import org.eclipse.persistence.oxm.NamespacePrefixMapper;
import org.jetbrains.annotations.Nullable;

/** Builds test fixture content for a norm work's {@code metadaten.xml} file. */
public class NormWorkMetadataTestDataBuilder {
  private NormWorkMetadataTestDataBuilder() {}

  /**
   * Builds the XML content of a work-level {@code metadaten.xml} file.
   *
   * @param vollzitat the full citation text, or {@code null} to omit the vollzitat element
   * @return the XML content
   */
  public static String buildMetadatenXml(@Nullable String vollzitat) {
    return buildMetadatenXml(vollzitat, List.of(), List.of());
  }

  /**
   * Builds the XML content of a work-level {@code metadaten.xml} file.
   *
   * @param vollzitat the full citation text, or {@code null} to omit the vollzitat element
   * @param standangabe the {@code standangabe} elements to include
   * @param standDerDokumentarischenBearbeitung the {@code standDerDokumentarischenBearbeitung}
   *     elements to include
   * @return the XML content
   */
  public static String buildMetadatenXml(
      @Nullable String vollzitat,
      List<Standangabe> standangabe,
      List<StandDerDokumentarischenBearbeitung> standDerDokumentarischenBearbeitung) {
    try {
      JAXBContext context = JAXBContext.newInstance(NormWorkMetadata.class);
      Marshaller marshaller = context.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      marshaller.setProperty(
          "org.glassfish.jaxb.namespacePrefixMapper",
          new NamespacePrefixMapper() {
            @Override
            public String getPreferredPrefix(
                String namespaceUri, String suggestion, boolean requirePrefix) {
              // The metadata files use the default namespace
              return "";
            }
          });

      StringWriter writer = new StringWriter();
      marshaller.marshal(
          new NormWorkMetadata(vollzitat, standangabe, standDerDokumentarischenBearbeitung),
          writer);
      String xml = writer.toString();

      NormXmlValidator.validateContent(xml, NormXmlValidator.Type.WORK_METADATA);
      return xml;
    } catch (Exception e) {
      throw new IllegalArgumentException("Failed to generate XML", e);
    }
  }
}
