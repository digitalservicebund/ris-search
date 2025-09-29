import { isDocumentKind } from "./documentKind";

describe("documentKind", () => {
  describe("isDocumentKind", () => {
    it("returns true if the value is a known document kind", () => {
      ["A", "R", "N"].forEach((i) => expect(isDocumentKind(i)).toBe(true));
    });

    it("returns false if the value is not a known document kind", () => {
      [undefined, "", "ZZZ"].forEach((i) =>
        expect(isDocumentKind(i as string)).toBe(false),
      );
    });
  });
});
