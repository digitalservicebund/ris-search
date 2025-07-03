package de.bund.digitalservice.ris.search.unit.nlex.schema.result;

import de.bund.digitalservice.ris.TestXmlUtils;
import de.bund.digitalservice.ris.search.nlex.schema.result.Content;
import de.bund.digitalservice.ris.search.nlex.schema.result.Document;
import de.bund.digitalservice.ris.search.nlex.schema.result.Error;
import de.bund.digitalservice.ris.search.nlex.schema.result.ExternUrl;
import de.bund.digitalservice.ris.search.nlex.schema.result.Navigation;
import de.bund.digitalservice.ris.search.nlex.schema.result.Page;
import de.bund.digitalservice.ris.search.nlex.schema.result.Para;
import de.bund.digitalservice.ris.search.nlex.schema.result.ParagraphRoles;
import de.bund.digitalservice.ris.search.nlex.schema.result.References;
import de.bund.digitalservice.ris.search.nlex.schema.result.RequestResult;
import de.bund.digitalservice.ris.search.nlex.schema.result.ResultList;
import de.bund.digitalservice.ris.search.nlex.schema.result.ResultStatus;
import jakarta.xml.bind.JAXB;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RequestResponseSchemaTest {

  @Test
  void theSchemaIsCorrectInErrorCases() throws IOException {
    String expected =
        """
             <?xml version="1.0" encoding="UTF-8"?>
                <result status="error" site="site" connector="connector">
                  <errors>
                    <error cause="1"/>
                  </errors>
                </result>
            """;

    RequestResult result =
        new RequestResult()
            .setStatus(ResultStatus.ERROR)
            .setSite("site")
            .setConnector("connector")
            .setErrors(List.of(new Error().setCause("1")));
    StringWriter sw = new StringWriter();
    JAXB.marshal(result, sw);
    Assertions.assertEquals(TestXmlUtils.trim(expected), TestXmlUtils.trim(sw.toString()));
  }

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
                                <content lang="de-DE">
                                  <title>docTitle</title>
                                  <para roles="zoom">textMatch1</para>
                                  <para roles="zoom">textMatch2</para>
                                </content>
                            </document>
                        </documents>
                    </result-list>
                </result>
                """;

    String expectedXml = TestXmlUtils.trim(exampleXml);
    JAXBContext ctx = JAXBContext.newInstance(RequestResult.class);

    RequestResult result = new RequestResult();
    result.setStatus("OK");
    result.setConnector("https://testphase.rechtsinformationen.bund.de/nlex");
    result.setSite("https://testphase.rechtsinformationen.bund.de/");
    ResultList resultList = new ResultList();
    References ref = new References();
    ExternUrl externUrl = new ExternUrl();
    externUrl.setHref("link/to/legislation.html");
    externUrl.setFormat("text/html");
    externUrl.setDisplay("HTML extern");
    ref.setExternUrl(externUrl);
    Document doc = new Document();
    doc.setReferences(ref);
    Content content = new Content();
    content.setLang(Content.LANG_DE_DE);
    content.setTitle("docTitle");
    Para para1 = new Para();
    para1.setValue("textMatch1");
    para1.setRoles(ParagraphRoles.ZOOM);
    Para para2 = new Para();
    para2.setValue("textMatch2");
    para2.setRoles(ParagraphRoles.ZOOM);
    content.setParaList(List.of(para1, para2));
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
}
