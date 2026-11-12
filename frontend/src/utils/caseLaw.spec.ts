import { describe, expect, it } from "vitest";
import {
  formatVerknuepfteEntscheidung,
  getCaselawSecondaryTitle,
  toVerknuepfteEntscheidungListValue,
} from "~/utils/caseLaw";
import { truncateAtWord } from "~/utils/textFormatting";

describe("caselaw", () => {
  describe("getCaselawSecondaryTitle", () => {
    it("joins all nonblank decision names", () => {
      expect(
        getCaselawSecondaryTitle({
          decisionNames: ["", "Decision name", "Another decision name"],
          titleLine: "Title line",
        }),
      ).toBe("Decision name, Another decision name");
    });

    it("falls back to the Titlezeile", () => {
      expect(
        getCaselawSecondaryTitle({
          decisionNames: [],
          titleLine: "Title line",
        }),
      ).toBe("Title line");
    });

    it("returns undefined without a decision name or Titlezeile", () => {
      expect(getCaselawSecondaryTitle({ decisionNames: [] })).toBeUndefined();
    });

    it("truncates the secondary title to 90 characters", () => {
      expect(
        getCaselawSecondaryTitle({ decisionNames: ["a".repeat(100)] }),
      ).toBe("a".repeat(90) + "…");
    });

    it("truncates the secondary title after formatting decision names", () => {
      const decisionName1 = "a".repeat(50);
      const decisionName2 = "b".repeat(50);
      expect(
        getCaselawSecondaryTitle({
          decisionNames: [decisionName1, decisionName2],
        }),
      ).toBe(truncateAtWord(`${decisionName1}, ${decisionName2}`, 90, true));
    });

    it("does not truncate the title when disabled", () => {
      const fullTItle = "a".repeat(100);
      expect(
        getCaselawSecondaryTitle({ decisionNames: [fullTItle] }, false),
      ).toBe(fullTItle);
    });
  });

  describe("formatVerknuepfteEntscheidung", () => {
    it("formats a decision with all parts", () => {
      expect(
        formatVerknuepfteEntscheidung({
          gericht: { gerichtstyp: "VG", gerichtsort: "Berlin" },
          dokumenttyp: "Urteil",
          entscheidungsdatum: "2020-01-01",
          aktenzeichen: "1 K 23/19",
        }),
      ).toBe("VG Berlin, Urteil vom 1. Januar 2020 - 1 K 23/19");
    });

    it("appends anhängig for pending proceedings", () => {
      expect(
        formatVerknuepfteEntscheidung({
          art: "anhängig",
          gericht: { gerichtstyp: "BVerwG" },
          dokumenttyp: "Beschluss",
          entscheidungsdatum: "2022-10-01",
          aktenzeichen: "3 B 12/22",
        }),
      ).toBe("BVerwG, Beschluss vom 1. Oktober 2022 - 3 B 12/22 (anhängig)");
    });

    it("detects pending proceedings case-insensitively", () => {
      expect(
        formatVerknuepfteEntscheidung({
          art: "Anhängig",
          aktenzeichen: "3 B 12/22",
        }),
      ).toBe("3 B 12/22 (anhängig)");
    });

    it("does not append anhängig for concluded decisions", () => {
      expect(
        formatVerknuepfteEntscheidung({
          art: "nachgehend",
          aktenzeichen: "4 C 1/23",
        }),
      ).toBe("4 C 1/23");
    });

    it("uses only the Gerichtsort when the Gerichtstyp is missing", () => {
      expect(
        formatVerknuepfteEntscheidung({
          gericht: { gerichtsort: "Berlin" },
          dokumenttyp: "Urteil",
        }),
      ).toBe("Berlin, Urteil");
    });

    it("omits a court without Gerichtstyp and Gerichtsort", () => {
      expect(
        formatVerknuepfteEntscheidung({
          gericht: { gerichtsbarkeit: "Verwaltungsgerichtsbarkeit" },
          dokumenttyp: "Urteil",
        }),
      ).toBe("Urteil");
    });

    it("omits a blank court", () => {
      expect(
        formatVerknuepfteEntscheidung({
          gericht: { gerichtstyp: " " },
          aktenzeichen: "1 K 23/19",
        }),
      ).toBe("1 K 23/19");
    });

    it("starts with the date when court and Dokumenttyp are missing", () => {
      expect(
        formatVerknuepfteEntscheidung({
          entscheidungsdatum: "2020-12-31",
          aktenzeichen: "1 K 23/19",
        }),
      ).toBe("vom 31. Dezember 2020 - 1 K 23/19");
    });

    it("returns undefined when no parts are present", () => {
      expect(
        formatVerknuepfteEntscheidung({ art: "anhängig" }),
      ).toBeUndefined();
    });
  });

  describe("toVerknuepfteEntscheidungListValue", () => {
    it("links to the decision when it has a Dokumentnummer", () => {
      expect(
        toVerknuepfteEntscheidungListValue({
          dokumentnummer: "KORE300002025",
          aktenzeichen: "1 K 23/19",
        }),
      ).toEqual({
        text: "1 K 23/19",
        to: "/gerichtsentscheidungen/KORE300002025",
      });
    });

    it("returns plain text without a Dokumentnummer", () => {
      expect(
        toVerknuepfteEntscheidungListValue({ aktenzeichen: "1 K 23/19" }),
      ).toBe("1 K 23/19");
    });

    it("returns undefined when the decision cannot be formatted", () => {
      expect(
        toVerknuepfteEntscheidungListValue({ dokumentnummer: "KORE300002025" }),
      ).toBeUndefined();
    });
  });
});
