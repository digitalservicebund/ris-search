package de.bund.digitalservice.ris.search.integration.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.c4_soft.springaddons.security.oauth2.test.annotations.WithJwt;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import de.bund.digitalservice.ris.search.repository.opensearch.NormsRepository;
import de.bund.digitalservice.ris.search.service.IndexNormsService;
import de.bund.digitalservice.ris.search.utils.eli.ManifestationEli;
import org.apache.commons.compress.utils.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Tag("integration")
@WithJwt("jwtTokens/ValidAccessToken.json")
class IndexNormsServiceIntegrationTest extends ContainersIntegrationBase {

  @Autowired private NormsBucket bucket;

  @Autowired private NormsRepository repository;

  private IndexNormsService indexNormsService;

  @BeforeEach
  void setup() {
    indexNormsService = new IndexNormsService(bucket, repository);
  }

  private String regelungstextTemplate =
      """
              <?xml version="1.0" encoding="UTF-8"?>
              <?xml-model href="../../../Grammatiken/legalDocML.de.sch" schematypens="http://purl.oclc.org/dsdl/schematron"?>
              <akn:akomaNtoso xmlns:akn="http://Inhaltsdaten.LegalDocML.de/1.8.2/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xsi:schemaLocation="http://Metadaten.LegalDocML.de/1.8.2/ ../../../Grammatiken/legalDocML.de-metadaten.xsd
                                    http://Inhaltsdaten.LegalDocML.de/1.8.2/ ../../../Grammatiken/legalDocML.de-regelungstextverkuendungsfassung.xsd">
                <akn:act name="regelungstext">
                  <akn:meta eId="meta-1" GUID="40a13276-39ac-4fc9-aafd-3fbfea0df2b0">
                    <akn:identification eId="meta-1_ident-1" GUID="6098a736-d359-4caf-a45e-dfa3792f2bc4" source="attributsemantik-noch-undefiniert">
                      <akn:FRBRWork eId="meta-1_ident-1_frbrwork-1" GUID="cc4476ce-6342-4bb9-a777-030a21e4b0fc">
                        <akn:FRBRthis eId="meta-1_ident-1_frbrwork-1_frbrthis-1" GUID="d2bd3a22-c547-479d-85c3-ef374778c74b" value="%s"></akn:FRBRthis>
                        <akn:FRBRuri eId="meta-1_ident-1_frbrwork-1_frbruri-1" GUID="0d015c4c-be37-453a-917f-7975287b2fcf" value="eli/bund/bgbl-1/1992/s101/regelungstext-1"></akn:FRBRuri>
                        <akn:FRBRalias eId="meta-1_ident-1_frbrwork-1_frbralias-1" GUID="e2acf308-f9e4-4781-ba3a-5339a999f69f" name="übergreifende-id" value="f96cfae4-4fce-4c72-9186-0d84778dc11c"></akn:FRBRalias>
                        <akn:FRBRdate eId="meta-1_ident-1_frbrwork-1_frbrdate-1" GUID="63e8780d-4225-4987-aec1-794510b4d7f6" date="1992-01-01" name="verkuendungsfassung"></akn:FRBRdate>
                        <akn:FRBRauthor eId="meta-1_ident-1_frbrwork-1_frbrauthor-1" GUID="b6deb229-93c7-43f4-886a-b9693587ca8a" href="recht.bund.de/institution/bundesregierung"></akn:FRBRauthor>
                        <akn:FRBRcountry eId="meta-1_ident-1_frbrwork-1_frbrcountry-1" GUID="1fd2ebf9-a423-443c-a7e5-665b86a0d9d9" value="de"></akn:FRBRcountry>
                        <akn:FRBRnumber eId="meta-1_ident-1_frbrwork-1_frbrnumber-1" GUID="e7405de2-48df-4049-9621-a0ffa2d8e317" value="1"></akn:FRBRnumber>
                        <akn:FRBRname eId="meta-1_ident-1_frbrwork-1_frbrname-1" GUID="fc410fa5-4236-4735-87e5-1bb5ab1402ba" value="bgbl-1"></akn:FRBRname>
                        <akn:FRBRsubtype eId="meta-1_ident-1_frbrwork-1_frbrsubtype-1" GUID="994fe962-3694-49ad-a2d1-4e17d12b10d0" value="regelungstext-1"></akn:FRBRsubtype>
                      </akn:FRBRWork>
                      <akn:FRBRExpression eId="meta-1_ident-1_frbrexpression-1" GUID="2a16dd66-9151-40d4-ab04-3f0525b4102d">
                        <akn:FRBRthis eId="meta-1_ident-1_frbrexpression-1_frbrthis-1" GUID="b4d5b0c5-53e3-4c51-b6c6-0311563941df" value="%s"></akn:FRBRthis>
                        <akn:FRBRuri eId="meta-1_ident-1_frbrexpression-1_frbruri-1" GUID="8909d2e8-abde-437a-8efc-f45483b4f996" value="eli/bund/bgbl-1/1992/s101/1992-01-01/1/deu/regelungstext-1"></akn:FRBRuri>
                        <akn:FRBRalias eId="meta-1_ident-1_frbrexpression-1_frbralias-1" GUID="c23228e0-af6b-46e0-aaae-93520ae758c9" name="vorherige-version-id" value="79c7f606-b47c-4c7d-8e3a-1a89b333cd85"></akn:FRBRalias>
                        <akn:FRBRalias eId="meta-1_ident-1_frbrexpression-1_frbralias-2" GUID="2e74b960-342e-4af1-b127-08bfef86d3ae" name="aktuelle-version-id" value="d33c67a0-2be2-4728-932d-5abae5a84422"></akn:FRBRalias>
                        <akn:FRBRalias eId="meta-1_ident-1_frbrexpression-1_frbralias-3" GUID="a7a64f41-bd12-4311-9ae0-dd23e94fa018" name="nachfolgende-version-id" value="24c51028-eb62-4853-a986-6c62e6e25731"></akn:FRBRalias>
                        <akn:FRBRauthor eId="meta-1_ident-1_frbrexpression-1_frbrauthor-1" GUID="4e8005ef-7e71-478f-bc91-b89e8c69e9e2" href="recht.bund.de/institution/bundesregierung"></akn:FRBRauthor>
                        <akn:FRBRdate eId="meta-1_ident-1_frbrexpression-1_frbrdate-1" GUID="a5bffed4-7bb4-4b74-a326-eaabfea58f94" date="1992-01-01" name="verkuendung"></akn:FRBRdate>
                        <akn:FRBRlanguage eId="meta-1_ident-1_frbrexpression-1_frbrlanguage-1" GUID="e6cfb417-7a91-444d-8563-a0a09c55058c" language="deu"></akn:FRBRlanguage>
                        <akn:FRBRversionNumber eId="meta-1_ident-1_frbrexpression-1_frbrersionnumber-1" GUID="6b354c1d-cc0b-4df0-83ee-c5d062cc2d0a" value="1"></akn:FRBRversionNumber>
                      </akn:FRBRExpression>
                      <akn:FRBRManifestation eId="meta-1_ident-1_frbrmanifestation-1" GUID="a3747035-aff8-41a6-86d7-0c7b75caddf2">
                        <akn:FRBRthis eId="meta-1_ident-1_frbrmanifestation-1_frbrthis-1" GUID="451a75df-0eb0-4644-ba75-04743d44aadc" value="%s"></akn:FRBRthis>
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
                  %s
                </akn:act>
              </akn:akomaNtoso>
              """;

  private String anlageTemplate =
      """
          <?xml version="1.0" encoding="UTF-8"?>
          <?xml-model href="Grammatiken/legalDocML.de.sch" schematypens="http://purl.oclc.org/dsdl/schematron"?>
          <akn:akomaNtoso xmlns:akn="http://Inhaltsdaten.LegalDocML.de/1.8.2/"
                          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                          xsi:schemaLocation="http://MetadatenRIS.LegalDocML.de/1.8.2/ Grammatiken/legalDocML.de-metadaten-ris.xsd
                                  http://MetadatenRegelungstext.LegalDocML.de/1.8.2/ Grammatiken/legalDocML.de-metadaten-regelungstext.xsd
                                  http://MetadatenRechtsetzungsdokument.LegalDocML.de/1.8.2/ Grammatiken/legalDocML.de-metadaten-rechtsetzungsdokument.xsd
                                  http://Inhaltsdaten.LegalDocML.de/1.8.2/ Grammatiken/legalDocML.de-offenestruktur.xsd">
              <akn:doc name="/akn/ontology/de/concept/documenttype/bund/anlage-regelungstext">
                  <akn:meta GUID="4cda11eb-c56d-495e-bd32-809501304d20" eId="meta-n1">
                      <akn:identification GUID="c411c3fa-065c-4485-b7cd-8458f997b0b8" eId="meta-n1_ident-n1"
                                          source="attributsemantik-noch-undefiniert">
                          <akn:FRBRWork GUID="4d05c211-504e-431f-acb6-5a048ae85c08" eId="meta-n1_ident-n1_frbrwork-n1">
                              <akn:FRBRthis GUID="e50b1116-b94b-44d9-89c7-dfb65fd0e5ac" eId="meta-n1_ident-n1_frbrwork-n1_frbrthis-n1"
                                            value="%s"/>
                              <akn:FRBRuri GUID="e821153a-29bd-45ba-a395-fac7abfddc0f" eId="meta-n1_ident-n1_frbrwork-n1_frbruri-n1"
                                           value="eli/bund/bgbl-1/2000/s1016/offenestruktur-1"/>
                              <akn:FRBRalias GUID="9e411718-08ee-4f49-a9ce-9eb92731d850"
                                             eId="meta-n1_ident-n1_frbrwork-n1_frbralias-n1" name="übergreifende-id"
                                             value="38b112bd-ae28-4008-b1db-146fe794ed30"/>
                              <akn:FRBRdate GUID="64551148-935c-4b64-819b-2458492d3a16" date="2000-05-27"
                                            eId="meta-n1_ident-n1_frbrwork-n1_frbrdate-n1" name="verkuendungsfassung-verkuendungsdatum"/>
                              <akn:FRBRauthor GUID="21a1139e-3c3c-4c84-a704-64231d3ccc5a"
                                              eId="meta-n1_ident-n1_frbrwork-n1_frbrauthor-n1"
                                              href="recht.bund.de/institution/bundesregierung"/>
                              <akn:FRBRcountry GUID="fadf130b-a548-4119-a281-3a196b8e0470"
                                               eId="meta-n1_ident-n1_frbrwork-n1_frbrcountry-n1" value="de"/>
                              <akn:FRBRnumber GUID="d349f87c-fa03-4e11-9252-923ff67ef28b"
                                              eId="meta-n1_ident-n1_frbrwork-n1_frbrnumber-n1" value="s1016"/>
                              <akn:FRBRname GUID="1992ba44-58b6-445e-9211-ef4dec597073" eId="meta-n1_ident-n1_frbrwork-n1_frbrname-n1"
                                            value="bgbl-1"/>
                              <akn:FRBRsubtype GUID="203a80d9-90f3-4f4c-8e11-14c639f92c01"
                                               eId="meta-n1_ident-n1_frbrwork-n1_frbrsubtype-n1" value="anlage-regelungstext-1"/>
                          </akn:FRBRWork>
                          <akn:FRBRExpression GUID="16857812-3b53-4711-adb3-794b9b1b6113" eId="meta-n1_ident-n1_frbrexpression-n1">
                              <akn:FRBRthis GUID="a59ca0ff-1c34-117d-afaf-4fe42778550d"
                                            eId="meta-n1_ident-n1_frbrexpression-n1_frbrthis-n1"
                                            value="%s"/>
                              <akn:FRBRuri GUID="6f859aa2-4153-4a11-b4be-313e425dd7b6"
                                           eId="meta-n1_ident-n1_frbrexpression-n1_frbruri-n1"
                                           value="eli/bund/bgbl-1/2000/s1016/2023-04-26/10/deu/anlage-regelungstext-1"/>
                              <akn:FRBRalias GUID="bba8d3ad-83df-411e-aed4-1ab9df1382a6"
                                             eId="meta-n1_ident-n1_frbrexpression-n1_frbralias-n1" name="vorherige-version-id"
                                             value="e7317ecb-0ca1-4116-b5be-80e7cfdd2e09"/>
                              <akn:FRBRalias GUID="00d9ce7a-5be3-11cd-9626-ba8ac66c680d"
                                             eId="meta-n1_ident-n1_frbrexpression-n1_frbralias-n2" name="aktuelle-version-id"
                                             value="5ec7ff64-6b38-4211-9927-92a1bcd0f63b"/>
                              <akn:FRBRalias GUID="c1619709-7deb-114e-9a3f-e18560aa2f12"
                                             eId="meta-n1_ident-n1_frbrexpression-n1_frbralias-n3" name="nachfolgende-version-id"
                                             value="4871652f-4364-11c1-96db-93b638c457a4"/>
                              <akn:FRBRauthor GUID="edf49ab8-6237-1195-840f-052206f49da6"
                                              eId="meta-n1_ident-n1_frbrexpression-n1_frbrauthor-n1"
                                              href="example.de/institution/test"/>
                              <akn:FRBRdate GUID="25bca8bf-1148-4fe2-a1f0-811e4192cd99" date="2023-04-26"
                                            eId="meta-n1_ident-n1_frbrexpression-n1_frbrdate-n1" name="verkuendung"/>
                              <akn:FRBRlanguage GUID="b0711c29-1a02-4e91-9610-46a437388fbf"
                                                eId="meta-n1_ident-n1_frbrexpression-n1_frbrlanguage-n1" language="deu"/>
                              <akn:FRBRversionNumber GUID="98498e8e-11a3-4724-b469-e068a1c127f3"
                                                     eId="meta-n1_ident-n1_frbrexpression-n1_frbrversionnumber-n1" value="10"/>
                          </akn:FRBRExpression>
                          <akn:FRBRManifestation GUID="36cf571d-1120-4ba7-89e8-80381bd607bd"
                                                 eId="meta-n1_ident-n1_frbrmanifestation-n1">
                              <akn:FRBRthis GUID="051360a2-45ad-1129-825d-f2961dea6f7e"
                                            eId="meta-n1_ident-n1_frbrmanifestation-n1_frbrthis-n1"
                                            value="%s"/>
                              <akn:FRBRuri GUID="1646c4f5-e4c5-4113-8b1a-8986a176df07"
                                           eId="meta-n1_ident-n1_frbrmanifestation-n1_frbruri-n1"
                                           value="eli/bund/bgbl-1/2000/s1016/2023-04-26/10/deu/2023-04-26/anlage-regelungstext-1.xml"/>
                              <akn:FRBRdate GUID="b8e3aa94-19f0-411f-81da-c2e4bce4bfe9" date="2023-04-26"
                                            eId="meta-n1_ident-n1_frbrmanifestation-n1_frbrdate-n1" name="generierung"/>
                              <akn:FRBRauthor GUID="937aab19-211f-4c72-a50f-3e77ad8cb80e"
                                              eId="meta-n1_ident-n1_frbrmanifestation-n1_frbrauthor-n1" href="recht.bund.de"/>
                              <akn:FRBRformat GUID="656cf2ab-11ee-4085-a378-ee5accf1ff9f"
                                              eId="meta-n1_ident-n1_frbrmanifestation-n1_frbrformat-n1" value="xml"/>
                          </akn:FRBRManifestation>
                      </akn:identification>
                  </akn:meta>
                  <akn:preface GUID="ca78411a-6a11-4779-9958-f88fc48efbf7" eId="einleitung-n1">
                      <akn:block GUID="5f317c6b-a602-4e33-115c-7c2fe1ec4060" eId="einleitung-n1_block-n1"
                                 name="attributsemantik-noch-undefiniert">
                          <akn:docTitle GUID="abcd1234-5678-90ef-115c-7c2fe1ec4060" eId="einleitung-n1_block-n1_doctitel-n1">
                              <akn:inline GUID="dff9e31d-d9a6-442f-a28a-9899fb5d9b01"
                                          eId="einleitung-n1_block-n1_doctitel-n1_inline-n1"
                                          name="attributsemantik-noch-undefiniert" refersTo="anlageregelungstext-num">%s
                              </akn:inline>
                              <akn:inline GUID="f8610d9b-35f4-432b-a63e-5c759fbd3617"
                                          eId="einleitung-n1_block-n1_doctitel-n1_inline-n2"
                                          name="attributsemantik-noch-undefiniert" refersTo="anlageregelungstext-bezug">
                              </akn:inline>
                          </akn:docTitle>
                      </akn:block>
                  </akn:preface>
                  <akn:mainBody GUID="43960b63-b71f-4119-b6c8-cae9ab89741c" eId="hauptteil-n1">
                      <akn:p GUID="46c37aae-55bb-4b26-a11d-6a73c41e30b6" eId="hauptteil-n1_text-n1">
                          This is a test attachment
                      </akn:p>
                  </akn:mainBody>
              </akn:doc>
          </akn:akomaNtoso>
          """;

  private String mockWithTemplate(
      String template, String manifestationEliString, String attachmentsBlockOrTitle) {
    var manifestationEli = ManifestationEli.fromString(manifestationEliString);
    if (manifestationEli.isEmpty()) {
      return "";
    }

    var expressionEli = manifestationEli.get().getExpressionEli();
    var workEli = manifestationEli.get().getWorkEli();

    return String.format(
        template, workEli, expressionEli, manifestationEli, attachmentsBlockOrTitle);
  }

  private String attachmentBlock(String attachmentEliString) {
    return String.format(
        """
            <akn:attachments GUID="32b152d5-aee4-101f-10b1-4b6f6e485fde" eId="anlagen-n1">
                <akn:attachment GUID="ff624c3c-b438-101b-b67e-7bf875f3fbab" eId="anlagen-n1_anlage-n1">
                    <akn:documentRef GUID="3c39bb1b-7aa1-4b10-af10-f0cb61014bf8" eId="anlagen-n1_anlage-n1_verweis-n1"
                                     href="%s"
                                     showAs="/akn/ontology/de/concept/documenttype/bund/anlage-regelungstext"/>
                </akn:attachment>
            </akn:attachments>
            """,
        attachmentEliString);
  }

  @Test
  void shouldReindexNormIfOnlyAttachmentChanged() throws ObjectStoreServiceException {
    var anlage1Eli = "eli/foo/bar/1000/s1/2000-01-01/1/deu/2000-01-01/anlage-regelungstext.xml";
    var regelungstext1Eli =
        "eli/foo/bar/1000/s1/2000-01-01/1/deu/2000-01-01/regelungstext-verkuendung-1.xml";
    var anlage1 = mockWithTemplate(anlageTemplate, anlage1Eli, "Title 1");
    var regelungstext1 =
        mockWithTemplate(regelungstextTemplate, regelungstext1Eli, attachmentBlock(anlage1Eli));

    bucket.save(regelungstext1Eli, regelungstext1);
    bucket.save(anlage1Eli, anlage1);

    repository.deleteAll();
    assertThat(indexNormsService.getNumberOfIndexedDocuments()).isZero();
    var changelog1 = new Changelog();
    changelog1.setChanged(Sets.newHashSet(regelungstext1Eli, anlage1Eli));
    indexNormsService.indexChangelog("changelog1", changelog1);
    assertThat(indexNormsService.getNumberOfIndexedDocuments()).isEqualTo(1);
    var expressionEli =
        ManifestationEli.fromString(regelungstext1Eli).get().getExpressionEli().toString();
    var norm = repository.getByExpressionEli(expressionEli);
    assertThat(norm.getArticles().getLast().name().strip()).isEqualTo("Title 1");

    // Now the attachment changes
    var anlage1Changed = mockWithTemplate(anlageTemplate, anlage1Eli, "Title 1 Changed");
    bucket.delete(anlage1Eli);
    bucket.save(anlage1Eli, anlage1Changed);
    var changelog2 = new Changelog();
    changelog1.setChanged(Sets.newHashSet(anlage1Eli));
    indexNormsService.indexChangelog("changelog2", changelog2);

    assertThat(indexNormsService.getNumberOfIndexedDocuments()).isEqualTo(1);
    var normChanged = repository.getByExpressionEli(expressionEli);
    assertThat(normChanged.getArticles().getLast().name().strip()).isEqualTo("Title 1 Changed");
  }
}
