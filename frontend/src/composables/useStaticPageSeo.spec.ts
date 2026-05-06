import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { describe, it, expect, vi, beforeEach } from "vitest";
import { useStaticPageSeo } from "./useStaticPageSeo";
import { staticPageSeo } from "~/utils/i18n/staticPageSeo";
import type { StaticPage } from "~/utils/i18n/staticPageSeo";

const TEST_URL = "https://testphase.rechtsinformationen.bund.de/example";

const { useHead, useRequestURL } = vi.hoisted(() => ({
  useHead: vi.fn(),
  useRequestURL: vi.fn(() => new URL(TEST_URL)),
}));

mockNuxtImport("useHead", () => useHead);
mockNuxtImport("useRequestURL", () => useRequestURL);

describe("useStaticPageSeo composable", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    useRequestURL.mockReturnValue(new URL(TEST_URL));
  });

  it.each(Object.keys(staticPageSeo) as StaticPage[])(
    "sets correct meta tags",
    (key) => {
      const entry = staticPageSeo[key];
      const ogTitle = entry.ogTitle ?? entry.title;

      useStaticPageSeo(key);

      expect(useHead).toHaveBeenCalledWith(
        expect.objectContaining({
          title: entry.title,
          link: [{ rel: "canonical", href: TEST_URL }],
          meta: [
            { name: "description", content: entry.description },
            { property: "og:type", content: "article" },
            { property: "og:title", content: ogTitle },
            { property: "og:description", content: entry.description },
            { property: "og:url", content: TEST_URL },
            { name: "twitter:title", content: ogTitle },
            { name: "twitter:description", content: entry.description },
          ],
        }),
      );
    },
  );

  describe("URL handling", () => {
    it.each([
      "https://testphase.rechtsinformationen.bund.de/custom-path",
      "https://testphase.rechtsinformationen.bund.de/page?param=value&other=test",
    ])("uses exact url for canonical and og:url", (href) => {
      useRequestURL.mockReturnValueOnce(new URL(href));

      useStaticPageSeo("startseite");

      const entry = staticPageSeo.startseite;
      const ogTitle = entry.ogTitle ?? entry.title;

      expect(useRequestURL).toHaveBeenCalled();
      expect(useHead).toHaveBeenCalledWith(
        expect.objectContaining({
          title: entry.title,
          link: [{ rel: "canonical", href }],
          meta: [
            { name: "description", content: entry.description },
            { property: "og:type", content: "article" },
            { property: "og:title", content: ogTitle },
            { property: "og:description", content: entry.description },
            { property: "og:url", content: href },
            { name: "twitter:title", content: ogTitle },
            { name: "twitter:description", content: entry.description },
          ],
        }),
      );
    });
  });
});
