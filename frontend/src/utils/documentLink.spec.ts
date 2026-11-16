import { toDocumentLink } from "./documentLink";

describe("documentLink", () => {
  describe("toDocumentLink", () => {
    it("links to the document", () => {
      expect(
        toDocumentLink("BGH", "/gerichtsentscheidungen", "KORE1", "search"),
      ).toEqual({
        text: "BGH",
        to: {
          path: "/gerichtsentscheidungen/KORE1",
          query: { from: "search" },
        },
      });
    });

    it("returns a link without target if there is no document number", () => {
      expect(toDocumentLink("BGH", "/gerichtsentscheidungen")).toEqual({
        text: "BGH",
      });
    });

    it("returns undefined if there is no text", () => {
      expect(
        toDocumentLink(undefined, "/gerichtsentscheidungen", "KORE1"),
      ).toBeUndefined();
    });
  });
});
