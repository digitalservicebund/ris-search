import type { Rechtsprechung } from "~/types/api";
import {
  formatZitierungLiteraturSelbststaendig,
  formatZitierungLiteraturUnselbststaendig,
  formatZitierungRechtsprechung,
  getZitierungen,
} from "./zitierungen";

describe("zitierungen", () => {
  describe("formatZitierungRechtsprechung", () => {
    it("formats all fields", () => {
      expect(
        formatZitierungRechtsprechung({
          artDerZitierung: "Parallelentscheidung",
          gericht: { gerichtstyp: "BVerwG", gerichtsort: "Leipzig" },
          entscheidungsdatum: "2026-08-27",
          dokumenttyp: "Beschluss",
          aktenzeichen: "6 B 13.26",
        }),
      ).toBe(
        "Parallelentscheidung BVerwG Leipzig, 27. August 2026 — 6 B 13.26",
      );
    });

    it("leaves out missing Art der Zitierung and Gerichtsort", () => {
      expect(
        formatZitierungRechtsprechung({
          gericht: { gerichtstyp: "BVerfG" },
          entscheidungsdatum: "2020-11-11",
          aktenzeichen: "1 BvR 1/20",
        }),
      ).toBe("BVerfG, 11. November 2020 — 1 BvR 1/20");
    });

    it("leaves out the separator of a missing date", () => {
      expect(
        formatZitierungRechtsprechung({
          gericht: { gerichtstyp: "BGH" },
          aktenzeichen: "I ZR 1/24",
        }),
      ).toBe("BGH — I ZR 1/24");
    });

    it("leaves out the separator of a missing Aktenzeichen", () => {
      expect(
        formatZitierungRechtsprechung({
          gericht: { gerichtstyp: "BGH" },
          entscheidungsdatum: "2024-01-02",
        }),
      ).toBe("BGH, 2. Januar 2024");
    });

    it("formats a citation with only an Aktenzeichen", () => {
      expect(formatZitierungRechtsprechung({ aktenzeichen: "I ZR 1/24" })).toBe(
        "I ZR 1/24",
      );
    });

    it("ignores blank fields", () => {
      expect(
        formatZitierungRechtsprechung({
          artDerZitierung: " ",
          gericht: { gerichtstyp: "BGH", gerichtsort: "" },
          aktenzeichen: "I ZR 1/24",
        }),
      ).toBe("BGH — I ZR 1/24");
    });

    it("returns undefined if there is nothing to show", () => {
      expect(formatZitierungRechtsprechung({})).toBeUndefined();
    });
  });

  describe("formatZitierungLiteraturUnselbststaendig", () => {
    it("formats all fields", () => {
      expect(
        formatZitierungLiteraturUnselbststaendig({
          autor: "Kugelmüller-Pugh, Anette",
          fundstelle: {
            periodikum: { abkuerzung: "DStR", titel: "Deutsches Steuerrecht" },
            zitatstelle: "2026, 1636",
          },
        }),
      ).toBe(
        "Kugelmüller-Pugh, Anette; DStR (Deutsches Steuerrecht) 2026, 1636",
      );
    });

    it("leaves out the parentheses of a missing Periodikum title", () => {
      expect(
        formatZitierungLiteraturUnselbststaendig({
          autor: "Müller",
          fundstelle: {
            periodikum: { abkuerzung: "NVwZ" },
            zitatstelle: "2020, 7",
          },
        }),
      ).toBe("Müller; NVwZ 2020, 7");
    });

    it("leaves out the separator of a missing Autor", () => {
      expect(
        formatZitierungLiteraturUnselbststaendig({
          fundstelle: {
            periodikum: { abkuerzung: "NVwZ" },
            zitatstelle: "2020, 7",
          },
        }),
      ).toBe("NVwZ 2020, 7");
    });

    it("formats a citation with only an Autor", () => {
      expect(
        formatZitierungLiteraturUnselbststaendig({ autor: "Müller" }),
      ).toBe("Müller");
    });

    it("returns undefined if there is nothing to show", () => {
      expect(
        formatZitierungLiteraturUnselbststaendig({ fundstelle: {} }),
      ).toBeUndefined();
    });
  });

  describe("formatZitierungLiteraturSelbststaendig", () => {
    it("formats all fields", () => {
      expect(
        formatZitierungLiteraturSelbststaendig({
          autor: "Böhringer, Ayse-Martina",
          titel: "Pandemie im offenen Grundrechtsstaat",
          veroeffentlichungsjahr: "2021",
        }),
      ).toBe(
        "Böhringer, Ayse-Martina; Pandemie im offenen Grundrechtsstaat 2021",
      );
    });

    it("leaves out the separator of a missing Autor", () => {
      expect(
        formatZitierungLiteraturSelbststaendig({
          titel: "Pandemie im offenen Grundrechtsstaat",
          veroeffentlichungsjahr: "2021",
        }),
      ).toBe("Pandemie im offenen Grundrechtsstaat 2021");
    });

    it("formats a citation without Veröffentlichungsjahr", () => {
      expect(
        formatZitierungLiteraturSelbststaendig({
          autor: "Weber",
          titel: "Grundrechte",
        }),
      ).toBe("Weber; Grundrechte");
    });

    it("returns undefined if there is nothing to show", () => {
      expect(formatZitierungLiteraturSelbststaendig({})).toBeUndefined();
    });
  });

  describe("getZitierungen", () => {
    it("splits Rechtsprechung by direction and merges passive Literatur", () => {
      const rechtsprechung = {
        zitierungenRechtsprechung: [
          { richtung: "aktiv", aktenzeichen: "A 1/20" },
          { richtung: "passiv", aktenzeichen: "P 2/20" },
        ],
        zitierungenLiteraturSelbststaendig: [
          { richtung: "passiv", autor: "SLI passiv" },
          { richtung: "aktiv", autor: "SLI aktiv" },
        ],
        zitierungenLiteraturUnselbststaendig: [
          { richtung: "passiv", autor: "ULI passiv" },
          { richtung: "aktiv", autor: "ULI aktiv" },
        ],
      } as Rechtsprechung;

      expect(getZitierungen(rechtsprechung)).toEqual({
        aktivRechtsprechung: [{ text: "A 1/20" }],
        passivRechtsprechung: [{ text: "P 2/20" }],
        passivLiteratur: [{ text: "SLI passiv" }, { text: "ULI passiv" }],
      });
    });

    it("links citations that have a Dokumentnummer", () => {
      const rechtsprechung = {
        zitierungenRechtsprechung: [
          {
            richtung: "aktiv",
            aktenzeichen: "A 1/20",
            dokumentnummer: "KORE000000001",
          },
        ],
        zitierungenLiteraturSelbststaendig: [
          { richtung: "passiv", autor: "SLI", dokumentnummer: "XXLS0001" },
        ],
        zitierungenLiteraturUnselbststaendig: [
          { richtung: "passiv", autor: "ULI", dokumentnummer: "XXLU0001" },
        ],
      } as Rechtsprechung;

      expect(getZitierungen(rechtsprechung, "search")).toEqual({
        aktivRechtsprechung: [
          {
            text: "A 1/20",
            to: {
              path: "/gerichtsentscheidungen/KORE000000001",
              query: { from: "search" },
            },
          },
        ],
        passivRechtsprechung: [],
        passivLiteratur: [
          {
            text: "SLI",
            to: {
              path: "/literaturnachweise/XXLS0001",
              query: { from: "search" },
            },
          },
          {
            text: "ULI",
            to: {
              path: "/literaturnachweise/XXLU0001",
              query: { from: "search" },
            },
          },
        ],
      });
    });

    it("leaves out citations without displayable data", () => {
      const rechtsprechung = {
        zitierungenRechtsprechung: [
          { richtung: "passiv", dokumentnummer: "KORE000000001" },
          { richtung: "passiv", aktenzeichen: "P 2/20" },
        ],
      } as Rechtsprechung;

      expect(getZitierungen(rechtsprechung)?.passivRechtsprechung).toEqual([
        { text: "P 2/20" },
      ]);
    });

    it("returns undefined if there are no citations", () => {
      expect(getZitierungen({} as Rechtsprechung)).toBeUndefined();
    });

    it("returns undefined if there are only citations that aren't shown", () => {
      const rechtsprechung = {
        zitierungenLiteraturSelbststaendig: [
          { richtung: "aktiv", autor: "SLI aktiv" },
        ],
        zitierungenVerwaltungsvorschriften: [
          { richtung: "passiv", jurisAbkuerzung: "VV" },
        ],
      } as Rechtsprechung;

      expect(getZitierungen(rechtsprechung)).toBeUndefined();
    });
  });
});
