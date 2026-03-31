package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import jakarta.xml.bind.ValidationException;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CaseLawLdmlValidator {

  public void validate(CaseLawLdml ldml) throws ValidationException {
    if (ldml == null) throw new ValidationException("LDML root is null");

    Judgment judgment = required(ldml.getJudgment(), "Judgment");
    Meta meta = required(judgment.getMeta(), "Meta");
    required(judgment.getJudgmentBody(), "JudgmentBody");

    FrbrElement work = required(meta.getIdentification().getFrbrWork(), "FRBRWork");
    required(work.getFrbrThis(), "FrbrThis (ID)");

    if (work.getFrbrDate() == null || work.getFrbrDate().getDate().isBlank()) {
      throw new ValidationException("Decision date is missing");
    }

    RisMeta risMeta = required(meta.getProprietary().getRisMeta(), "RisMeta");
    requiredList(risMeta.getAktenzeichen(), "FileNumbers (Aktenzeichen)");
    required(risMeta.getRisDokumentTyp(), "DocumentType");
    RisGericht gericht = required(risMeta.getRisGericht(), "RisGericht");
    required(gericht.getGerichtstyp(), "CourtType");

    if (judgment.getHeader() == null || judgment.getHeader().findShortTitle() == null) {
      throw new ValidationException("Header or Short Title is missing");
    }
  }

  private <T> T required(T object, String fieldName) throws ValidationException {
    if (object == null) {
      throw new ValidationException(fieldName + " is mandatory but missing");
    }
    return object;
  }

  private void requiredList(List<?> list, String fieldName) throws ValidationException {
    if (list == null || list.isEmpty()) {
      throw new ValidationException(fieldName + " cannot be null or empty");
    }
  }
}
