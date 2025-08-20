package de.bund.digitalservice.ris.search.unit.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import de.bund.digitalservice.ris.search.mapper.literature.LiteratureLdmlToOpenSearchMapper;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LiteratureLdmlToOpenSearchMapperTest {

  private final String minimalValidLdml =
      """
      <akn:akomaNtoso xmlns:akn="http://docs.oasis-open.org/legaldocml/ns/akn/3.0">
        <akn:doc name="offene-struktur">
          <akn:meta>
            <akn:identification>
              <akn:FRBRExpression>
                <akn:FRBRalias name="documentNumber" value="BJLU002758328" />
              </akn:FRBRExpression>
            </akn:identification>
          </akn:meta>
        </akn:doc>
      </akn:akomaNtoso>
      """
          .stripIndent();

  @Test
  @DisplayName("Extracts and sets document number")
  void extractsAndSetsDocumentNumber() {
    Literature literature =
        LiteratureLdmlToOpenSearchMapper.parseLiteratureLdml(minimalValidLdml).get();

    assertEquals("BJLU002758328", literature.documentNumber());
  }

  @Test
  @DisplayName("Returns empty optional if document number is missing")
  void returnsEmptyOptionalIfDocumentNumberIsMissing() {
    String literatureLdml =
        """
            <akn:akomaNtoso xmlns:akn="http://docs.oasis-open.org/legaldocml/ns/akn/3.0">
              <akn:doc name="offene-struktur">
              </akn:doc>
            </akn:akomaNtoso>
            """
            .stripIndent();
    Optional<Literature> literature =
        LiteratureLdmlToOpenSearchMapper.parseLiteratureLdml(literatureLdml);

    assertEquals(Optional.empty(), literature);
  }

  @Test
  @DisplayName("Extracts and sets years of publication")
  void extractsAndSetsYearsOfPublication() {
    String literatureLdml =
        """
            <akn:akomaNtoso xmlns:akn="http://docs.oasis-open.org/legaldocml/ns/akn/3.0"
             xmlns:ris="http://ldml.neuris.de/literature/metadata/">
             <akn:doc name="offene-struktur">
               <akn:meta>
                   <akn:identification>
                     <akn:FRBRExpression>
                       <akn:FRBRalias name="documentNumber" value="BJLU002758328" />
                     </akn:FRBRExpression>
                   </akn:identification>
                   <akn:proprietary>
                     <ris:metadata>
                       <ris:veroeffentlichungsJahre>
                         <ris:veroeffentlichungsJahr>2009</ris:veroeffentlichungsJahr>
                       </ris:veroeffentlichungsJahre>
                     </ris:metadata>
                 </akn:proprietary>
               </akn:meta>
             </akn:doc>
           </akn:akomaNtoso>
           """
            .stripIndent();
    Literature literature =
        LiteratureLdmlToOpenSearchMapper.parseLiteratureLdml(literatureLdml).get();

    assertEquals(List.of("2009"), literature.yearsOfPublication());
  }

  @Test
  @DisplayName("Extracts and sets document types")
  void extractsAndSetsDocumentTypes() {
    String literatureLdml =
        """
            <akn:akomaNtoso xmlns:akn="http://docs.oasis-open.org/legaldocml/ns/akn/3.0"
             xmlns:ris="http://ldml.neuris.de/literature/metadata/">
             <akn:doc name="offene-struktur">
               <akn:meta>
                   <akn:identification>
                     <akn:FRBRExpression>
                       <akn:FRBRalias name="documentNumber" value="BJLU002758328" />
                     </akn:FRBRExpression>
                   </akn:identification>
                   <akn:classification source="doktyp">
                      <akn:keyword dictionary="attributsemantik-noch-undefiniert" showAs="foo" value="Auf"/>
                   </akn:classification>
               </akn:meta>
             </akn:doc>
           </akn:akomaNtoso>
           """
            .stripIndent();
    Literature literature =
        LiteratureLdmlToOpenSearchMapper.parseLiteratureLdml(literatureLdml).get();

    assertEquals(List.of("Auf"), literature.documentTypes());
  }

  @Test
  @DisplayName("Extracts and sets main title")
  void extractsAndSetsMainTitle() {
    String literatureLdml =
        """
              <akn:akomaNtoso xmlns:akn="http://docs.oasis-open.org/legaldocml/ns/akn/3.0"
               xmlns:ris="http://ldml.neuris.de/literature/metadata/">
               <akn:doc name="offene-struktur">
                 <akn:meta>
                     <akn:identification>
                       <akn:FRBRExpression>
                         <akn:FRBRalias name="documentNumber" value="BJLU002758328" />
                       </akn:FRBRExpression>
                     </akn:identification>
                 </akn:meta>
                 <akn:preface>
                    <akn:longTitle>
                     <akn:block name="longTitle">This is a long title</akn:block>
                     <akn:block name="foo">This should not be considered</akn:block>
                    </akn:longTitle>
                  </akn:preface>
               </akn:doc>
             </akn:akomaNtoso>
             """
            .stripIndent();

    Literature literature =
        LiteratureLdmlToOpenSearchMapper.parseLiteratureLdml(literatureLdml).get();

    assertEquals("This is a long title", literature.mainTitle());
  }

  @Test
  @DisplayName("Extracts and sets documentary title")
  void extractsAndSetsDocumentaryTitle() {
    String literatureLdml =
        """
              <akn:akomaNtoso xmlns:akn="http://docs.oasis-open.org/legaldocml/ns/akn/3.0"
               xmlns:ris="http://ldml.neuris.de/literature/metadata/">
               <akn:doc name="offene-struktur">
                 <akn:meta>
                     <akn:identification>
                      <akn:FRBRWork>
                        <akn:FRBRalias name="dokumentarischerTitel" value="Dokumentarischer Titel"/>
                      </akn:FRBRWork>
                      <akn:FRBRExpression>
                        <akn:FRBRalias name="documentNumber" value="BJLU002758328" />
                       </akn:FRBRExpression>
                     </akn:identification>
                 </akn:meta>
               </akn:doc>
             </akn:akomaNtoso>
             """
            .stripIndent();

    Literature literature =
        LiteratureLdmlToOpenSearchMapper.parseLiteratureLdml(literatureLdml).get();

    assertEquals("Dokumentarischer Titel", literature.documentaryTitle());
  }

  @Test
  @DisplayName("Does not set values for missing optional datapoints")
  void doesNotSetValuesForMissingOptionalDatapoints() {
    Literature literature =
        LiteratureLdmlToOpenSearchMapper.parseLiteratureLdml(minimalValidLdml).get();

    assertEquals(Collections.emptyList(), literature.yearsOfPublication());
    assertEquals(Collections.emptyList(), literature.documentTypes());
    assertNull(literature.mainTitle());
    assertNull(literature.documentaryTitle());
  }
}
