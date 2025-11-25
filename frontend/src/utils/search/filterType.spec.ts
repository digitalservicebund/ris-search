import {
  dateFilterToQuery,
  isFilterType,
  isStrictDateFilterValue,
} from "./filterType";
import { DocumentKind } from "~/types";

describe("filterType", () => {
  describe("isFilterType", () => {
    it("returns true if the value is a known filter type", () => {
      for (const i of [
        "allTime",
        "period",
        "specificDate",
        "currentlyInForce",
      ]) {
        expect(isFilterType(i)).toBe(true);
      }
    });

    it("returns false if the value is not a known filter type", () => {
      for (const i of [undefined, "", "notAFilter"]) {
        expect(isFilterType(i as string)).toBe(false);
      }
    });
  });

  describe("isStrictDateFilterValue", () => {
    it("returns true for valid 'allTime' filter", () => {
      expect(
        isStrictDateFilterValue({
          type: "allTime",
          from: undefined,
          to: undefined,
        }),
      ).toBe(true);
    });

    it("returns true for valid 'currentlyInForce' filter", () => {
      expect(
        isStrictDateFilterValue({
          type: "currentlyInForce",
          from: undefined,
          to: undefined,
        }),
      ).toBe(true);
    });

    it("returns true for valid 'specificDate' filter", () => {
      expect(
        isStrictDateFilterValue({
          type: "specificDate",
          from: "2024-01-01",
          to: undefined,
        }),
      ).toBe(true);
    });

    it("returns true for valid 'period' filter", () => {
      expect(
        isStrictDateFilterValue({
          type: "period",
          from: "2024-01-01",
          to: "2024-12-31",
        }),
      ).toBe(true);
    });

    it("returns false for 'specificDate' without from date", () => {
      expect(
        isStrictDateFilterValue({
          type: "specificDate",
          from: undefined,
          to: undefined,
        }),
      ).toBe(false);
    });

    it("returns false for 'period' without from date", () => {
      expect(
        isStrictDateFilterValue({
          type: "period",
          from: undefined,
          to: "2024-12-31",
        }),
      ).toBe(false);
    });

    it("returns false for 'period' without to date", () => {
      expect(
        isStrictDateFilterValue({
          type: "period",
          from: "2024-01-01",
          to: undefined,
        }),
      ).toBe(false);
    });

    it("returns true for 'allTime' filter even with extra properties", () => {
      expect(
        isStrictDateFilterValue({
          type: "allTime",
          from: "2024-01-01",
          to: "2024-12-31",
        }),
      ).toBe(true);
    });
  });

  describe("dateFilterToQuery", () => {
    describe("for Norm document kind", () => {
      it("returns undefined for 'allTime' filter", () => {
        expect(
          dateFilterToQuery(
            { type: "allTime", from: undefined, to: undefined },
            DocumentKind.Norm,
          ),
        ).toBeUndefined();
      });

      it("returns correct query for 'currentlyInForce' filter", () => {
        const result = dateFilterToQuery(
          { type: "currentlyInForce", from: undefined, to: undefined },
          DocumentKind.Norm,
        );
        expect(result).toMatch(
          /^entry_into_force_date:<\d{4}-\d{2}-\d{2} AND \(\(expiry_date:>\d{4}-\d{2}-\d{2}\) OR \(NOT _exists_:expiry_date\)\)$/,
        );
      });

      it("returns correct query for 'specificDate' filter", () => {
        expect(
          dateFilterToQuery(
            { type: "specificDate", from: "2024-06-15", to: undefined },
            DocumentKind.Norm,
          ),
        ).toBe(
          "entry_into_force_date:<2024-06-15 AND ((expiry_date:>2024-06-15) OR (NOT _exists_:expiry_date))",
        );
      });

      it("returns correct query for 'period' filter", () => {
        expect(
          dateFilterToQuery(
            { type: "period", from: "2024-01-01", to: "2024-12-31" },
            DocumentKind.Norm,
          ),
        ).toBe(
          "((expiry_date:>=2024-01-01 OR (NOT _exists_:expiry_date))) AND (entry_into_force_date:<=2024-12-31)",
        );
      });

      it("throws error for 'specificDate' without from date", () => {
        expect(() =>
          dateFilterToQuery(
            { type: "specificDate", from: undefined, to: undefined },
            DocumentKind.Norm,
          ),
        ).toThrow("Missing 'from' date in filter type specificDate");
      });

      it("throws error for 'period' without from date", () => {
        expect(() =>
          dateFilterToQuery(
            { type: "period", from: undefined, to: "2024-12-31" },
            DocumentKind.Norm,
          ),
        ).toThrow("Missing 'from' or 'to' date in filter type period");
      });
    });

    describe("for CaseLaw document kind", () => {
      it("returns undefined for 'allTime' filter", () => {
        expect(
          dateFilterToQuery(
            { type: "allTime", from: undefined, to: undefined },
            DocumentKind.CaseLaw,
          ),
        ).toBeUndefined();
      });

      it("returns undefined for 'currentlyInForce' filter", () => {
        expect(
          dateFilterToQuery(
            { type: "currentlyInForce", from: undefined, to: undefined },
            DocumentKind.CaseLaw,
          ),
        ).toBeUndefined();
      });

      it("returns correct query for 'specificDate' filter", () => {
        expect(
          dateFilterToQuery(
            { type: "specificDate", from: "2024-06-15", to: undefined },
            DocumentKind.CaseLaw,
          ),
        ).toBe("DATUM:2024-06-15");
      });

      it("returns correct query for 'period' filter", () => {
        expect(
          dateFilterToQuery(
            { type: "period", from: "2024-01-01", to: "2024-12-31" },
            DocumentKind.CaseLaw,
          ),
        ).toBe("DATUM:[2024-01-01 TO 2024-12-31]");
      });

      it("throws error for 'specificDate' without from date", () => {
        expect(() =>
          dateFilterToQuery(
            { type: "specificDate", from: undefined, to: undefined },
            DocumentKind.CaseLaw,
          ),
        ).toThrow("Missing 'from' date in filter type specificDate");
      });

      it("throws error for 'period' without from date", () => {
        expect(() =>
          dateFilterToQuery(
            { type: "period", from: undefined, to: "2024-12-31" },
            DocumentKind.CaseLaw,
          ),
        ).toThrow("Missing 'from' or 'to' date in filter type period");
      });
    });

    describe("for Literature document kind", () => {
      it("returns undefined for 'allTime' filter", () => {
        expect(
          dateFilterToQuery(
            { type: "allTime", from: undefined, to: undefined },
            DocumentKind.Literature,
          ),
        ).toBeUndefined();
      });

      it("returns undefined for 'currentlyInForce' filter", () => {
        expect(
          dateFilterToQuery(
            { type: "currentlyInForce", from: undefined, to: undefined },
            DocumentKind.Literature,
          ),
        ).toBeUndefined();
      });

      it("returns undefined for 'specificDate' filter", () => {
        expect(
          dateFilterToQuery(
            { type: "specificDate", from: "2024", to: undefined },
            DocumentKind.Literature,
          ),
        ).toBeUndefined();
      });

      it("returns correct query for 'period' filter", () => {
        expect(
          dateFilterToQuery(
            { type: "period", from: "2020", to: "2024" },
            DocumentKind.Literature,
          ),
        ).toBe("years_of_publication:[2020 TO 2024]");
      });

      it("throws error for 'period' without from date", () => {
        expect(() =>
          dateFilterToQuery(
            { type: "period", from: undefined, to: "2024" },
            DocumentKind.Literature,
          ),
        ).toThrow("Missing 'from' or 'to' date in filter type period");
      });
    });
  });
});
