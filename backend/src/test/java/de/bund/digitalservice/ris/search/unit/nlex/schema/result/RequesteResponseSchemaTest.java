package de.bund.digitalservice.ris.search.unit.nlex.schema.result;

import de.bund.digitalservice.ris.search.nlex.schema.result.Content;
import de.bund.digitalservice.ris.search.nlex.schema.result.Document;
import de.bund.digitalservice.ris.search.nlex.schema.result.ExternUrl;
import de.bund.digitalservice.ris.search.nlex.schema.result.Navigation;
import de.bund.digitalservice.ris.search.nlex.schema.result.Page;
import de.bund.digitalservice.ris.search.nlex.schema.result.References;
import de.bund.digitalservice.ris.search.nlex.schema.result.RequestResult;
import de.bund.digitalservice.ris.search.nlex.schema.result.ResultList;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RequesteResponseSchemaTest {

  @Test
  void theSchemaCreatesTheDesiredXml() throws JAXBException, IOException {
    String exampleXml =
        """
                <?xml version="1.0" encoding="UTF-8"?>
                <result status="OK" site="https://testphase.rechtsinformationen.bund.de/" connector="https://testphase.rechtsinformationen.bund.de/nlex">
                    <result-list>
                        <navigation>
                            <request-id>uuid-1111-1111</request-id>
                            <page number="1" size="10"/>
                            <hits>1</hits>
                        </navigation>
                        <documents>
                            <document>
                                <references>
                                    <extern-url format="text/html" display="HTML extern" href="link/to/legislation.html"/>
                                </references>
                                <content lang="de-DE"/>
                            </document>
                        </documents>
                    </result-list>
                </result>
                """;

    String expectedXml = trimXml(exampleXml);
    JAXBContext ctx = JAXBContext.newInstance(RequestResult.class);

    RequestResult result = new RequestResult();
    result.setStatus("OK");

    ResultList resultList = new ResultList();
    References ref = new References();
    ExternUrl externUrl = new ExternUrl();
    externUrl.setHref("link/to/legislation.html");
    ref.setExternUrl(externUrl);
    Document doc = new Document();
    doc.setReferences(ref);
    Content content = new Content();
    content.setLang(Content.LANG.DE_DE);
    doc.setContent(content);
    Navigation navigation = new Navigation();
    navigation.setHits(1);
    navigation.setRequestId("uuid-1111-1111");

    Page page = new Page();
    page.setNumber(1);
    page.setSize(10);
    navigation.setPage(page);

    resultList.setDocuments(List.of(doc));
    resultList.setNavigation(navigation);
    result.setResultList(resultList);

    Marshaller m = ctx.createMarshaller();
    StringWriter sw = new StringWriter();

    m.marshal(result, sw);

    String actualtXml = sw.toString();

    Assertions.assertEquals(expectedXml.trim(), actualtXml.trim());
  }

  /**
   * @param original pretty print xml
   * @return String single line xml
   * @throws IOException If an I/O error occurs
   */
  private String trimXml(String original) throws IOException {
    BufferedReader br = new BufferedReader(new StringReader(original));
    String line;
    StringBuilder sb = new StringBuilder();
    while ((line = br.readLine()) != null) {
      sb.append(line.trim());
    }
    return sb.toString();
  }
}
