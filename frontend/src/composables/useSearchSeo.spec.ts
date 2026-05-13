import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { ref } from "vue";
import { DocumentKind } from "~/types/api";
import { useSearchSeo } from "./useSearchSeo";

const { useSeo } = vi.hoisted(() => ({
  useSeo: vi.fn(),
}));

mockNuxtImport("useSeo", () => useSeo);

describe("useSearchSeo", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("passes description and ogTitle to useSeo", () => {
    useSearchSeo({
      query: ref("Mietrecht"),
      documentKind: ref(DocumentKind.All),
      pageIndex: ref(0),
      searchType: "Suche",
      description: "Some description",
      ogTitle: "OG Title",
    });

    expect(useSeo).toHaveBeenCalledOnce();
    expect(useSeo).toHaveBeenCalledWith(
      expect.objectContaining({
        description: "Some description",
        ogTitle: "OG Title",
      }),
    );
  });

  describe("title construction", () => {
    it.each([
      [DocumentKind.All, ""],
      [DocumentKind.Norm, "Gesetze & Verordnungen, "],
      [DocumentKind.CaseLaw, "Gerichtsentscheidungen, "],
      [DocumentKind.Literature, "Literaturnachweise, "],
      [DocumentKind.AdministrativeDirective, "Verwaltungsvorschriften, "],
    ])(
      "builds title for document kind %s, empty query and page 0",
      (documentKind, documentKindLabel) => {
        useSearchSeo({
          query: ref(undefined),
          documentKind: ref(documentKind),
          pageIndex: ref(0),
          searchType: "Suche",
          description: "Foo",
          ogTitle: "bar",
        });

        const capturedTitle = vi.mocked(useSeo).mock.lastCall?.[0].title;
        expect(capturedTitle?.value).toEqual(`${documentKindLabel}Suche`);
      },
    );

    it("query takes precedence over document kind", () => {
      useSearchSeo({
        query: ref("BGB"),
        documentKind: ref(DocumentKind.CaseLaw),
        pageIndex: ref(0),
        searchType: "Erweiterte Suche",
        description: "Foo",
        ogTitle: "bar",
      });

      const capturedTitle = vi.mocked(useSeo).mock.lastCall?.[0].title;
      expect(capturedTitle?.value).toEqual("Erweiterte Suche, BGB");
    });

    it("appends page number starting from page 2", () => {
      useSearchSeo({
        query: ref(undefined),
        documentKind: ref(DocumentKind.All),
        pageIndex: ref(1),
        searchType: "Suche",
        description: "Foo",
        ogTitle: "bar",
      });

      const capturedTitle = vi.mocked(useSeo).mock.lastCall?.[0].title;
      expect(capturedTitle?.value).toEqual("Suche, Seite 2");
    });

    it("combines query and page number", () => {
      useSearchSeo({
        query: ref("FooBar"),
        documentKind: ref(DocumentKind.Norm),
        pageIndex: ref(3),
        searchType: "Suche",
        description: "Foo",
        ogTitle: "bar",
      });

      const capturedTitle = vi.mocked(useSeo).mock.lastCall?.[0].title;
      expect(capturedTitle?.value).toEqual("Suche, FooBar, Seite 4");
    });

    it.each([
      [DocumentKind.All, ""],
      [DocumentKind.Norm, "Gesetze & Verordnungen, "],
      [DocumentKind.CaseLaw, "Gerichtsentscheidungen, "],
      [DocumentKind.Literature, "Literaturnachweise, "],
      [DocumentKind.AdministrativeDirective, "Verwaltungsvorschriften, "],
    ])(
      "combines document kind and page number for searches with empty query",
      (documentKind, documentKindLabel) => {
        useSearchSeo({
          query: ref(undefined),
          documentKind: ref(documentKind),
          pageIndex: ref(3),
          searchType: "Suche",
          description: "Foo",
          ogTitle: "bar",
        });

        const capturedTitle = vi.mocked(useSeo).mock.lastCall?.[0].title;
        expect(capturedTitle?.value).toEqual(
          `${documentKindLabel}Suche, Seite 4`,
        );
      },
    );

    it("title reacts to changes in query, documentKind, and pageIndex", () => {
      const query = ref<string | undefined>("BGB");
      const documentKind = ref<DocumentKind>(DocumentKind.Norm);
      const pageIndex = ref(0);

      useSearchSeo({
        query,
        documentKind,
        pageIndex,
        searchType: "Erweiterte Suche",
        description: "Foo",
        ogTitle: "bar",
      });

      const capturedTitle = vi.mocked(useSeo).mock.lastCall?.[0].title;

      expect(capturedTitle?.value).toEqual("Erweiterte Suche, BGB");

      query.value = "StGB";
      expect(capturedTitle?.value).toEqual("Erweiterte Suche, StGB");

      query.value = undefined;
      expect(capturedTitle?.value).toEqual(
        "Gesetze & Verordnungen, Erweiterte Suche",
      );

      documentKind.value = DocumentKind.CaseLaw;
      expect(capturedTitle?.value).toEqual(
        "Gerichtsentscheidungen, Erweiterte Suche",
      );

      pageIndex.value = 3;
      expect(capturedTitle?.value).toEqual(
        "Gerichtsentscheidungen, Erweiterte Suche, Seite 4",
      );
    });
  });
});
