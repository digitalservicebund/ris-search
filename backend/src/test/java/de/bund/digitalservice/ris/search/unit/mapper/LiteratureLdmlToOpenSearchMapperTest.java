package de.bund.digitalservice.ris.search.unit.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.mapper.literature.LiteratureLdmlToOpenSearchMapper;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
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

    assertThat(literature.documentNumber()).isEqualTo("BJLU002758328");
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

    assertThat(literature).isEmpty();
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

    assertThat(literature.yearsOfPublication()).containsExactly("2009");
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

    assertThat(literature.documentTypes()).containsExactly("Auf");
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

    assertThat(literature.mainTitle()).isEqualTo("This is a long title");
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

    assertThat(literature.documentaryTitle()).isEqualTo("Dokumentarischer Titel");
  }

  @Test
  @DisplayName("Extracts and sets authors")
  void extractsAndSetsAuthors() {
    String literatureLdml =
        """
                  <akn:akomaNtoso xmlns:akn="http://docs.oasis-open.org/legaldocml/ns/akn/3.0"
                   xmlns:ris="http://ldml.neuris.de/literature/metadata/">
                   <akn:doc name="offene-struktur">
                     <akn:meta>
                         <akn:identification>
                          <akn:FRBRWork>
                            <akn:FRBRauthor as="#verfasser" href="#mustermann-max-verfasser-1"/>
                            <akn:FRBRauthor as="#verfasser" href="#musterfrau-susanne-verfasser-2"/>
                          </akn:FRBRWork>
                          <akn:FRBRExpression>
                            <akn:FRBRalias name="documentNumber" value="BJLU002758328" />
                           </akn:FRBRExpression>
                         </akn:identification>
                         <akn:references source="attributsemantik-noch-undefiniert">
                              <akn:TLCPerson eId="mustermann-max-verfasser-1" href="" ris:name="Mustermann, Max"/>
                              <akn:TLCPerson eId="musterfrau-susanne-verfasser-2" href="" ris:titel="Prof Dr" ris:name="Musterfrau, Susanne"/>
                              <akn:TLCRole eId="verfasser" href="akn/ontology/roles/de/verfasser" showAs="Verfasser"/>
                            </akn:references>
                     </akn:meta>
                   </akn:doc>
                 </akn:akomaNtoso>
                 """
            .stripIndent();

    Literature literature =
        LiteratureLdmlToOpenSearchMapper.parseLiteratureLdml(literatureLdml).get();

    var authors = literature.authors();
    assertThat(authors).hasSize(2);
    assertThat(authors.getFirst().name()).isEqualTo("Mustermann, Max");
    assertThat(authors.getFirst().title()).isNull();

    assertThat(authors.get(1).name()).isEqualTo("Musterfrau, Susanne");
    assertThat(authors.get(1).title()).isEqualTo("Prof Dr");
  }

  @Test
  @DisplayName("Extracts and sets collaborators")
  void extractsAndSetsCollaborators() {
    String literatureLdml =
        """
                      <akn:akomaNtoso xmlns:akn="http://docs.oasis-open.org/legaldocml/ns/akn/3.0"
                       xmlns:ris="http://ldml.neuris.de/literature/metadata/">
                       <akn:doc name="offene-struktur">
                         <akn:meta>
                             <akn:identification>
                              <akn:FRBRWork>
                                <akn:FRBRauthor as="#mitarbeiter" href="#foo-peter-mitarbeiter-1"/>
                                <akn:FRBRauthor as="#mitarbeiter" href="#bar-janine-mitarbeiter-2"/>
                              </akn:FRBRWork>
                              <akn:FRBRExpression>
                                <akn:FRBRalias name="documentNumber" value="BJLU002758328" />
                               </akn:FRBRExpression>
                             </akn:identification>
                             <akn:references source="attributsemantik-noch-undefiniert">
                                  <akn:TLCPerson eId="foo-peter-mitarbeiter-1" href="" ris:name="Foo, Peter"/>
                                  <akn:TLCPerson eId="bar-janine-mitarbeiter-2" href="" ris:titel="Prof Dr" ris:name="Bar, Janine"/>
                                  <akn:TLCRole eId="mitarbeiter" href="akn/ontology/roles/de/mitarbeiter" showAs="Mitarbeiter"/>
                                </akn:references>
                         </akn:meta>
                       </akn:doc>
                     </akn:akomaNtoso>
                     """
            .stripIndent();

    Literature literature =
        LiteratureLdmlToOpenSearchMapper.parseLiteratureLdml(literatureLdml).get();

    var collaborators = literature.collaborators();
    assertThat(collaborators).hasSize(2);
    assertThat(collaborators.getFirst().name()).isEqualTo("Foo, Peter");
    assertThat(collaborators.getFirst().title()).isNull();

    assertThat(collaborators.get(1).name()).isEqualTo("Bar, Janine");
    assertThat(collaborators.get(1).title()).isEqualTo("Prof Dr");
  }

  @Test
  @DisplayName("Does not set values for missing optional datapoints")
  void doesNotSetValuesForMissingOptionalDatapoints() {
    Literature literature =
        LiteratureLdmlToOpenSearchMapper.parseLiteratureLdml(minimalValidLdml).get();

    assertThat(literature.yearsOfPublication()).isEmpty();
    assertThat(literature.documentTypes()).isEmpty();
    assertThat(literature.yearsOfPublication()).isEmpty();
    assertThat(literature.mainTitle()).isNull();
    assertThat(literature.documentaryTitle()).isNull();
    assertThat(literature.authors()).isEmpty();
    assertThat(literature.collaborators()).isEmpty();
  }
}
