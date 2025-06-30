package de.bund.digitalservice.ris.search.unit.nlex.schema.query;

import de.bund.digitalservice.ris.search.nlex.schema.query.Request;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.StringReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Since the provided xsd schema is having a lot of dynamic fields, the actual query might look
 * different depending on how exactly the input fields in the n-lex UI are parsed. Based on our
 * ris-query.xsd we can expect the request to have a schema similar to the testcases provided below
 */
class RequestSchemaTest {

  @Test
  void theQueryStringsWithSingleContainsGetsProperlyUnmarshalled() throws JAXBException {

    jakarta.xml.bind.JAXBContext ctx = JAXBContext.newInstance(Request.class);

    String exampleRequest =
        """
                    <request output_lang="de-DE">
                        <navigation>
                            <page number="1" />
                        </navigation>
                        <criteria encoding="utf8">
                                <words idx-name="nlex:fulltext">
                                    <contains>Sample expression</contains>
                                </words>
                        </criteria>
                    </request>
                """;

    Unmarshaller um = ctx.createUnmarshaller();

    Request request = (Request) um.unmarshal(new StringReader(exampleRequest));
    Assertions.assertEquals("Sample expression", request.getCriteria().getWords().getContains());
  }

  @Test
  void theQueryStringsWithSingleCriteriaInAndBlockGetsProperlyUnmarshalled() throws JAXBException {

    jakarta.xml.bind.JAXBContext ctx = JAXBContext.newInstance(Request.class);

    String exampleRequest =
        """
                    <request output_lang="de-DE">
                        <navigation>
                            <page number="1" />
                        </navigation>
                        <criteria encoding="utf8">
                           <and>
                                <words idx-name="nlex:fulltext">
                                    <contains>Sample expression</contains>
                                </words>
                           </and>
                        </criteria>
                    </request>
                """;

    Unmarshaller um = ctx.createUnmarshaller();
    Request request = (Request) um.unmarshal(new StringReader(exampleRequest));
    Assertions.assertEquals(
        "Sample expression", request.getCriteria().getAnd().getWords().getContains());
  }
}
