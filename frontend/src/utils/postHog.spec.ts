import { describe, expect, it } from "vitest";
import { parseConsentCookie } from "./postHog";

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
});
