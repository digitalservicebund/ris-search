import { describe, expect, it } from "vitest";
import { shouldSuppressSentryEvent } from "~/utils/sentry";

function serializedPayloadError(status: number) {
  return {
    data: { path: "/case-law/KVRE460072401" },
    error: "true",
    message: `Page not found: /case-law/KVRE460072401`,
    status,
    statusCode: status,
    statusMessage: `Page not found: /case-law/KVRE460072401`,
    statusText: `Page not found: /case-law/KVRE460072401`,
    url: "/case-law/KVRE460072401",
  };
}

describe("sentry", () => {
  describe("shouldSuppressSentryEvent", () => {
    it("suppresses client errors created via createError", () => {
      expect(shouldSuppressSentryEvent(createError({ status: 404 }))).toBe(
        true,
      );
    });

    it("reports server errors created via createError", () => {
      expect(shouldSuppressSentryEvent(createError({ status: 500 }))).toBe(
        false,
      );
    });

    it("suppresses client errors that lost their Nuxt error marker", () => {
      expect(shouldSuppressSentryEvent(serializedPayloadError(404))).toBe(true);
    });

    it("reports server errors that lost their Nuxt error marker", () => {
      expect(shouldSuppressSentryEvent(serializedPayloadError(500))).toBe(
        false,
      );
    });

    it("reports errors that are not Nuxt errors, even with a client error status", () => {
      const fetchError = Object.assign(new Error("Not Found"), {
        status: 404,
        statusCode: 404,
      });

      expect(shouldSuppressSentryEvent(fetchError)).toBe(false);
    });

    it.each([
      [299, false],
      [300, true],
      [499, true],
      [500, false],
    ])("handles the status %i at the range boundaries", (status, expected) => {
      expect(shouldSuppressSentryEvent({ status })).toBe(expected);
    });

    it("falls back to statusCode when status is missing", () => {
      expect(shouldSuppressSentryEvent({ statusCode: 404 })).toBe(true);
      expect(shouldSuppressSentryEvent({ statusCode: 500 })).toBe(false);
    });

    it("suppresses aborted requests", () => {
      expect(
        shouldSuppressSentryEvent(
          new DOMException("The user aborted a request.", "AbortError"),
        ),
      ).toBe(true);
    });

    it("suppresses aborted requests wrapped in another error", () => {
      const fetchError = new Error('[GET] "/v1/document": <no response>', {
        cause: new DOMException("The user aborted a request.", "AbortError"),
      });

      expect(shouldSuppressSentryEvent(fetchError)).toBe(true);
    });

    it("reports errors with a circular cause chain and no abort error", () => {
      const error: Error & { cause?: unknown } = new Error("Something failed");
      error.cause = error;

      expect(shouldSuppressSentryEvent(error)).toBe(false);
    });

    it.each([undefined, null, "404", 404, {}, { status: "not a status" }])(
      "reports %o, which carries no usable status",
      (error) => {
        expect(shouldSuppressSentryEvent(error)).toBe(false);
      },
    );
  });
});
