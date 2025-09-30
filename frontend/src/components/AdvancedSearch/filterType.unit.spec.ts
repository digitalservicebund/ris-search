import { isFilterType } from "./filterType";

describe("filterType", () => {
  describe("isFilterType", () => {
    it("returns true if the value is a known filter type", () => {
      ["allTime", "period", "specificDate", "currentlyInForce"].forEach((i) =>
        expect(isFilterType(i)).toBe(true),
      );
    });

    it("returns false if the value is not a known filter type", () => {
      [undefined, "", "notAFilter"].forEach((i) =>
        expect(isFilterType(i as string)).toBe(false),
      );
    });
  });
});
