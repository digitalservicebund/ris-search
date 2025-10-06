import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { describe, it, expect, vi, beforeEach } from "vitest";
import { useStaticPageSeo } from "./useStaticPageSeo";
import staticPageSeo from "~/i18n/staticPageSeo.json";

const { useHead, useRequestURL } = vi.hoisted(() => ({
  useHead: vi.fn(),
  useRequestURL: vi.fn(
    () => new URL("https://testphase.rechtsinformationen.bund.de/example"),
  ),
}));

mockNuxtImport("useHead", () => useHead);
mockNuxtImport("useRequestURL", () => useRequestURL);

describe("useStaticPageSeo composable", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    useRequestURL.mockReturnValue(
      new URL("https://testphase.rechtsinformationen.bund.de/example"),
    );
  });

  it.each(Object.keys(staticPageSeo) as (keyof typeof staticPageSeo)[])(
    "sets correct meta tags",
    (key) => {
      const entry = staticPageSeo[key];

      useStaticPageSeo(key);

      expect(useHead).toHaveBeenCalledWith(
        expect.objectContaining({
          title: entry.titel,
          link: [
            {
              rel: "canonical",
              href: "https://testphase.rechtsinformationen.bund.de/example",
            },
          ],
          meta: expect.arrayContaining([
            { name: "description", content: entry.beschreibung },
            { property: "og:title", content: entry.titel },
            { property: "og:description", content: entry.beschreibung },
            {
              property: "og:url",
              content: "https://testphase.rechtsinformationen.bund.de/example",
            },
            { name: "twitter:title", content: entry.titel },
            { name: "twitter:description", content: entry.beschreibung },
          ]),
        }),
      );
    },
  );
  describe("called with invalid page keys", () => {
    const invalidKeys = ["nonexistent-page", undefined, null, ""] as const;

    it.each(invalidKeys)(
      "returns early and does not call useHead for invalid entry",
      (invalidKey) => {
        // @ts-expect-error – intentionally invalid
        useStaticPageSeo(invalidKey);
        expect(useHead).not.toHaveBeenCalled();
      },
    );
  });

  describe("URL handling", () => {
    it.each([
      "https://testphase.rechtsinformationen.bund.de/custom-path",
      "https://testphase.rechtsinformationen.bund.de/page?param=value&other=test",
    ])("uses exact url for canonical and og:url", (href) => {
      useRequestURL.mockReturnValueOnce(new URL(href));

      useStaticPageSeo("startseite");

      expect(useRequestURL).toHaveBeenCalled();
      expect(useHead).toHaveBeenCalledWith(
        expect.objectContaining({
          link: [{ rel: "canonical", href }],
          meta: expect.arrayContaining([{ property: "og:url", content: href }]),
        }),
      );
    });
  });
});
