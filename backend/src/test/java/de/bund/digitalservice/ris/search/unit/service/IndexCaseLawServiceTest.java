package de.bund.digitalservice.ris.search.unit.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.search.exception.RetryableObjectStoreException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.opensearch.CaseLawSynthesizedRepository;
import de.bund.digitalservice.ris.search.service.IndexCaselawService;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IndexCaseLawServiceTest {

  IndexCaselawService service;

  @Mock CaseLawBucket bucket;
  @Mock CaseLawSynthesizedRepository repo;

  String caseLawContent =
      """
          <?xml version="1.0" encoding="utf-8"?>
          <akn:akomaNtoso xmlns:akn="http://docs.oasis-open.org/legaldocml/ns/akn/3.0/WD17"
                          xmlns:ris="http://example.com/0.1/"
                          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                          xsi:schemaLocation="http://docs.oasis-open.org/legaldocml/ns/akn/3.0/WD17 https://docs.oasis-open.org/legaldocml/akn-core/v1.0/csprd02/part2-specs/schemas/akomantoso30.xsd">
             <akn:judgment name="attributsemantik-noch-undefiniert">
                <akn:meta>
                   <akn:identification source="attributsemantik-noch-undefiniert">
                      <akn:FRBRWork>
                         <akn:FRBRthis value="TEST80020093"/>
                         <akn:FRBRuri value="TEST80020093"/>
                         <akn:FRBRalias name="uebergreifende-id" value="19c1545d-4c41-4765-be23-dc71ec0d0a90"/>
                         <akn:FRBRdate date="2008-09-04" name="entscheidungsdatum"/>
                         <akn:FRBRauthor href="attributsemantik-noch-undefiniert"/>
                         <akn:FRBRcountry value="de"/>
                      </akn:FRBRWork>
                      <akn:FRBRExpression>
                         <akn:FRBRthis value="TEST80020093/dokument"/>
                         <akn:FRBRuri value="TEST80020093/dokument"/>
                         <akn:FRBRdate date="2008-09-04" name="entscheidungsdatum"/>
                         <akn:FRBRauthor href="attributsemantik-noch-undefiniert"/>
                         <akn:FRBRlanguage language="deu"/>
                      </akn:FRBRExpression>
                      <akn:FRBRManifestation>
                         <akn:FRBRthis value="TEST80020093/dokument.xml"/>
                         <akn:FRBRuri value="TEST80020093/dokument.xml"/>
                         <akn:FRBRdate date="2008-09-04" name="entscheidungsdatum"/>
                         <akn:FRBRauthor href="attributsemantik-noch-undefiniert"/>
                      </akn:FRBRManifestation>
                   </akn:identification>
                   <akn:classification source="attributsemantik-noch-undefiniert">
                      <akn:keyword dictionary="attributsemantik-noch-undefiniert"
                                   showAs="attributsemantik-noch-undefiniert"
                                   value="Beschwerdeverfahren"/>
                      <akn:keyword dictionary="attributsemantik-noch-undefiniert"
                                   showAs="attributsemantik-noch-undefiniert"
                                   value="bedürftige Partei"/>
                      <akn:keyword dictionary="attributsemantik-noch-undefiniert"
                                   showAs="attributsemantik-noch-undefiniert"
                                   value="wesentliche Änderung"/>
                      <akn:keyword dictionary="attributsemantik-noch-undefiniert"
                                   showAs="attributsemantik-noch-undefiniert"
                                   value="Änderung"/>
                      <akn:keyword dictionary="attributsemantik-noch-undefiniert"
                                   showAs="attributsemantik-noch-undefiniert"
                                   value="Erklärungspflicht"/>
                      <akn:keyword dictionary="attributsemantik-noch-undefiniert"
                                   showAs="attributsemantik-noch-undefiniert"
                                   value="Prozesskostenhilfe"/>
                      <akn:keyword dictionary="attributsemantik-noch-undefiniert"
                                   showAs="attributsemantik-noch-undefiniert"
                                   value="Aufhebung"/>
                      <akn:keyword dictionary="attributsemantik-noch-undefiniert"
                                   showAs="attributsemantik-noch-undefiniert"
                                   value="Erklärung"/>
                      <akn:keyword dictionary="attributsemantik-noch-undefiniert"
                                   showAs="attributsemantik-noch-undefiniert"
                                   value="Gehaltsabrechnung"/>
                      <akn:keyword dictionary="attributsemantik-noch-undefiniert"
                                   showAs="attributsemantik-noch-undefiniert"
                                   value="persönliche und wirtschaftliche Verhältnisse"/>
                   </akn:classification>
                   <akn:proprietary source="attributsemantik-noch-undefiniert">
                      <ris:meta>
                         <ris:previousDecisions>
                            <ris:previousDecision date="2008-04-08">
                               <ris:fileNumber>8 Ca 1758/05</ris:fileNumber>
                               <ris:courtType>ArbG</ris:courtType>
                            </ris:previousDecision>
                         </ris:previousDecisions>
                         <ris:fileNumbers>
                            <ris:fileNumber>3 Ta 156/08</ris:fileNumber>
                         </ris:fileNumbers>
                         <ris:documentType>Beschluss</ris:documentType>
                         <ris:courtLocation>Mainz</ris:courtLocation>
                         <ris:courtType>LArbG</ris:courtType>
                         <ris:legalEffect>JA</ris:legalEffect>
                         <ris:fieldOfLaws>
                            <ris:fieldOfLaw>Arbeitsrecht</ris:fieldOfLaw>
                            <ris:fieldOfLaw>Aufhebung der Bewilligung</ris:fieldOfLaw>
                            <ris:fieldOfLaw>Bewilligung, Wirkungen, Beiordnung eines Rechtsanwalts</ris:fieldOfLaw>
                            <ris:fieldOfLaw>Bewilligungsvoraussetzungen</ris:fieldOfLaw>
                         </ris:fieldOfLaws>
                         <ris:judicialBody>3. Kammer</ris:judicialBody>
                         <ris:publicationStatus>PUBLISHED</ris:publicationStatus>
                         <ris:error>false</ris:error>
                         <ris:documentationOffice>BAG</ris:documentationOffice>
                         <ris:procedures>
                            <ris:procedure>SELECT081105</ris:procedure>
                         </ris:procedures>
                      </ris:meta>
                   </akn:proprietary>
                </akn:meta>
                <akn:header>
                   <akn:p>(Testheader)</akn:p>
                </akn:header>
                <akn:judgmentBody>
                   <akn:introduction>
                      <akn:block name="Orientierungssatz">
                         <akn:embeddedStructure>
                            <akn:p>An die Erfüllung der Erklärungspflicht im Rahmen des § 120 Abs. 4 S. 2 ZPO (- ist eine Änderung der Verhältnisse eingetreten? -) dürfen keine zu strengen Anforderungen gestellt werden.</akn:p>
                            <akn:p>
                      Die (bedürftige) kann Partei die erforderliche Erklärung auch noch im Beschwerdeverfahren abgeben bzw. eine bereits abgegebene Erklärung ergänzen und belegen.
                      <akn:a class="border-number-link" href="#border-number-link-7">7</akn:a>
                            </akn:p>
                         </akn:embeddedStructure>
                      </akn:block>
                      <akn:block name="Tenor">
                         <akn:embeddedStructure>
                            <akn:p>1. Tenor</akn:p>
                            <akn:p>2.Tenor</akn:p>
                         </akn:embeddedStructure>
                      </akn:block>
                   </akn:introduction>
                   <akn:decision>
                      <akn:block name="Gründe">
                         <akn:embeddedStructure>
                            <akn:p>
                               <akn:b>I.</akn:b>
                            </akn:p>
                            <akn:hcontainer name="randnummer">
                               <akn:num>1</akn:num>
                               <akn:content>
                                  <akn:p>Content 1</akn:p>
                               </akn:content>
                            </akn:hcontainer>
                            <akn:hcontainer name="randnummer">
                               <akn:num>2</akn:num>
                               <akn:content>
                                  <akn:p>Content 2</akn:p>
                               </akn:content>
                            </akn:hcontainer>
                         </akn:embeddedStructure>
                      </akn:block>
                   </akn:decision>
                </akn:judgmentBody>
             </akn:judgment>
          </akn:akomaNtoso>
          """;

  @BeforeEach()
  void setup() {
    this.service = new IndexCaselawService(bucket, repo);
  }

  @Test
  void reindexAllIgnoreseInvalidFiles() throws RetryableObjectStoreException {
    String testContent = caseLawContent;

    when(this.bucket.getAllFilenames()).thenReturn(List.of("file1", "file2"));
    when(this.bucket.getFileAsString("file1")).thenReturn(Optional.of(testContent));
    when(this.bucket.getFileAsString("file2")).thenReturn(Optional.of("this will not parse"));

    String startingTimestamp =
        ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
    this.service.reindexAll(startingTimestamp);

    verify(repo, times(1))
        .save(
            argThat(
                arg -> {
                  assertThat(arg.id()).isEqualTo("TEST80020093");
                  return true;
                }));
    verify(repo, times(1)).deleteByIndexedAtBefore(startingTimestamp);
  }

  @Test
  void itCanReindexFromeOneSpecificChangelog() throws RetryableObjectStoreException {
    String testContent = caseLawContent;

    when(this.bucket.getFileAsString("TEST080020093.xml")).thenReturn(Optional.of(testContent));

    Changelog changelog = new Changelog();
    changelog.setChanged(Sets.newHashSet(List.of("TEST080020093.xml")));
    service.indexChangelog("changelog1", changelog);

    verify(repo, times(1))
        .save(
            argThat(
                arg -> {
                  assertThat(arg.id()).isEqualTo("TEST80020093");
                  return true;
                }));
  }

  @Test
  void itCanDeleteFromOneSpecificChangelog() throws RetryableObjectStoreException {
    Changelog changelog = new Changelog();
    changelog.setDeleted(Sets.newHashSet(Set.of("TEST080020093.xml")));
    service.indexChangelog("changelog1", changelog);

    verify(repo, times(1)).deleteAllById(Set.of("TEST080020093"));
  }

  @Test
  void itReturnsRightNumberOfFiles() {
    when(this.bucket.getAllFilenames())
        .thenReturn(
            List.of(
                "TEST080020093",
                "TEST080020094",
                "changelogs/2025-03-26T14:13:34.096304815Z-caselaw.json"));
    when(this.bucket.getAllFilenamesByPath("changelogs"))
        .thenReturn(List.of("2025-03-26T14:13:34.096304815Z-caselaw.json"));
    assertThat(service.getNumberOfFilesInBucket()).isEqualTo(2);
  }
}
