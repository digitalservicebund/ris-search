import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { useTranslationSeo } from "./useTranslationSeo";

const { useSeo } = vi.hoisted(() => ({
  useSeo: vi.fn(),
}));

mockNuxtImport("useSeo", () => useSeo);

describe("useTranslationSeo", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("builds title, description and ogTitle", () => {
    useTranslationSeo({ name: "BGB", translationOfWork: "Civil Code" });

    expect(useSeo).toHaveBeenCalledWith(
      expect.objectContaining({
        title: "BGB, English translation",
        description: expect.stringContaining("BGB"),
        ogTitle: "BGB – English Translation",
      }),
    );
  });

  it("uses fallbacks if name is missing", () => {
    useTranslationSeo({ translationOfWork: "Civil Code" });

    expect(useSeo).toHaveBeenCalledWith(
      expect.objectContaining({
        title: "English translation",
        description: expect.stringContaining("Civil Code"),
        ogTitle: "Civil Code – English Translation",
      }),
    );
  });

  it("creates only fallback title if no values given", () => {
    useTranslationSeo({});

    expect(useSeo).toHaveBeenCalledWith(
      expect.objectContaining({
        title: "English translation",
        description: "",
        ogTitle: "",
      }),
    );
  });
});
