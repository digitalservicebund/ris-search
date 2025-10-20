package de.bund.digitalservice.ris.search.unit.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.mapper.LiteratureLdmlToOpenSearchMapper;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

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
    Literature literature = LiteratureLdmlToOpenSearchMapper.mapLdml(minimalValidLdml).get();

    assertThat(literature.id()).isEqualTo("BJLU002758328");
  }

  @Test
  @DisplayName("Extracts and sets document number")
  void extractsAndSetsDocumentNumber() {
    Literature literature = LiteratureLdmlToOpenSearchMapper.mapLdml(minimalValidLdml).get();

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
    Optional<Literature> literature = LiteratureLdmlToOpenSearchMapper.mapLdml(literatureLdml);

    assertThat(literature).isEmpty();
  }

  @Test
  @DisplayName("Extracts and sets years of publication and first publication date")
  void extractsAndSetsYearsOfPublication() {
    String literatureLdml =
        """
            <akn:akomaNtoso xmlns:akn="http://docs.oasis-open.org/legaldocml/ns/akn/3.0"
             xmlns:ris="http://ldml.neuris.de/literature/unselbstaendig/metadata/">
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
    Literature literature = LiteratureLdmlToOpenSearchMapper.mapLdml(literatureLdml).get();

    assertThat(literature.yearsOfPublication()).containsExactly("2009");
    assertThat(literature.firstPublicationDate()).isEqualTo(LocalDate.of(2009, 1, 1));
  }

  @Test
  @DisplayName("Sets first publication date to default min date if years of publication missing")
  void setFirstPublicationDateToDefaultMinDate() {
    Literature literature = LiteratureLdmlToOpenSearchMapper.mapLdml(minimalValidLdml).get();

    assertThat(literature.yearsOfPublication()).isEmpty();
    assertThat(literature.firstPublicationDate()).isEqualTo(LocalDate.MIN);
  }

  @Test
  @DisplayName("Extracts and sets document types")
  void extractsAndSetsDocumentTypes() {
    String literatureLdml =
        """
            <akn:akomaNtoso xmlns:akn="http://docs.oasis-open.org/legaldocml/ns/akn/3.0"
             xmlns:ris="http://ldml.neuris.de/literature/unselbstaendig/metadata/">
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
    Literature literature = LiteratureLdmlToOpenSearchMapper.mapLdml(literatureLdml).get();

    assertThat(literature.documentTypes()).containsExactly("Auf");
  }

  @Test
  @DisplayName("Extracts and sets dependent reference")
  void extractsDependentReference() {
    String literatureLdml =
        """
                 <akn:akomaNtoso xmlns:akn="http://docs.oasis-open.org/legaldocml/ns/akn/3.0"
                   xmlns:ris="http://ldml.neuris.de/literature/unselbstaendig/metadata/">
                     <akn:doc name="offene-struktur">
                       <akn:meta>
                           <akn:identification>
                             <akn:FRBRExpression>
                               <akn:FRBRalias name="documentNumber" value="BJLU002758328" />
                             </akn:FRBRExpression>
                           </akn:identification>
                          <akn:analysis source="attributsemantik-noch-undefiniert">
                            <akn:otherReferences source="attributsemantik-noch-undefiniert">
                              <akn:implicitReference showAs="2024, 123">
                                <ris:fundstelleUnselbstaendig zitatstelle="2024, 123"/>
                              </akn:implicitReference>
                              <akn:implicitReference showAs="RdA 1982, 122">
                                <ris:fundstelleUnselbstaendig periodikum="RdA" zitatstelle="1982, 122"/>
                              </akn:implicitReference>
                            </akn:otherReferences>
                          </akn:analysis>
                       </akn:meta>
                     </akn:doc>
                 </akn:akomaNtoso>
                 """
            .stripIndent();

    Literature literature = LiteratureLdmlToOpenSearchMapper.mapLdml(literatureLdml).get();

    var dependentReferences = literature.dependentReferences();
    assertThat(dependentReferences).hasSize(2).containsExactly("2024, 123", "RdA 1982, 122");
  }

  @Test
  @DisplayName("Extracts and sets independent reference")
  void extractsIndependentReference() {
    String literatureLdml =
        """
                  <akn:akomaNtoso xmlns:akn="http://docs.oasis-open.org/legaldocml/ns/akn/3.0"
                  xmlns:ris="http://ldml.neuris.de/literature/unselbstaendig/metadata/">
                   <akn:doc name="offene-struktur">
                     <akn:meta>
                         <akn:identification>
                           <akn:FRBRExpression>
                             <akn:FRBRalias name="documentNumber" value="BJLU002758328" />
                           </akn:FRBRExpression>
                         </akn:identification>
                        <akn:analysis source="attributsemantik-noch-undefiniert">
                          <akn:otherReferences source="attributsemantik-noch-undefiniert">
                            <akn:implicitReference showAs="2023, 432">
                              <ris:fundstelleSelbstaendig zitatstelle="2023, 432"/>
                            </akn:implicitReference>
                            <akn:implicitReference showAs="Foo 1982, 122">
                              <ris:fundstelleSelbstaendig titel="Foo" zitatstelle="1982, 122"/>
                            </akn:implicitReference>
                          </akn:otherReferences>
                        </akn:analysis>
                     </akn:meta>
                   </akn:doc>
                 </akn:akomaNtoso>
                 """
            .stripIndent();

    Literature literature = LiteratureLdmlToOpenSearchMapper.mapLdml(literatureLdml).get();

    var independentReferences = literature.independentReferences();
    assertThat(independentReferences).hasSize(2).containsExactly("2023, 432", "Foo 1982, 122");
  }

  @Test
  @DisplayName("Extracts and sets main title")
  void extractsAndSetsMainTitle() {
    String literatureLdml =
        """
              <akn:akomaNtoso xmlns:akn="http://docs.oasis-open.org/legaldocml/ns/akn/3.0"
               xmlns:ris="http://ldml.neuris.de/literature/unselbstaendig/metadata/">
               <akn:doc name="offene-struktur">
                 <akn:meta>
                     <akn:identification>
                       <akn:FRBRWork>
                          <akn:FRBRalias name="haupttitel" value="This is a long title"/>
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

    Literature literature = LiteratureLdmlToOpenSearchMapper.mapLdml(literatureLdml).get();

    assertThat(literature.mainTitle()).isEqualTo("This is a long title");
  }

  @Test
  @DisplayName("Extracts and sets main title additions")
  void extractsAndSetsMainTitleAdditions() {
    String literatureLdml =
        """
                  <akn:akomaNtoso xmlns:akn="http://docs.oasis-open.org/legaldocml/ns/akn/3.0"
                   xmlns:ris="http://ldml.neuris.de/literature/unselbstaendig/metadata/">
                   <akn:doc name="offene-struktur">
                     <akn:meta>
                         <akn:identification>
                           <akn:FRBRWork>
                              <akn:FRBRalias name="haupttitel" value="This is a long title"/>
                              <akn:FRBRalias name="hauptsachtitelZusatz" value="Some additional infos"/>
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

    Literature literature = LiteratureLdmlToOpenSearchMapper.mapLdml(literatureLdml).get();

    assertThat(literature.mainTitleAdditions()).isEqualTo("Some additional infos");
  }

  @Test
  @DisplayName("Extracts and sets documentary title")
  void extractsAndSetsDocumentaryTitle() {
    String literatureLdml =
        """
              <akn:akomaNtoso xmlns:akn="http://docs.oasis-open.org/legaldocml/ns/akn/3.0"
               xmlns:ris="http://ldml.neuris.de/literature/unselbstaendig/metadata/">
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

    Literature literature = LiteratureLdmlToOpenSearchMapper.mapLdml(literatureLdml).get();

    assertThat(literature.documentaryTitle()).isEqualTo("Dokumentarischer Titel");
  }

  @Test
  @DisplayName("Extracts and sets authors")
  void extractsAndSetsAuthors() {
    String literatureLdml =
        """
                  <akn:akomaNtoso xmlns:akn="http://docs.oasis-open.org/legaldocml/ns/akn/3.0"
                   xmlns:ris="http://ldml.neuris.de/literature/unselbstaendig/metadata/">
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

    Literature literature = LiteratureLdmlToOpenSearchMapper.mapLdml(literatureLdml).get();

    var authors = literature.authors();
    assertThat(authors).hasSize(2).containsExactly("Mustermann, Max", "Musterfrau, Susanne");
  }

  @Test
  @DisplayName("Extracts and sets collaborators")
  void extractsAndSetsCollaborators() {
    String literatureLdml =
        """
                      <akn:akomaNtoso xmlns:akn="http://docs.oasis-open.org/legaldocml/ns/akn/3.0"
                       xmlns:ris="http://ldml.neuris.de/literature/unselbstaendig/metadata/">
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

    Literature literature = LiteratureLdmlToOpenSearchMapper.mapLdml(literatureLdml).get();

    var collaborators = literature.collaborators();
    assertThat(collaborators).hasSize(2).containsExactly("Foo, Peter", "Bar, Janine");
  }

  @Test
  @DisplayName("Extracts and sets originators")
  void extractsAndSetsOriginators() {
    String literatureLdml =
        """
            <akn:akomaNtoso xmlns:akn="http://docs.oasis-open.org/legaldocml/ns/akn/3.0"
             xmlns:ris="http://ldml.neuris.de/literature/unselbstaendig/metadata/">
             <akn:doc name="offene-struktur">
               <akn:meta>
                   <akn:identification>
                    <akn:FRBRWork>
                      <akn:FRBRauthor as="#urheber" href="#dgb-urheber-1"/>
                      <akn:FRBRauthor as="#urheber" href="#foo-urheber-2"/>
                    </akn:FRBRWork>
                    <akn:FRBRExpression>
                      <akn:FRBRalias name="documentNumber" value="BJLU002758328" />
                     </akn:FRBRExpression>
                   </akn:identification>
                   <akn:references source="attributsemantik-noch-undefiniert">
                        <akn:TLCOrganization eId="dgb-urheber-1" href="akn/ontology/organizations/de/dgb" ris:name="DGB" ris:zusatz="Bundesausschuß" showAs="DGB, Bundesauschuß"/>
                        <akn:TLCOrganization eId="foo-urheber-2" href="akn/ontology/organizations/de/foo" ris:name="FOO" ris:zusatz="Foofaafee" showAs="FOO, Foofaafee"/>
                        <akn:TLCRole eId="urheber" href="akn/ontology/roles/de/urheber" showAs="Urheber"/>
                      </akn:references>
                 </akn:meta>
               </akn:doc>
             </akn:akomaNtoso>
             """
            .stripIndent();

    Literature literature = LiteratureLdmlToOpenSearchMapper.mapLdml(literatureLdml).get();

    var originators = literature.originators();
    assertThat(originators).hasSize(2).containsExactly("DGB", "FOO");
  }

  @Test
  @DisplayName("Extracts and sets conference notes")
  void extractsAndSetsConferenceNotes() {
    String literatureLdml =
        """
            <akn:akomaNtoso xmlns:akn="http://docs.oasis-open.org/legaldocml/ns/akn/3.0"
             xmlns:ris="http://ldml.neuris.de/literature/unselbstaendig/metadata/">
             <akn:doc name="offene-struktur">
               <akn:meta>
                   <akn:identification>
                    <akn:FRBRWork>
                      <akn:FRBRauthor as="#kongress" href="#foo-kongress-1"/>
                      <akn:FRBRauthor as="#kongress" href="#bar-kongress-2"/>
                    </akn:FRBRWork>
                    <akn:FRBRExpression>
                      <akn:FRBRalias name="documentNumber" value="BJLU002758328" />
                     </akn:FRBRExpression>
                   </akn:identification>
                   <akn:references source="attributsemantik-noch-undefiniert">
                      <akn:TLCEvent eId="foo-kongress-1" href="akn/ontology/organizations/de/kongress"
                          ris:name="Internationaler Kongreß für das Recht"
                          ris:jahr="1991"
                          ris:ort="Athen"
                          ris:land="GRC"
                          showAs="Internationaler Kongreß für das Recht, 1991, Athen, GRC"
                      />
                      <akn:TLCEvent eId="bar-kongress-2" href="akn/ontology/organizations/de/kongress"
                          ris:name="Kongreß für Bar"
                          ris:jahr="2024"
                          ris:ort="Berlin"
                          ris:land="GER"
                          showAs="Kongreß für Bar, 2024, Berlin, GER"
                      />
                      <akn:TLCRole eId="kongress" href="akn/ontology/roles/de/kongress" showAs="Kongress"/>
                      </akn:references>
                 </akn:meta>
               </akn:doc>
             </akn:akomaNtoso>
             """
            .stripIndent();

    Literature literature = LiteratureLdmlToOpenSearchMapper.mapLdml(literatureLdml).get();

    var conferenceNotes = literature.conferenceNotes();
    assertThat(conferenceNotes)
        .hasSize(2)
        .containsExactly(
            "Internationaler Kongreß für das Recht, 1991, Athen, GRC",
            "Kongreß für Bar, 2024, Berlin, GER");
  }

  @Test
  @DisplayName("Extracts and sets languages")
  void extractsAndSetsLanguages() {
    String literatureLdml =
        """
            <akn:akomaNtoso xmlns:akn="http://docs.oasis-open.org/legaldocml/ns/akn/3.0">
              <akn:doc name="offene-struktur">
                <akn:meta>
                  <akn:identification>
                    <akn:FRBRExpression>
                      <akn:FRBRalias name="documentNumber" value="BJLU002758328" />
                      <akn:FRBRlanguage language="deu"/>
                      <akn:FRBRlanguage language="eng"/>
                    </akn:FRBRExpression>
                  </akn:identification>
                </akn:meta>
              </akn:doc>
            </akn:akomaNtoso>
            """
            .stripIndent();

    Literature literature = LiteratureLdmlToOpenSearchMapper.mapLdml(literatureLdml).get();

    var languages = literature.languages();
    assertThat(languages).hasSize(2).containsExactly("deu", "eng");
  }

  @Test
  @DisplayName("Extracts and sets norm reference")
  void extractsAndSetsNormReference() {
    String literatureLdml =
        """
                             <akn:akomaNtoso xmlns:akn="http://docs.oasis-open.org/legaldocml/ns/akn/3.0"
                               xmlns:ris="http://ldml.neuris.de/literature/unselbstaendig/metadata/">
                                 <akn:doc name="offene-struktur">
                                   <akn:meta>
                                       <akn:identification>
                                         <akn:FRBRExpression>
                                           <akn:FRBRalias name="documentNumber" value="BJLU002758328" />
                                         </akn:FRBRExpression>
                                       </akn:identification>
                                      <akn:analysis source="attributsemantik-noch-undefiniert">
                                        <akn:otherReferences source="attributsemantik-noch-undefiniert">
                                           <akn:implicitReference showAs="BMV-Ä">
                                             <ris:normReference abbreviation="BMV-Ä" />
                                           </akn:implicitReference>
                                           <akn:implicitReference showAs="GG, Art 6 Abs 2 S 1, 1949-05-23">
                                             <ris:normReference abbreviation="GG" dateOfVersion="1949-05-23" singleNorm="Art 6 Abs 2 S 1"/>
                                           </akn:implicitReference>
                                        </akn:otherReferences>
                                      </akn:analysis>
                                   </akn:meta>
                                 </akn:doc>
                             </akn:akomaNtoso>
                             """
            .stripIndent();

    Literature literature = LiteratureLdmlToOpenSearchMapper.mapLdml(literatureLdml).get();

    var normReferences = literature.normReferences();
    assertThat(normReferences)
        .hasSize(2)
        .containsExactly("BMV-Ä", "GG, Art 6 Abs 2 S 1, 1949-05-23");
  }

  @Test
  @DisplayName("Extracts, cleans and sets short report")
  void extractsAndSetsShortReport() {
    String literatureLdml =
        """
                  <akn:akomaNtoso xmlns:akn="http://docs.oasis-open.org/legaldocml/ns/akn/3.0"
                   xmlns:ris="http://ldml.neuris.de/literature/unselbstaendig/metadata/">
                   <akn:doc name="offene-struktur">
                     <akn:meta>
                         <akn:identification>
                           <akn:FRBRExpression>
                             <akn:FRBRalias name="documentNumber" value="BJLU002758328" />
                           </akn:FRBRExpression>
                         </akn:identification>
                     </akn:meta>
                      <akn:mainBody>
                        Foo.
                        <akn:div>
                          <akn:p>
                            A <akn:a href="http://www.foo.de" shape="rect">foo</akn:a> is <akn:span>bar</akn:span>.
                            <akn:br/>
                            Bar <akn:sub>baz</akn:sub> or <akn:sup>bas</akn:sup>.
                          </akn:p>
                          Bar.
                          <akn:p>
                             1. <akn:inline name="em">EM</akn:inline>
                             2. <akn:inline name="hlj">hlj</akn:inline>
                             3. <akn:inline name="noindex">noindex</akn:inline>
                             4. <akn:inline name="strong">strong</akn:inline>
                          </akn:p>
                        </akn:div>
                        Baz.
                      </akn:mainBody>
                   </akn:doc>
                 </akn:akomaNtoso>
                \s"""
            .stripIndent();

    Literature literature = LiteratureLdmlToOpenSearchMapper.mapLdml(literatureLdml).get();

    assertThat(literature.shortReport())
        .isEqualTo(
            "Foo. A foo is bar. Bar baz or bas. Bar. 1. EM 2. hlj 3. noindex 4. strong Baz.");
  }

  @Test
  @DisplayName("Sets null if short report is empty hcontainer")
  void setsNullIfShortReportIsMissing() {
    String literatureLdml =
        """
                  <akn:akomaNtoso xmlns:akn="http://docs.oasis-open.org/legaldocml/ns/akn/3.0"
                   xmlns:ris="http://ldml.neuris.de/literature/unselbstaendig/metadata/">
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

    Literature literature = LiteratureLdmlToOpenSearchMapper.mapLdml(literatureLdml).get();

    assertThat(literature.shortReport()).isNull();
  }

  @Test
  @DisplayName("Extracts, cleans and sets outline")
  void extractsAndSetsOutline() {
    String literatureLdml =
        """
                     <akn:akomaNtoso xmlns:akn="http://docs.oasis-open.org/legaldocml/ns/akn/3.0"
                      xmlns:ris="http://ldml.neuris.de/literature/unselbstaendig/metadata/">
                      <akn:doc name="offene-struktur">
                        <akn:meta>
                            <akn:identification>
                              <akn:FRBRExpression>
                                <akn:FRBRalias name="documentNumber" value="BJLU002758328" />
                              </akn:FRBRExpression>
                            </akn:identification>
                            <akn:proprietary source="attributsemantik-noch-undefiniert">
                              <ris:metadata>
                                <ris:gliederung>
                                    <ris:gliederungEntry>I. Foo.</ris:gliederungEntry>
                                    <ris:gliederungEntry>II. Bar.</ris:gliederungEntry>
                                    <ris:gliederungEntry>III. Baz.</ris:gliederungEntry>
                                </ris:gliederung>
                              </ris:metadata>
                            </akn:proprietary>
                        </akn:meta>
                      </akn:doc>
                    </akn:akomaNtoso>
                    """
            .stripIndent();

    Literature literature = LiteratureLdmlToOpenSearchMapper.mapLdml(literatureLdml).get();

    assertThat(literature.outline()).isEqualTo("I. Foo. II. Bar. III. Baz.");
  }

  @Test
  @DisplayName("Sets indexedAt to current time")
  void setsIndexedAtToCurrentTime() {
    Instant fixedInstant = Instant.parse("2025-01-01T10:00:00Z");

    try (MockedStatic<Instant> mockedInstant = Mockito.mockStatic(Instant.class)) {
      mockedInstant.when(Instant::now).thenReturn(fixedInstant);

      Literature literature = LiteratureLdmlToOpenSearchMapper.mapLdml(minimalValidLdml).get();
      assertThat(literature.indexedAt()).isEqualTo(fixedInstant.toString());
    }
  }

  @Test
  @DisplayName("Does not set values for missing optional datapoints")
  void doesNotSetValuesForMissingOptionalDatapoints() {
    Literature literature = LiteratureLdmlToOpenSearchMapper.mapLdml(minimalValidLdml).get();

    assertThat(literature.yearsOfPublication()).isEmpty();
    assertThat(literature.documentTypes()).isEmpty();
    assertThat(literature.dependentReferences()).isEmpty();
    assertThat(literature.independentReferences()).isEmpty();
    assertThat(literature.yearsOfPublication()).isEmpty();
    assertThat(literature.mainTitle()).isNull();
    assertThat(literature.mainTitleAdditions()).isNull();
    assertThat(literature.documentaryTitle()).isNull();
    assertThat(literature.authors()).isEmpty();
    assertThat(literature.collaborators()).isEmpty();
    assertThat(literature.originators()).isEmpty();
    assertThat(literature.conferenceNotes()).isEmpty();
    assertThat(literature.languages()).isEmpty();
    assertThat(literature.normReferences()).isEmpty();
    assertThat(literature.shortReport()).isNull();
    assertThat(literature.outline()).isNull();
  }
}
