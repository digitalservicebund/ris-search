import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { describe, it, expect, vi, beforeEach } from "vitest";
import { useStaticPageSeo } from "./useStaticPageSeo";

const TEST_URL = "https://testphase.rechtsinformationen.bund.de/example";

const { useHead, useSeoMeta, useRequestURL } = vi.hoisted(() => ({
  useHead: vi.fn(),
  useSeoMeta: vi.fn(),
  useRequestURL: vi.fn(() => new URL(TEST_URL)),
}));

mockNuxtImport("useHead", () => useHead);
mockNuxtImport("useSeoMeta", () => useSeoMeta);
mockNuxtImport("useRequestURL", () => useRequestURL);

describe("useStaticPageSeo composable", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    useRequestURL.mockReturnValue(new URL(TEST_URL));
  });

  it("sets meta tags and canonical link", () => {
    useStaticPageSeo("Test Title", "Test Description");

    expect(useSeoMeta).toHaveBeenCalledWith(
      expect.objectContaining({
        title: "Test Title",
        description: "Test Description",
        ogType: "article",
        ogTitle: "Test Title",
        ogDescription: "Test Description",
        ogUrl: TEST_URL,
        twitterTitle: "Test Title",
        twitterDescription: "Test Description",
      }),
    );

    expect(useHead).toHaveBeenCalledWith({
      link: [{ rel: "canonical", href: TEST_URL }],
    });
  });

  it("uses ogTitle when provided instead of falling back to title", () => {
    useStaticPageSeo("Test Title", "Test Description", "Custom OG Title");

    expect(useSeoMeta).toHaveBeenCalledWith(
      expect.objectContaining({
        ogTitle: "Custom OG Title",
        twitterTitle: "Custom OG Title",
      }),
    );
  });
});
