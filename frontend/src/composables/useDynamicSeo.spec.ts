// frontend/src/composables/useDynamicSeo.spec.ts
import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { describe, it, expect, vi, beforeEach } from "vitest";
import { computed } from "vue";
import { useDynamicSeo } from "./useDynamicSeo";

type MetaTag =
  | {
      name: "description" | "twitter:title" | "twitter:description";
      content: string | undefined;
    }
  | {
      property: "og:type" | "og:title" | "og:description" | "og:url";
      content: string | undefined;
    };

type CanonicalLink = { rel: "canonical"; href: string };

const { useHead, useRequestURL } = vi.hoisted(() => ({
  useHead: vi.fn(),
  useRequestURL: vi.fn(
    () => new URL("https://testphase.rechtsinformationen.bund.de/example"),
  ),
}));

mockNuxtImport("useHead", () => useHead);
mockNuxtImport("useRequestURL", () => useRequestURL);

describe("useDynamicSeo composable", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    useRequestURL.mockReturnValue(
      new URL("https://testphase.rechtsinformationen.bund.de/example"),
    );
  });

  it("sets correct meta tags with title and description", () => {
    const title = computed(() => "Test Title");
    const description = computed(() => "Test Description");

    useDynamicSeo({ title, description });

    expect(useHead).toHaveBeenCalledTimes(1);
    const headArgs = useHead.mock.calls[0][0];

    const canonicalLinks = headArgs.link.value as CanonicalLink[];
    const metaTags = headArgs.meta.value as MetaTag[];

    expect(canonicalLinks).toEqual([
      {
        rel: "canonical",
        href: "https://testphase.rechtsinformationen.bund.de/example",
      },
    ]);

    expect(metaTags).toEqual(
      expect.arrayContaining<MetaTag>([
        { name: "description", content: "Test Description" },
        { property: "og:type", content: "article" },
        { property: "og:title", content: "Test Title" },
        { property: "og:description", content: "Test Description" },
        {
          property: "og:url",
          content: "https://testphase.rechtsinformationen.bund.de/example",
        },
        { name: "twitter:title", content: "Test Title" },
        { name: "twitter:description", content: "Test Description" },
      ]),
    );
  });

  it("handles undefined title and description (filters out empty tags)", () => {
    const title = computed(() => undefined);
    const description = computed(() => undefined);

    useDynamicSeo({ title, description });

    const headArgs = useHead.mock.calls[0][0];
    const canonicalLinks = headArgs.link.value as CanonicalLink[];
    const metaTags = headArgs.meta.value as MetaTag[];

    expect(canonicalLinks).toEqual([
      {
        rel: "canonical",
        href: "https://testphase.rechtsinformationen.bund.de/example",
      },
    ]);

    expect(metaTags).toEqual(
      expect.arrayContaining<MetaTag>([
        { property: "og:type", content: "article" },
        {
          property: "og:url",
          content: "https://testphase.rechtsinformationen.bund.de/example",
        },
      ]),
    );
  });

  it("filters out empty meta tags", () => {
    const title = computed(() => "");
    const description = computed(() => "   ");

    useDynamicSeo({ title, description });

    const headArgs = useHead.mock.calls[0][0];
    const metaTags = headArgs.meta.value as MetaTag[];

    expect(metaTags).toEqual(
      expect.arrayContaining<MetaTag>([
        { property: "og:type", content: "article" },
        {
          property: "og:url",
          content: "https://testphase.rechtsinformationen.bund.de/example",
        },
      ]),
    );
  });

  describe("URL handling", () => {
    it.each([
      "https://testphase.rechtsinformationen.bund.de/custom-path",
      "https://testphase.rechtsinformationen.bund.de/page?param=value&other=test",
    ])("uses exact url for canonical and og:url (%s)", (href) => {
      useRequestURL.mockReturnValueOnce(new URL(href));

      const title = computed(() => "Test Title");
      const description = computed(() => "Test Description");

      useDynamicSeo({ title, description });

      const headArgs = useHead.mock.calls[0][0];
      const canonicalLinks = headArgs.link.value as CanonicalLink[];
      const metaTags = headArgs.meta.value as MetaTag[];

      expect(canonicalLinks).toEqual([{ rel: "canonical", href }]);
      expect(metaTags).toEqual(
        expect.arrayContaining<MetaTag>([
          { property: "og:url", content: href },
        ]),
      );
    });
  });
});
