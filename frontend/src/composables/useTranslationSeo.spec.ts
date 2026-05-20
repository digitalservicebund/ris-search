import { beforeEach, describe, expect, it, vi } from "vitest";
import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { useTranslationSeo } from "./useTranslationSeo";

const { useSeo } = vi.hoisted(() => ({
  useSeo: vi.fn(),
}));

mockNuxtImport("useSeo", () => useSeo);

describe("useTranslationSeo", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe("title", () => {
    it("build title with name", () => {
      useTranslationSeo({ name: "BGB" });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({ title: "BGB, English translation" }),
      );
    });

    it("omits name when name is absent", () => {
      useTranslationSeo({});

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({ title: "English translation" }),
      );
    });

    it("omits name when name is empty string", () => {
      useTranslationSeo({ name: "" });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({ title: "English translation" }),
      );
    });
  });

  describe("description and ogTitle", () => {
    it("includes the name in the description when provided", () => {
      useTranslationSeo({ name: "BGB", translationOfWork: "Civil Code" });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({
          description: expect.stringContaining("BGB"),
          ogTitle: "BGB – English Translation",
        }),
      );
    });

    it("falls back to translationOfWork when name is absent", () => {
      useTranslationSeo({ translationOfWork: "Civil Code" });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({
          description: expect.stringContaining("Civil Code"),
          ogTitle: "Civil Code – English Translation",
        }),
      );
    });

    it("returns empty description when neither name nor translationOfWork is given", () => {
      useTranslationSeo({});

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({ description: "", ogTitle: "" }),
      );
    });
  });
});
