package de.bund.digitalservice.ris.search.unit.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.mapper.literature.LiteratureLdmlToOpenSearchMapper;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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
  @DisplayName("Sets document number as Id")
  void setsDocumentNumberAsId() {
    Literature literature =
        LiteratureLdmlToOpenSearchMapper.parseLiteratureLdml(minimalValidLdml).get();

    assertThat(literature.id()).isEqualTo("BJLU002758328");
  }

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
  @DisplayName("Extracts and sets dependent reference")
  void extractsDependentReference() {
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
                          <akn:analysis source="attributsemantik-noch-undefiniert">
                            <akn:otherReferences source="attributsemantik-noch-undefiniert">
                              <akn:implicitReference showAs="">
                                <ris:fundstelleUnselbstaendig periodikum="RdA" zitatstelle="1982, 122"/>
                              </akn:implicitReference>
                            </akn:otherReferences>
                          </akn:analysis>
                       </akn:doc>
                     </akn:akomaNtoso>
                     """
            .stripIndent();

    Literature literature =
        LiteratureLdmlToOpenSearchMapper.parseLiteratureLdml(literatureLdml).get();

    var dependentReferences = literature.dependentReferences();
    assertThat(dependentReferences).hasSize(1).containsExactly("RdA 1982, 122");
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "<ris:fundstelleUnselbstaendig zitatstelle=\"1982, 122\"/>",
        "<ris:fundstelleUnselbstaendig periodikum=\"RdA\"/>"
      })
  @DisplayName("Does not create literature object if dependent reference has missing attributes")
  void doesNotCreateLiteratureObjectIfDependentReferenceHasMissingAttributes(String fundstelle) {
    String literatureLdml =
        String.format(
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
                          <akn:analysis source="attributsemantik-noch-undefiniert">
                            <akn:otherReferences source="attributsemantik-noch-undefiniert">
                              <akn:implicitReference showAs="">
                                %s
                              </akn:implicitReference>
                            </akn:otherReferences>
                          </akn:analysis>
                       </akn:doc>
                     </akn:akomaNtoso>
                     """,
                fundstelle)
            .stripIndent();

    assertThat(LiteratureLdmlToOpenSearchMapper.parseLiteratureLdml(literatureLdml)).isEmpty();
  }

  @Test
  @DisplayName("Extracts and sets independent reference")
  void extractsIndependentReference() {
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
                              <akn:analysis source="attributsemantik-noch-undefiniert">
                                <akn:otherReferences source="attributsemantik-noch-undefiniert">
                                  <akn:implicitReference showAs="">
                                    <ris:fundstelleSelbstaendig titel="Foo" zitatstelle="1982, 122"/>
                                  </akn:implicitReference>
                                </akn:otherReferences>
                              </akn:analysis>
                           </akn:doc>
                         </akn:akomaNtoso>
                         """
            .stripIndent();

    Literature literature =
        LiteratureLdmlToOpenSearchMapper.parseLiteratureLdml(literatureLdml).get();

    var independentReferences = literature.independentReferences();
    assertThat(independentReferences).hasSize(1).containsExactly("Foo 1982, 122");
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "<ris:fundstelleSelbstaendig zitatstelle=\"1982, 122\"/>",
        "<ris:fundstelleSelbstaendig titel=\"Foo\"/>"
      })
  @DisplayName("Does not create literature object if independent reference has missing attributes")
  void doesNotCreateLiteratureObjectIfIndependentReferenceHasMissingAttributes(String fundstelle) {
    String literatureLdml =
        String.format(
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
                                      <akn:analysis source="attributsemantik-noch-undefiniert">
                                        <akn:otherReferences source="attributsemantik-noch-undefiniert">
                                          <akn:implicitReference showAs="">
                                            %s
                                          </akn:implicitReference>
                                        </akn:otherReferences>
                                      </akn:analysis>
                                   </akn:doc>
                                 </akn:akomaNtoso>
                                 """,
                fundstelle)
            .stripIndent();

    assertThat(LiteratureLdmlToOpenSearchMapper.parseLiteratureLdml(literatureLdml)).isEmpty();
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
    assertThat(authors).hasSize(2).containsExactly("Mustermann, Max", "Musterfrau, Susanne");
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
    assertThat(collaborators).hasSize(2).containsExactly("Foo, Peter", "Bar, Janine");
  }

  @Test
  @DisplayName("Extracts, cleans and sets short report")
  void extractsAndSetsShortReport() {
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
                      <akn:mainBody>
                        <akn:div>
                          <akn:p>
                            A <akn:a href="http://www.foo.de" shape="rect">foo</akn:a> is <akn:span>bar</akn:span>.
                            <akn:br/>
                            Bar <akn:sub>baz</akn:sub> or <akn:sup>bas</akn:sup>.
                          </akn:p>
                          <akn:p>
                            <akn:inline name="em">EM</akn:inline>
                            <akn:inline name="hlj">hlj</akn:inline>
                            <akn:inline name="noindex">noindex</akn:inline>
                            <akn:inline name="strong">strong</akn:inline>
                          </akn:p>
                        </akn:div>
                      </akn:mainBody>
                   </akn:doc>
                 </akn:akomaNtoso>
                 """
            .stripIndent();

    Literature literature =
        LiteratureLdmlToOpenSearchMapper.parseLiteratureLdml(literatureLdml).get();

    assertThat(literature.shortReport())
        .isEqualTo("A foo is bar. Bar baz or bas. EM hlj noindex strong");
  }

  @Test
  @DisplayName("Sets null if short report is empty hcontainer")
  void setsNullIfShortReportIsMissing() {
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
                      <akn:mainBody>
                        <akn:hcontainer name="crossheading"/>
                      </akn:mainBody>
                   </akn:doc>
                 </akn:akomaNtoso>
                 """
            .stripIndent();

    Literature literature =
        LiteratureLdmlToOpenSearchMapper.parseLiteratureLdml(literatureLdml).get();

    assertThat(literature.shortReport()).isNull();
  }

  @Test
  @DisplayName("Does not set values for missing optional datapoints")
  void doesNotSetValuesForMissingOptionalDatapoints() {
    Literature literature =
        LiteratureLdmlToOpenSearchMapper.parseLiteratureLdml(minimalValidLdml).get();

    assertThat(literature.yearsOfPublication()).isEmpty();
    assertThat(literature.documentTypes()).isEmpty();
    assertThat(literature.dependentReferences()).isEmpty();
    assertThat(literature.independentReferences()).isEmpty();
    assertThat(literature.yearsOfPublication()).isEmpty();
    assertThat(literature.mainTitle()).isNull();
    assertThat(literature.documentaryTitle()).isNull();
    assertThat(literature.authors()).isEmpty();
    assertThat(literature.collaborators()).isEmpty();
    assertThat(literature.shortReport()).isNull();
  }
}
