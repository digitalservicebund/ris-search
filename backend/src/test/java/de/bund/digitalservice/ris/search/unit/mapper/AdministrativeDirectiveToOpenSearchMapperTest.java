package de.bund.digitalservice.ris.search.unit.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.mapper.AdministrativeDirectiveLdmlToOpenSearchMapper;
import de.bund.digitalservice.ris.search.models.ldml.directive.AdministrativeDirectiveLdml;
import de.bund.digitalservice.ris.search.models.ldml.directive.Doc;
import de.bund.digitalservice.ris.search.models.ldml.directive.DocumentType;
import de.bund.digitalservice.ris.search.models.ldml.directive.DocumentTypeCategory;
import de.bund.digitalservice.ris.search.models.ldml.directive.FrbrNameValueElement;
import de.bund.digitalservice.ris.search.models.ldml.directive.FrbrWork;
import de.bund.digitalservice.ris.search.models.ldml.directive.Identification;
import de.bund.digitalservice.ris.search.models.ldml.directive.Meta;
import de.bund.digitalservice.ris.search.models.ldml.directive.Normgeber;
import de.bund.digitalservice.ris.search.models.ldml.directive.Proprietary;
import de.bund.digitalservice.ris.search.models.ldml.directive.RisMeta;
import de.bund.digitalservice.ris.search.models.opensearch.AdministrativeDirective;
import java.util.List;
import org.junit.jupiter.api.Test;

class AdministrativeDirectiveToOpenSearchMapperTest {

  private AdministrativeDirectiveLdml getLdmlWithMandatoryFields() {
    AdministrativeDirectiveLdml ldml = new AdministrativeDirectiveLdml();
    Doc doc = new Doc();
    Meta meta = new Meta();
    Proprietary proprietary = new Proprietary();
    Identification identification = new Identification();
    FrbrWork work = new FrbrWork();
    FrbrNameValueElement nameValue = new FrbrNameValueElement();
    nameValue.setValue("ABC000");
    nameValue.setName("Dokumentnummer");
    work.setFrbrAliasList(List.of(nameValue));
    identification.setFrbrWork(work);

    meta.setIdentification(identification);
    RisMeta risMeta = new RisMeta();

    DocumentType docType = new DocumentType();
    docType.setCategory(DocumentTypeCategory.VV);

    risMeta.setDocumentType(docType);
    proprietary.setMeta(risMeta);
    meta.setProprietary(proprietary);

    doc.setMeta(meta);
    ldml.setDoc(doc);

    return ldml;
  }

  @Test
  void itExtractsTheNormgeberStaatAndOrganAsASingleString() {
    AdministrativeDirectiveLdml ldml = getLdmlWithMandatoryFields();
    Normgeber normgeber = new Normgeber();
    normgeber.setStaat("BB");
    normgeber.setOrgan("Ministerium");

    ldml.getDoc().getMeta().getProprietary().getMeta().setNormgeber(normgeber);
    AdministrativeDirective entity = AdministrativeDirectiveLdmlToOpenSearchMapper.map(ldml);
    assertThat(entity.normgeber()).isEqualTo("BB Ministerium");
  }

  @Test
  void itExtractsTheNormgeberStaat() {
    AdministrativeDirectiveLdml ldml = getLdmlWithMandatoryFields();
    Normgeber normgeber = new Normgeber();
    normgeber.setStaat("BB");

    ldml.getDoc().getMeta().getProprietary().getMeta().setNormgeber(normgeber);
    AdministrativeDirective entity = AdministrativeDirectiveLdmlToOpenSearchMapper.map(ldml);
    assertThat(entity.normgeber()).isEqualTo("BB");
  }
}
