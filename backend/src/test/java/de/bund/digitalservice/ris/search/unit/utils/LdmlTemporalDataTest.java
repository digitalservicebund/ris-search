package de.bund.digitalservice.ris.search.unit.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import de.bund.digitalservice.ris.search.models.ldml.TimeInterval;
import de.bund.digitalservice.ris.search.utils.LdmlTemporalData;
import de.bund.digitalservice.ris.search.utils.XmlDocument;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

class LdmlTemporalDataTest {

  private String testContent =
      """
         <?xml-model href="../../../Grammatiken/legalDocML.de.sch" schematypens="http://purl.oclc.org/dsdl/schematron"?>
         <?xml-model href="../../../Grammatiken/legalDocML.de.sch" schematypens="http://purl.oclc.org/dsdl/schematron"?>
         <?xml-model href="../../../Grammatiken/legalDocML.de.sch" schematypens="http://purl.oclc.org/dsdl/schematron"?>
         <akn:akomaNtoso xmlns:akn="http://Inhaltsdaten.LegalDocML.de/1.8.1/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://Inhaltsdaten.LegalDocML.de/1.8.1/ ../schema/legalDocML.de-regelungstextverkuendungsfassung.xsd">
           <akn:act name="regelungstext">
             <akn:meta GUID="87b3446a-faa1-4d81-bb34-48ce7929de86" eId="meta-1">
               <akn:lifecycle GUID="21800a74-04c9-424e-8017-c63fe835a017" eId="meta-1_lebzykl-1" source="attributsemantik-noch-undefiniert">
                 <akn:eventRef GUID="b0cae450-f3ad-45e7-9b7d-7a93ecfbf811" date="2003-11-03" eId="meta-1_lebzykl-1_ereignis-1" refersTo="ausfertigung" source="attributsemantik-noch-undefiniert" type="generation"/>
                 <akn:eventRef GUID="edab3f88-47fd-480a-bb7e-60790585ecbb" date="2003-11-06" eId="meta-1_lebzykl-1_ereignis-2" refersTo="inkrafttreten" source="attributsemantik-noch-undefiniert" type="generation"/>
                 <akn:eventRef GUID="b0cae450-f3ad-45e7-9b7d-7a93ecfbf811" date="2003-11-01" eId="meta-1_lebzykl-1_ereignis-3" refersTo="inkrafttreten-weggefallen" source="attributsemantik-noch-undefiniert" type="amendment"/>
               </akn:lifecycle>
               <akn:temporalData GUID="5f144775-0382-4c9b-aca6-85c3030b4299" eId="meta-1_geltzeiten-1" source="attributsemantik-noch-undefiniert">
                 <akn:temporalGroup GUID="069968d8-2839-471a-b118-d4a8e3a4f0f4" eId="meta-1_geltzeiten-1_geltungszeitgr-1">
                   <akn:timeInterval GUID="e41f265d-288c-4b2a-911a-ea17090dcf9e" eId="meta-1_geltzeiten-1_geltungszeitgr-1_gelzeitintervall-1" refersTo="geltungszeit" start="#meta-1_lebzykl-1_ereignis-1"/>
                 </akn:temporalGroup>
                 <akn:temporalGroup GUID="069968d8-2839-471a-b118-d4a8e3a4f0f5" eId="meta-1_geltzeiten-1_geltungszeitgr-2">
                   <akn:timeInterval GUID="e41f265d-288c-4b2a-911a-ea17090dcf9f" eId="meta-1_geltzeiten-1_geltungszeitgr-2_gelzeitintervall-1" end="#meta-1_lebzykl-1_ereignis-2" refersTo="geltungszeit" start="#meta-1_lebzykl-1_ereignis-1"/>
                 </akn:temporalGroup>
                 <akn:temporalGroup GUID="069968d8-2839-471a-b118-d4a8e3a4f0f6" eId="meta-1_geltzeiten-1_geltungszeitgr-3">
                   <akn:timeInterval GUID="e41f265d-288c-4b2a-911a-ea17090dcf1f" eId="meta-1_geltzeiten-1_geltungszeitgr-3_gelzeitintervall-1" end="#meta-1_lebzykl-1_ereignis-3" refersTo="geltungszeit"/>
                 </akn:temporalGroup>
               </akn:temporalData>
             </akn:meta>
           </akn:act>
         </akn:akomaNtoso>
         """;

  @Test
  void testGetTemporalDataWithDatesMapping()
      throws ParserConfigurationException, IOException, SAXException {
    XmlDocument xmlDocument = new XmlDocument(testContent.getBytes());
    Map<String, TimeInterval> result =
        LdmlTemporalData.getTemporalDataWithDatesMapping(xmlDocument);
    assert result.size() == 3;
    assert result.get("#meta-1_geltzeiten-1_geltungszeitgr-1").getStart().equals("2003-11-03");
    assertNull(result.get("#meta-1_geltzeiten-1_geltungszeitgr-1").getEnd());
    assert result.get("#meta-1_geltzeiten-1_geltungszeitgr-2").getStart().equals("2003-11-03");
    assert result.get("#meta-1_geltzeiten-1_geltungszeitgr-2").getEnd().equals("2003-11-06");
    assertNull(result.get("#meta-1_geltzeiten-1_geltungszeitgr-3").getStart());
    assert result.get("#meta-1_geltzeiten-1_geltungszeitgr-3").getEnd().equals("2003-11-01");
  }

  @Test
  void testPrivateConstructorThrowsException() throws Exception {
    Constructor<LdmlTemporalData> constructor = LdmlTemporalData.class.getDeclaredConstructor();
    constructor.setAccessible(true);

    InvocationTargetException thrown =
        assertThrows(InvocationTargetException.class, constructor::newInstance);

    assertTrue(thrown.getCause() instanceof IllegalStateException);
    assertEquals("Utility class", thrown.getCause().getMessage());
  }
}
