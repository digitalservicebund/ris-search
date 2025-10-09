import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { describe, it, expect, vi, beforeEach } from "vitest";
import { useStaticPageSeo } from "./useStaticPageSeo";
import { staticPageSeo } from "~/i18n/staticPageSeo";
import type { StaticPage } from "~/i18n/staticPageSeo";
import {
  TEST_URL,
  createExpectedHeadCall,
} from "~/utils/testing/seoTestHelpers";

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

      useStaticPageSeo(key);

      expect(useHead).toHaveBeenCalledWith(
        createExpectedHeadCall(entry.title, entry.description, TEST_URL),
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

      expect(useRequestURL).toHaveBeenCalled();
      expect(useHead).toHaveBeenCalledWith(
        createExpectedHeadCall(
          staticPageSeo.startseite.title,
          staticPageSeo.startseite.description,
          href,
        ),
      );
    });
  });
});
