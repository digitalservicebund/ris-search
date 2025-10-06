import { isFilterType } from "./filterType";

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
});
