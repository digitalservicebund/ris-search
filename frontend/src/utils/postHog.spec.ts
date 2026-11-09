import { describe, expect, it } from "vitest";
import { formatConsentCookie, parseConsentCookie } from "./postHog";

describe("postHog", () => {
  describe("parseConsentCookie", () => {
    it.each([true, 1, "true", "1", "yes"])(
      "returns granted for %j",
      (value) => {
        expect(parseConsentCookie(value)).toBe("granted");
      },
    );

    it.each([false, 0, "false", "0", "no"])(
      "returns denied for %j",
      (value) => {
        expect(parseConsentCookie(value)).toBe("denied");
      },
    );

    it.each([undefined, null, "", "maybe"])(
      "returns pending for %j",
      (value) => {
        expect(parseConsentCookie(value)).toBe("pending");
      },
    );
  });

  describe("formatConsentCookie", () => {
    it.each([
      [true, "1"],
      [false, "0"],
    ])("returns %j for %s", (isGranted, value) => {
      expect(formatConsentCookie(isGranted)).toBe(value);
    });

    it.each([true, false])("round-trips %s", (isGranted) => {
      const expected = isGranted ? "granted" : "denied";
      expect(parseConsentCookie(formatConsentCookie(isGranted))).toBe(expected);
    });
  });
});
