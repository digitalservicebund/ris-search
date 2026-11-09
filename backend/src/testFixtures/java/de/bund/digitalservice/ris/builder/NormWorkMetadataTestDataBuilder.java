package de.bund.digitalservice.ris.builder;

import de.bund.digitalservice.ris.search.mapper.NormWorkMetadataMapper;
import org.jetbrains.annotations.Nullable;

/** Builds test fixture content for a norm work's {@code metadaten.xml} file. */
public class NormWorkMetadataTestDataBuilder {
  private NormWorkMetadataTestDataBuilder() {}

  /**
   * Builds the XML content of a work-level {@code metadaten.xml} file.
   *
   * @param workEli the eli attribute of the metadaten root element
   * @param vollzitat the full citation text, or {@code null} to omit the vollzitat element
   * @return the XML content
   */
  public static String buildMetadatenXml(String workEli, @Nullable String vollzitat) {
    String vollzitatElement =
        vollzitat == null ? "" : "<vollzitat>%s</vollzitat>".formatted(escapeXml(vollzitat));
    return """
        <?xml version="1.0" encoding="UTF-8"?>
        <metadaten xmlns="%s" eli="%s">%s</metadaten>
        """
        .formatted(NormWorkMetadataMapper.NAMESPACE, workEli, vollzitatElement);
  }

  private static String escapeXml(String text) {
    return text.replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&apos;");
  }
}
