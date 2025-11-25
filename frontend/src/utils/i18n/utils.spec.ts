import { describe, it, expect } from "vitest";
import { isErrorCode } from "./utils";

describe("i18n/utils", () => {
  describe("isErrorCode", () => {
    it("returns true for valid error codes", () => {
      expect(isErrorCode("NETWORK_ERROR")).toBe(true);
    });

    it("returns false for invalid error codes", () => {
      expect(isErrorCode("INVALID_CODE")).toBe(false);
      expect(isErrorCode("")).toBe(false);
    });
  });
});
