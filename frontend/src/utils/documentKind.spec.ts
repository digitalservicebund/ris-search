import { isDocumentKind } from "./documentKind";

describe("documentKind", () => {
  describe("isDocumentKind", () => {
    it("returns true if the value is a known document kind", () => {
      for (const i of ["A", "R", "N"]) {
        expect(isDocumentKind(i)).toBe(true);
      }
    });

    it("returns false if the value is not a known document kind", () => {
      for (const i of [undefined, "", "ZZZ"]) {
        expect(isDocumentKind(i as string)).toBe(false);
      }
    });
  });
});
