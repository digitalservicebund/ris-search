package de.bund.digitalservice.ris.search.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import de.bund.digitalservice.ris.search.schema.LiteraturnachweisSchema;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LiteraturnachweisSchemaMapperTest {

  @Test
  @DisplayName("Correctly maps attributes")
  void fromDomainSingle() {
    var literature =
        Literature.builder()
            .id("XXLU000000001")
            .documentNumber("XXLU000000001")
            .yearsOfPublication(List.of("1979", "2004-09"))
            .documentTypes(List.of("Auf"))
            .fundstellenUnselbststaendig(List.of("BUV, 1982, 123-123"))
            .fundstellenSelbststaendig(List.of("50 Jahre Betriebs-Berater, 1987, 123-456"))
            .normReferences(List.of("GG, Art 6 Abs 2 S 1, 1949-05-23"))
            .mainTitle("Hauptüberschrift")
            .mainTitleAdditions("Zusatz zur Hauptüberschrift")
            .documentaryTitle("Dokumentarischer Titel")
            .edition("Ausgabe")
            .internationalIdentifiers(List.of("ISBN XXXX"))
            .authors(List.of("Musterfrau, Sabine"))
            .collaborators(List.of("Mustermann, Max"))
            .editors(List.of("Bearbeiter"))
            .founder(List.of("Begründer"))
            .publisherPersons(List.of("Herausgeber Person"))
            .publisherOrganizations(List.of("Herausgeber Institution"))
            .publisherInformation(List.of("Verlagsangabe"))
            .originators(List.of("DGB"))
            .conferenceNotes(List.of("Internationaler Kongress 2025, Berlin, GER"))
            .universityNotes(List.of("Hochschulschriftenvermerk"))
            .volumes(List.of("Teilband 1", "Teilband 2"))
            .build();

    LiteraturnachweisSchema expected =
        LiteraturnachweisSchema.builder()
            .context("jsonLdContext")
            .id("/v1/literaturnachweise/XXLU000000001")
            .dokumentnummer("XXLU000000001")
            .veroeffentlichungsJahre(List.of("1979", "2004-09"))
            .dokumenttypen(List.of("Auf"))
            .fundstellenUnselbstaendig(List.of("BUV, 1982, 123-123"))
            .fundstellenSelbstaendig(List.of("50 Jahre Betriebs-Berater, 1987, 123-456"))
            .referenzenNorm(List.of("GG, Art 6 Abs 2 S 1, 1949-05-23"))
            .haupttitel("Hauptüberschrift")
            .haupttitelzusatz("Zusatz zur Hauptüberschrift")
            .dokumentarischerTitel("Dokumentarischer Titel")
            .ausgabe("Ausgabe")
            .bestellnummern(List.of("ISBN XXXX"))
            .verfasser(List.of("Musterfrau, Sabine"))
            .mitarbeiter(List.of("Mustermann, Max"))
            .bearbeiter(List.of("Bearbeiter"))
            .begruender(List.of("Begründer"))
            .herausgeberPersonen(List.of("Herausgeber Person"))
            .herausgeberInstitutionen(List.of("Herausgeber Institution"))
            .verlagsangaben(List.of("Verlagsangabe"))
            .urheber(List.of("DGB"))
            .kongressvermerke(List.of("Internationaler Kongress 2025, Berlin, GER"))
            .hochschulschriftenvermerke(List.of("Hochschulschriftenvermerk"))
            .teilbaende(List.of("Teilband 1", "Teilband 2"))
            .literaturtyp("uli")
            .encoding(
                EncodingSchemaFactory.documentEncodingSchemas(
                    "/v1/literaturnachweise/XXLU000000001"))
            .build();

    LiteraturnachweisSchema literaturnachweisSchema =
        LiteraturnachweisSchemaMapper.fromDomain(literature, "jsonLdContext");
    assertThat(literaturnachweisSchema).isEqualTo(expected);
  }
}
