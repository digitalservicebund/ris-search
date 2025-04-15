package de.bund.digitalservice.ris.search.unit.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.search.exception.RetryableObjectStoreException;
import de.bund.digitalservice.ris.search.mapper.NormLdmlToOpenSearchMapper;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import de.bund.digitalservice.ris.search.repository.opensearch.NormsSynthesizedRepository;
import de.bund.digitalservice.ris.search.service.IndexNormsService;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IndexNormsServiceTest {

  IndexNormsService service;

  @Mock NormsBucket bucket;
  @Mock NormsSynthesizedRepository repo;

  @BeforeEach()
  void setup() {
    this.service = new IndexNormsService(bucket, repo);
  }

  private String testContent =
      """
          <?xml version="1.0" encoding="UTF-8"?>
          <?xml-model href="../../../Grammatiken/legalDocML.de.sch" schematypens="http://purl.oclc.org/dsdl/schematron"?>
          <akn:akomaNtoso xmlns:akn="http://Inhaltsdaten.LegalDocML.de/1.7.2/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="http://Metadaten.LegalDocML.de/1.7.2/ ../../../Grammatiken/legalDocML.de-metadaten.xsd
                                http://Inhaltsdaten.LegalDocML.de/1.7.2/ ../../../Grammatiken/legalDocML.de-regelungstextverkuendungsfassung.xsd">
            <akn:act name="regelungstext">
              <akn:meta eId="meta-1" GUID="40a13276-39ac-4fc9-aafd-3fbfea0df2b0">
                <akn:identification eId="meta-1_ident-1" GUID="6098a736-d359-4caf-a45e-dfa3792f2bc4" source="attributsemantik-noch-undefiniert">
                  <akn:FRBRWork eId="meta-1_ident-1_frbrwork-1" GUID="cc4476ce-6342-4bb9-a777-030a21e4b0fc">
                    <akn:FRBRthis eId="meta-1_ident-1_frbrwork-1_frbrthis-1" GUID="d2bd3a22-c547-479d-85c3-ef374778c74b" value="eli/bund/bgbl-1/1992/s101/regelungstext-1"></akn:FRBRthis>
                    <akn:FRBRuri eId="meta-1_ident-1_frbrwork-1_frbruri-1" GUID="0d015c4c-be37-453a-917f-7975287b2fcf" value="eli/bund/bgbl-1/1992/s101"></akn:FRBRuri>
                    <akn:FRBRalias eId="meta-1_ident-1_frbrwork-1_frbralias-1" GUID="e2acf308-f9e4-4781-ba3a-5339a999f69f" name="übergreifende-id" value="f96cfae4-4fce-4c72-9186-0d84778dc11c"></akn:FRBRalias>
                    <akn:FRBRdate eId="meta-1_ident-1_frbrwork-1_frbrdate-1" GUID="63e8780d-4225-4987-aec1-794510b4d7f6" date="1992-01-01" name="verkuendungsfassung"></akn:FRBRdate>
                    <akn:FRBRauthor eId="meta-1_ident-1_frbrwork-1_frbrauthor-1" GUID="b6deb229-93c7-43f4-886a-b9693587ca8a" href="recht.bund.de/institution/bundesregierung"></akn:FRBRauthor>
                    <akn:FRBRcountry eId="meta-1_ident-1_frbrwork-1_frbrcountry-1" GUID="1fd2ebf9-a423-443c-a7e5-665b86a0d9d9" value="de"></akn:FRBRcountry>
                    <akn:FRBRnumber eId="meta-1_ident-1_frbrwork-1_frbrnumber-1" GUID="e7405de2-48df-4049-9621-a0ffa2d8e317" value="1"></akn:FRBRnumber>
                    <akn:FRBRname eId="meta-1_ident-1_frbrwork-1_frbrname-1" GUID="fc410fa5-4236-4735-87e5-1bb5ab1402ba" value="bgbl-1"></akn:FRBRname>
                    <akn:FRBRsubtype eId="meta-1_ident-1_frbrwork-1_frbrsubtype-1" GUID="994fe962-3694-49ad-a2d1-4e17d12b10d0" value="regelungstext-1"></akn:FRBRsubtype>
                  </akn:FRBRWork>
                  <akn:FRBRExpression eId="meta-1_ident-1_frbrexpression-1" GUID="2a16dd66-9151-40d4-ab04-3f0525b4102d">
                    <akn:FRBRthis eId="meta-1_ident-1_frbrexpression-1_frbrthis-1" GUID="b4d5b0c5-53e3-4c51-b6c6-0311563941df" value="eli/bund/bgbl-1/1992/s101/1992-01-01/1/deu/regelungstext-1"></akn:FRBRthis>
                    <akn:FRBRuri eId="meta-1_ident-1_frbrexpression-1_frbruri-1" GUID="8909d2e8-abde-437a-8efc-f45483b4f996" value="eli/bund/bgbl-1/1992/s101/1992-01-01/1/deu"></akn:FRBRuri>
                    <akn:FRBRalias eId="meta-1_ident-1_frbrexpression-1_frbralias-1" GUID="c23228e0-af6b-46e0-aaae-93520ae758c9" name="vorherige-version-id" value="79c7f606-b47c-4c7d-8e3a-1a89b333cd85"></akn:FRBRalias>
                    <akn:FRBRalias eId="meta-1_ident-1_frbrexpression-1_frbralias-2" GUID="2e74b960-342e-4af1-b127-08bfef86d3ae" name="aktuelle-version-id" value="d33c67a0-2be2-4728-932d-5abae5a84422"></akn:FRBRalias>
                    <akn:FRBRalias eId="meta-1_ident-1_frbrexpression-1_frbralias-3" GUID="a7a64f41-bd12-4311-9ae0-dd23e94fa018" name="nachfolgende-version-id" value="24c51028-eb62-4853-a986-6c62e6e25731"></akn:FRBRalias>
                    <akn:FRBRauthor eId="meta-1_ident-1_frbrexpression-1_frbrauthor-1" GUID="4e8005ef-7e71-478f-bc91-b89e8c69e9e2" href="recht.bund.de/institution/bundesregierung"></akn:FRBRauthor>
                    <akn:FRBRdate eId="meta-1_ident-1_frbrexpression-1_frbrdate-1" GUID="a5bffed4-7bb4-4b74-a326-eaabfea58f94" date="1992-01-01" name="verkuendung"></akn:FRBRdate>
                    <akn:FRBRlanguage eId="meta-1_ident-1_frbrexpression-1_frbrlanguage-1" GUID="e6cfb417-7a91-444d-8563-a0a09c55058c" language="deu"></akn:FRBRlanguage>
                    <akn:FRBRversionNumber eId="meta-1_ident-1_frbrexpression-1_frbrersionnumber-1" GUID="6b354c1d-cc0b-4df0-83ee-c5d062cc2d0a" value="1"></akn:FRBRversionNumber>
                  </akn:FRBRExpression>
                  <akn:FRBRManifestation eId="meta-1_ident-1_frbrmanifestation-1" GUID="a3747035-aff8-41a6-86d7-0c7b75caddf2">
                    <akn:FRBRthis eId="meta-1_ident-1_frbrmanifestation-1_frbrthis-1" GUID="451a75df-0eb0-4644-ba75-04743d44aadc" value="eli/bund/bgbl-1/1992/s101/1992-01-01/1/deu/1992-01-01/regelungstext-1.xml"></akn:FRBRthis>
                    <akn:FRBRuri eId="meta-1_ident-1_frbrmanifestation-1_frbruri-1" GUID="a2c90263-9bd4-48c4-974e-6d36f5a81021" value="eli/bund/bgbl-1/1992/s101/1992-01-01/1/deu/1992-01-01/regelungstext-1.xml"></akn:FRBRuri>
                    <akn:FRBRdate eId="meta-1_ident-1_frbrmanifestation-1_frbrdate-1" GUID="4fd05b86-52c3-4c01-9251-eb8df6e2ab0c" date="1992-01-01" name="generierung"></akn:FRBRdate>
                    <akn:FRBRauthor eId="meta-1_ident-1_frbrmanifestation-1_frbrauthor-1" GUID="54f8256b-97f1-4f73-afc3-088ae56af8c7" href="recht.bund.de"></akn:FRBRauthor>
                    <akn:FRBRformat eId="meta-1_ident-1_frbrmanifestation-1_frbrformat-1" GUID="3cd9946c-1df1-4797-8575-5e94bd3d63c5" value="xml"></akn:FRBRformat>
                  </akn:FRBRManifestation>
                </akn:identification>
                <akn:lifecycle source="attributsemantik-noch-undefiniert" GUID="f67462bc-7787-4ba9-9041-8e4fc8852ae3" eId="meta-1_lebzykl-1">
                  <akn:eventRef date="1992-01-01" source="attributsemantik-noch-undefiniert" refersTo="ausfertigung" type="generation" eId="meta-1_lebzykl-1_ereignis-1" GUID="1b8c849d-ee5c-4568-a119-be9dbbc27e2e"></akn:eventRef>
                </akn:lifecycle>
                <akn:analysis source="attributsemantik-noch-undefiniert" eId="meta-1_analysis-1" GUID="04f6a82d-20b3-4bf9-88bb-e442f58247b5"></akn:analysis>
                <akn:temporalData source="attributsemantik-noch-undefiniert" GUID="c3f9ce52-8e0c-428c-abdd-37e56ed66551" eId="meta-1_geltzeiten-1">
                  <akn:temporalGroup eId="meta-1_geltzeiten-1_geltungszeitgr-1" GUID="5a8e0a7b-97ca-433b-9d26-0c1501d34e7b">
                    <akn:timeInterval start="#meta-1_lebzykl-1_ereignis-1" refersTo="geltungszeit" eId="meta-1_geltzeiten-1_geltungszeitgr-1_gelzeitintervall-1" GUID="90afcb18-ec43-468b-b914-d37d4c60bf76"></akn:timeInterval>
                  </akn:temporalGroup>
                </akn:temporalData>
              </akn:meta>

              <akn:preface eId="einleitung-1" GUID="a3399d47-218a-46cf-939c-685f884ed724">
                <akn:longTitle eId="einleitung-1_doktitel-1" GUID="16586e8f-68e1-4191-a4a2-8473f6bdf4e5">
                  <akn:p eId="einleitung-1_doktitel-1_text-1" GUID="f312be90-0938-4ab8-a18d-e5b97e9be686">
                    <akn:docTitle eId="einleitung-1_doktitel-1_text-1_doctitel-1">Formatting Test Document</akn:docTitle>
                    <akn:shortTitle eId="einleitung-1_doktitel-1_text-1_kurztitel-1">(Formatting Test - <akn:inline refersTo="amtliche-abkuerzung" name="attributsemantik-noch-undefiniert" eId="einleitung-1_doktitel-1_text-1_kurztitel-1_inline-1">MFT</akn:inline>)</akn:shortTitle>
                  </akn:p>
                </akn:longTitle>
                <akn:block eId="einleitung-1_block-1" GUID="50b373c9-d7a9-4ea1-a6c9-bee1fa5617c6" name="attributsemantik-noch-undefiniert">
                  <akn:date eId="einleitung-1_block-1_datum-1" GUID="34a9b790-c90f-4177-8023-425062e13f07" refersTo="ausfertigung-datum" date="0001-01-01">0001-01-01</akn:date>
                </akn:block>
              </akn:preface>

              <akn:body eId="hauptteil-1" GUID="2bd1d0a2-ff72-42af-b9ab-2f30cc8d7539">
                <!-- Basic HTML elements -->
                <akn:article eId="hauptteil-1_para-1" GUID="87cd6b3a-d198-49c3-a02f-6adfd12940cb" period="#meta-1_geltzeiten-1_geltungszeitgr-1">
                  <akn:num eId="hauptteil-1_para-1_bezeichnung-1" GUID="0a0885f1-5bf1-476e-b12b-ce8243a47ddb">
                    <akn:marker eId="hauptteil-1_para-1_bezeichnung-1_zaehlbez-1" GUID="1e35f3b3-ad84-4c5a-9804-092454853b84" name="1"></akn:marker> § 1 </akn:num>
                  <akn:heading eId="hauptteil-1_para-1_überschrift-1" GUID="519116f1-968a-4eaa-a89e-71258285844f"> Basic HTML Elements </akn:heading>

                  <akn:paragraph eId="hauptteil-1_para-1_abs-1" GUID="5eb21b69-8e10-4b24-8d69-14e6688a7886">
                    <akn:num eId="hauptteil-1_para-1_abs-1_bezeichnung-1" GUID="da0afc22-6f9b-4fe6-8e25-a1b97c8fc382">
                      <akn:marker eId="hauptteil-1_para-1_abs-1_bezeichnung-1_zaehlbez-1" GUID="7ad2932c-a416-4dc9-a58d-cc0d6a597d71" name="1"></akn:marker>
                    </akn:num>
                    <akn:content eId="hauptteil-1_para-1_abs-1_inhalt-1" GUID="c07abae2-921b-47a5-9681-3ff0b2700836">
                      <akn:p eId="hauptteil-1_para-1_abs-1_inhalt-1_text-1" GUID="7d7bea1a-182d-42ed-9f44-7844b3bb2d4c">
                        <akn:b eId="hauptteil-1_para-1_abs-1_inhalt-1_text-1_fettschrift-1" GUID="c1c593df-c20b-4d11-9c4e-945c7041fd9b">Bold</akn:b> text. <akn:i eId="hauptteil-1_para-1_abs-1_inhalt-1_text-1_kursiv-1" GUID="726c9ef9-9cad-40ae-8654-cc058366c400">Italic</akn:i> text. <akn:u
                          eId="hauptteil-1_para-1_abs-1_inhalt-1_text-1_u-1" GUID="c30774a1-23b9-453d-a01f-4d873e29fec5">Underlined</akn:u> text. This contains <akn:sub eId="hauptteil-1_para-1_abs-1_inhalt-1_text-1_sub-1" GUID="ad66eeaf-0217-4e53-9c48-8bcb325c0d70">subscript</akn:sub> text. This contains <akn:sup
                          eId="hauptteil-1_para-1_abs-1_inhalt-1_text-1_sup-1" GUID="7a3eb2b3-9df7-4c21-a9d8-f2b6a1d81597">superscript</akn:sup> text. This text contains a <akn:a href="#" eId="hauptteil-1_para-1_abs-1_inhalt-1_text-1_a-1" GUID="0d9de688-e6d0-446d-a164-503420ffbb76">link</akn:a>. <akn:span
                          eId="hauptteil-1_para-1_abs-1_inhalt-1_text-1_span-1" GUID="0ae5920a-34ed-4391-87ec-cafb2780e868">Inline container</akn:span>. <akn:br eId="hauptteil-1_para-1_abs-1_inhalt-1_text-1_br-1" GUID="263f863c-99ac-4421-a99f-a430f879ef33"></akn:br><akn:br
                          eId="hauptteil-1_para-1_abs-1_inhalt-1_text-1_br-2" GUID="0a5685dd-08f2-41c2-aad9-8b20f85635c5"></akn:br>This text has two preceding line breaks.</akn:p>
                    </akn:content>
                  </akn:paragraph>
                </akn:article>
              </akn:body>
            </akn:act>
          </akn:akomaNtoso>
          """;

  @Test
  void reindexAllIgnoreseInvalidFiles() throws RetryableObjectStoreException {

    when(this.bucket.getAllFilenamesByPath("eli/"))
        .thenReturn(
            List.of(
                "eli/bund/bgbl-1/1992/s101/1992-01-01/1/deu/1992-01-02/regelungstext-1.xml",
                "not an eli"));
    when(this.bucket.getAllFilenamesByPath("eli/bund/bgbl-1/1992/s101/1992-01-01/1/deu"))
        .thenReturn(
            List.of("eli/bund/bgbl-1/1992/s101/1992-01-01/1/deu/1992-01-02/regelungstext-1.xml"));
    when(this.bucket.getFileAsString(
            "eli/bund/bgbl-1/1992/s101/1992-01-01/1/deu/1992-01-02/regelungstext-1.xml"))
        .thenReturn(Optional.of(testContent));

    String startingTimestamp =
        ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
    this.service.reindexAll(startingTimestamp);

    Norm exptectedNorm =
        NormLdmlToOpenSearchMapper.parseNorm(testContent, Collections.emptyMap()).orElseThrow();

    verify(repo, times(1))
        .saveAll(
            argThat(
                arg -> {
                  assertThat(arg.iterator().next().getId()).isEqualTo(exptectedNorm.getId());
                  return true;
                }));
    verify(repo, times(1)).deleteByIndexedAtBefore(startingTimestamp);
  }

  @Test
  void itReturnsRightNumberOfFiles() {
    when(this.bucket.getAllFilenamesByPath("eli/"))
        .thenReturn(
            List.of(
                "eli/bund/bgbl-1/2013/s323/2018-07-02/2/deu/2025-03-08/regelungstext-1.xml",
                "eli/bund/bgbl-1/2013/s4098/2022-03-15/2/deu/2025-03-08/regelungstext-1.xml",
                "eli/bund/bgbl-1/2013/s1925/2015-10-12/2/deu/2025-03-08/offenestruktur-1.xml"));
    assertThat(service.getNumberOfFilesInBucket()).isEqualTo(2);
  }
}
