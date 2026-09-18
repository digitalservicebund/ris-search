import { DocumentKind } from "~/types/api";
import { buildLuceneQuery, getLuceneSearchPath } from "./luceneSearch";

const allTime = { type: "allTime" } as const;
const currentlyInForce = { type: "currentlyInForce" } as const;

describe("luceneSearch", () => {
  describe("getLuceneSearchPath", () => {
    it.each([
      [DocumentKind.Norm, "/v1/document/lucene-search/legislation"],
      [DocumentKind.CaseLaw, "/v1/document/lucene-search/case-law"],
      [DocumentKind.Literature, "/v1/document/lucene-search/literature"],
      [
        DocumentKind.AdministrativeDirective,
        "/v1/document/lucene-search/administrative-directive",
      ],
      [DocumentKind.All, "/v1/document/lucene-search"],
    ])("returns the endpoint for %s", (documentKind, expected) => {
      expect(getLuceneSearchPath(documentKind)).toBe(expected);
    });
  });

  describe("buildLuceneQuery", () => {
    it("returns an empty string when there is nothing to filter by", () => {
      expect(buildLuceneQuery("", undefined, DocumentKind.Norm)).toBe("");
      expect(buildLuceneQuery("   ", allTime, DocumentKind.Norm)).toBe("");
    });

    it("wraps a user query in parentheses", () => {
      expect(
        buildLuceneQuery("test query", allTime, DocumentKind.CaseLaw),
      ).toBe("(test query)");
    });

    it("trims the user query", () => {
      expect(
        buildLuceneQuery("  test  ", undefined, DocumentKind.CaseLaw),
      ).toBe("(test)");
    });

    it("returns only the date filter when the user query is blank", () => {
      expect(buildLuceneQuery("", currentlyInForce, DocumentKind.Norm)).toMatch(
        /^\(entry_into_force_date:<\d{4}-\d{2}-\d{2} AND \(\(expiry_date:>\d{4}-\d{2}-\d{2}\) OR \(NOT _exists_:expiry_date\)\)\)$/,
      );
    });

    it("combines the user query and the date filter with AND", () => {
      expect(
        buildLuceneQuery(
          "test",
          { type: "specificDate", from: "2024-01-01" },
          DocumentKind.CaseLaw,
        ),
      ).toBe("(test) AND (DATUM:2024-01-01)");
    });

    it("drops a date filter that doesn't apply to the document kind", () => {
      expect(
        buildLuceneQuery("test", currentlyInForce, DocumentKind.CaseLaw),
      ).toBe("(test)");
    });

    it("throws for unsupported filter types", () => {
      expect(() =>
        buildLuceneQuery(
          "",
          { type: "before", to: "2024-01-01" },
          DocumentKind.Norm,
        ),
      ).toThrow("Attempted to convert unsupported filter type before to query");
    });
  });
});
