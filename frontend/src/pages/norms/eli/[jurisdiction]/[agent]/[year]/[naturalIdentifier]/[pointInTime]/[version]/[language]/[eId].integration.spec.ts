import { mockNuxtImport, mountSuspended } from "@nuxt/test-utils/runtime";
import { afterEach, describe, expect, it, vi } from "vitest";
import ArticlePage from "./[eId].vue";
import type { NormArticleContent } from "./useNormData";
import type { LegislationWork } from "~/types";

const headingInnerHtml = `<span class="akn-num">§ 1</span> <span class="akn-heading">Erster Paragraf<sup>1</sup></span>`;
const headingHtml = `<h2 class="einzelvorschrift">${headingInnerHtml}</h2>`;
const articleHtmlWithDescription = `${headingHtml}<p>Dies ist der erste Absatz des Artikels mit einer Beschreibung des Inhalts.</p>`;

export const legislationWork = (
  eId: string = "article_eId",
  name: string = "§ 1 Test Article",
  isActive: boolean = true,
  from: string = "2000-01-01",
  to: string = "2300-01-01",
): LegislationWork => {
  return {
    "@type": "Legislation",
    "@id": "id",
    name: "Sample Norm",
    legislationIdentifier: "eli/work-LEG12345",
    alternateName: "alternateName",
    abbreviation: "abbreviation",
    legislationDate: "2024-10-05",
    datePublished: "2024-10-07",
    isPartOf: {
      name: "The Official Gazette",
    },
    workExample: {
      "@id": "id/expression",
      "@type": "Legislation",
      hasPart: [
        {
          "@type": "Legislation",
          "@id": "id",
          eId: eId,
          guid: "",
          name: name,
          isActive: isActive,
          entryIntoForceDate: from,
          expiryDate: to,
          encoding: null,
        },
      ],
      legislationIdentifier: "eli/work-LEG12345/expression-LEG12345",
      encoding: [],
      tableOfContents: [],
      temporalCoverage: "../..",
      legislationLegalForce: "InForce",
    },
  };
};

const articleContent: NormArticleContent = {
  legislationWork: legislationWork(),
  html: articleHtmlWithDescription,
  articleHeading: headingInnerHtml,
};

const mocks = vi.hoisted(() => {
  return {
    useFetchNormArticleContent: vi.fn(() =>
      Promise.resolve({
        data: {
          value: articleContent,
        },
        error: { value: {} },
        status: { value: "success" },
      }),
    ),
    isPrototypeProfile: vi.fn().mockReturnValue(false),
  };
});

vi.mock("./useNormData", () => {
  return { useFetchNormArticleContent: mocks.useFetchNormArticleContent };
});

vi.mock("~/utils/config", () => {
  return { isPrototypeProfile: mocks.isPrototypeProfile };
});

const { useHeadMock } = vi.hoisted(() => {
  return {
    useHeadMock: vi.fn(),
  };
});

const { useRouteMock } = vi.hoisted(() => {
  return {
    useRouteMock: vi.fn().mockReturnValue({
      fullPath:
        "norms/eli/bund/bgbl-1/2000/s100/2000-01-01/1/deu/regelungstext-1/article_eId",
      params: {
        eId: "article_eId",
      },
    }),
  };
});

mockNuxtImport("useHead", () => useHeadMock);

mockNuxtImport("useRoute", () => useRouteMock);

function mountComponent() {
  return mountSuspended(ArticlePage, {
    global: {
      stubs: {
        RisBreadcrumb: true,
        NormTableOfContents: true,
        Accordion: true,
        Message: true,
        ArticleVersionWarning: true,
      },
    },
  });
}

describe("[eId].vue", () => {
  afterEach(() => {
    vi.clearAllMocks();
  });
  it("shows the article data properly on prototype", async () => {
    mocks.isPrototypeProfile.mockReturnValueOnce(true);
    const wrapper = await mountComponent();
    expect(wrapper.find(".ris-heading2-bold").html()).toBe(
      `<h2 class="ris-heading2-bold my-24 mb-24 inline-block">${headingInnerHtml}</h2>`,
    );
    const metadata = wrapper.find("div[data-testid='metadata']");
    expect(metadata.exists()).toBe(false);
  });
  it("shows entry into force and expiry dates if not prototype", async () => {
    mocks.isPrototypeProfile.mockReturnValue(false);
    const wrapper = await mountComponent();
    await nextTick();
    const metadataText = wrapper
      .get("div[data-testid='metadata']")
      .text()
      .replaceAll(" ", "");
    expect(metadataText).toContain(
      "Gültig ab 01.01.2000 Gültig bis 01.01.2300".replaceAll(" ", ""),
    );
  });
  it("sets up comprehensive meta tags for article page", async () => {
    await mountComponent();
    await nextTick();

    expect(useHeadMock).toHaveBeenCalled();

    const callArgs = useHeadMock.mock.calls?.at(0)?.at(0);

    expect(callArgs.title.value).toBe("abbreviation: § 1 Erster Paragraf1");
    expect(callArgs.link.value).toEqual([
      { rel: "canonical", href: expect.any(String) },
    ]);
    expect(callArgs.meta.value).toHaveLength(7);

    const metaTags = callArgs.meta.value;
    expect(metaTags).toContainEqual({
      name: "description",
      content:
        "Dies ist der erste Absatz des Artikels mit einer Beschreibung des Inhalts.",
    });
    expect(metaTags).toContainEqual({
      property: "og:type",
      content: "article",
    });
    expect(metaTags).toContainEqual({
      property: "og:title",
      content: "abbreviation: § 1 Erster Paragraf1",
    });
    expect(metaTags).toContainEqual({
      property: "og:description",
      content:
        "Dies ist der erste Absatz des Artikels mit einer Beschreibung des Inhalts.",
    });
    expect(metaTags).toContainEqual({
      property: "og:url",
      content: expect.any(String),
    });
    expect(metaTags).toContainEqual({
      name: "twitter:title",
      content: "abbreviation: § 1 Erster Paragraf1",
    });
    expect(metaTags).toContainEqual({
      name: "twitter:description",
      content:
        "Dies ist der erste Absatz des Artikels mit einer Beschreibung des Inhalts.",
    });
  });
});
