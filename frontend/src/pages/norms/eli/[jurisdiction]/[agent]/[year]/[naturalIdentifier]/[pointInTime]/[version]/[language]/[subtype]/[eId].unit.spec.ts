import { mockNuxtImport, mountSuspended } from "@nuxt/test-utils/runtime";
import { afterEach, describe, expect, it, vi } from "vitest";
import ArticlePage from "./[eId].vue";
import type { LegislationWork } from "~/types";
import type { NormArticleContent } from "./useNormData";

const headingInnerHtml = `<span class="akn-num">§ 1</span> <span class="akn-heading">Erster Paragraf<sup>1</sup></span>`;
const headingHtml = `<h2 class="einzelvorschrift">${headingInnerHtml}</h2>`;

export const legislationWork: LegislationWork = {
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
        eId: "article_eId",
        guid: "",
        name: "§ 1 Test Article",
        isActive: true,
        entryIntoForceDate: null,
        expiryDate: null,
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

const articleContent: NormArticleContent = {
  legislationWork,
  html: headingHtml,
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
    featureFlags: {
      showNormArticleStatus: vi.fn().mockReturnValue(true),
    },
  };
});

vi.mock("./useNormData", () => {
  return { useFetchNormArticleContent: mocks.useFetchNormArticleContent };
});

vi.mock("~/utils/config", () => {
  return { featureFlags: mocks.featureFlags };
});

mockNuxtImport("useRoute", () =>
  vi.fn().mockReturnValue({
    fullPath:
      "norms/eli/bund/bgbl-1/2000/s100/2000-01-01/1/deu/regelungstext-1/article_eId",
    params: {
      eId: "article_eId",
    },
  }),
);

const { useHeadMock } = vi.hoisted(() => {
  return {
    useHeadMock: vi.fn(),
  };
});

mockNuxtImport("useHead", () => useHeadMock);

function mountComponent() {
  return mountSuspended(ArticlePage, {
    global: {
      stubs: {
        RisBreadcrumb: true,
        NormTableOfContents: true,
        Accordion: true,
        Message: true,
      },
    },
  });
}

describe("[eId].vue", () => {
  afterEach(() => {
    vi.clearAllMocks();
  });
  it("shows the HTML title", async () => {
    const wrapper = await mountComponent();
    expect(wrapper.find(".ris-heading2-bold").html()).toBe(
      `<h2 class="ris-heading2-bold my-24 mb-24 inline-block">${headingInnerHtml}</h2>`,
    );
  });
  it("shows entry into force and expiry dates if enabled by flag", async () => {
    mocks.featureFlags.showNormArticleStatus.mockReturnValue(true);
    const wrapper = await mountComponent();
    await nextTick();
    const metadata = wrapper.get("div[data-testid='metadata']");
    expect(metadata.text()).toBe("Status" + "Nicht in Kraft");
  });
  it("hides date metadata if disabled by flag", async () => {
    mocks.featureFlags.showNormArticleStatus.mockReturnValueOnce(false);
    const wrapper = await mountComponent();
    const metadata = wrapper.find("div[data-testid='metadata']");
    expect(metadata.exists()).toBe(false);
  });

  it("shows the article name in page title", async () => {
    await mountComponent();
    await nextTick();
    expect(useHeadMock).toHaveBeenCalledWith({ title: "§ 1 Test Article" });
  });
});
