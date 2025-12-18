import { mockNuxtImport, mountSuspended } from "@nuxt/test-utils/runtime";
import type { VueWrapper } from "@vue/test-utils";
import { describe, it, expect, vi } from "vitest";
import Index from "./index.vue";
import Breadcrumbs from "~/components/Breadcrumbs.vue";
import NormActionMenu from "~/components/documents/actionMenu/NormActionMenu.vue";
import Metadata from "~/components/Metadata.vue";
import type { NormContent } from "~/composables/useNormData";
import type { LegislationWork } from "~/types";

const mocks = vi.hoisted(() => {
  return {
    useFetchNormContent: vi.fn(),
    useFetchNormArticleContent: vi.fn(),
    usePrivateFeaturesFlag: vi.fn().mockReturnValue(false),
  };
});

vi.mock("~/composables/useNormData", () => {
  return {
    useFetchNormContent: mocks.useFetchNormContent,
    useFetchNormArticleContent: mocks.useFetchNormArticleContent,
  };
});

vi.mock("~/composables/usePrivateFeaturesFlag", () => {
  return { usePrivateFeaturesFlag: mocks.usePrivateFeaturesFlag };
});

const { useHeadMock } = vi.hoisted(() => {
  return {
    useHeadMock: vi.fn(),
  };
});

mockNuxtImport("useHead", () => useHeadMock);

const legislationWork: LegislationWork = {
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
    hasPart: [],
    legislationIdentifier: "eli/work-LEG12345/expression-LEG12345",
    encoding: [
      {
        "@type": "LegislationObject",
        "@id": "id-xml",
        encodingFormat: "application/xml",
        contentUrl:
          "/v1/legislation/eli/work-LEG12345/expression-LEG12345/manifestation-LEG12345/regelungstext-1.xml",
        inLanguage: "test",
      },
      {
        "@type": "LegislationObject",
        "@id": "id-zip",
        encodingFormat: "application/zip",
        contentUrl:
          "/v1/legislation/eli/work-LEG12345/expression-LEG12345/manifestation-LEG12345.zip",
        inLanguage: "test",
      },
    ],
    tableOfContents: [],
    temporalCoverage: "2024-01-01/2024-12-31",
    legislationLegalForce: "InForce",
  },
};

const mockData: NormContent = {
  legislationWork,
  html: "",
  htmlParts: {
    officialToc: undefined,
    heading: `<div class="titel">Sample Norm</div>`,
    headingAuthorialNotes: `<div class="fussnoten">Footnote content</div>`,
    headingNotes: `<div class="akn-notes">Notes</div>`,
    vollzitat: "Sample Norm of 05. October 2024",
    standangaben: ["Zuletzt geändert durch X"],
    standangabenHinweis: ["Änderung durch Y textlich nachgewiesen"],
    prefaceContainer: "Mit dieser Verordnung wird…",
  },
};

function mockMetadata(value: typeof mockData = mockData) {
  mocks.useFetchNormContent.mockResolvedValueOnce({
    data: {
      value,
    },
    error: { value: {} },
    status: { value: "success" },
  });
}

function mountComponent() {
  return mountSuspended(Index, {
    global: {
      stubs: {
        Breadcrumbs: true,
        NormTableOfContents: true,
        Accordion: true,
        IncompleteDataMessage: true,
        RisExpandableText: {
          template: '<div class="mock-expandable-text"><slot /></div>',
        },
        VersionWarningMessage: true,
      },
    },
  });
}

type StubbedComponent = {
  props: (value: string) => string;
};

function getBreadcrumbStub(wrapper: VueWrapper) {
  return wrapper.findComponent(Breadcrumbs) as unknown as StubbedComponent;
}

describe("index", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    vi.restoreAllMocks();
  });
  it("shows the alternateName title if present", async () => {
    mockMetadata();
    const wrapper = await mountComponent();
    expect(wrapper.get(".ris-heading3-regular").text()).toBe("alternateName");
    expect(wrapper.find(".titel").text()).toBe("Sample Norm");
    expect(getBreadcrumbStub(wrapper).props("items")).toStrictEqual(
      Array.of(
        {
          label: "Gesetze & Verordnungen",
          route: "/search?category=N",
        },
        {
          label: "abbreviation",
          route: "/norms/eli/work-LEG12345",
        },
      ),
    );
  });

  it("uses the alternateName as title if the name is not present", async () => {
    mockMetadata({
      ...mockData,
      legislationWork: {
        ...legislationWork,
        name: "",
      },
      htmlParts: {
        ...mockData.htmlParts,
        heading: "",
      },
    });
    const wrapper = await mountComponent();
    expect(wrapper.find(".ris-heading2-regular").exists()).toBe(false);
    expect(wrapper.find(".titel").text()).toBe("alternateName");
    expect(getBreadcrumbStub(wrapper).props("items")).toStrictEqual(
      Array.of(
        {
          label: "Gesetze & Verordnungen",
          route: "/search?category=N",
        },
        {
          route: "/norms/eli/work-LEG12345",
          label: "abbreviation",
        },
      ),
    );
  });

  it("uses the abbreviation as title if the name and alternateName are not present", async () => {
    mockMetadata({
      ...mockData,
      legislationWork: { ...legislationWork, name: "", alternateName: "" },
      htmlParts: {
        ...mockData.htmlParts,
        heading: "",
      },
    });
    const wrapper = await mountComponent();
    expect(wrapper.find(".ris-heading2-regular").exists()).toBe(false);
    expect(wrapper.find(".titel").text()).toBe("abbreviation");
    expect(getBreadcrumbStub(wrapper).props("items")).toStrictEqual(
      Array.of(
        {
          label: "Gesetze & Verordnungen",
          route: "/search?category=N",
        },
        {
          route: "/norms/eli/work-LEG12345",
          label: "abbreviation",
        },
      ),
    );
  });

  it("has a different breadcrumb with PrivateFeatures", async () => {
    mocks.usePrivateFeaturesFlag.mockReturnValue(true);
    mockMetadata();
    const wrapper = await mountComponent();
    expect(getBreadcrumbStub(wrapper).props("items")).toStrictEqual(
      Array.of(
        {
          label: "Gesetze & Verordnungen",
          route: "/search?category=N",
        },
        {
          route: "/norms/eli/work-LEG12345",
          label: "abbreviation vom 01.01.2024",
        },
      ),
    );
  });

  it("passes metadata to the NormActionMenu", async () => {
    mockMetadata();
    const wrapper = await mountComponent();
    const normActionMenu = wrapper.getComponent(NormActionMenu);
    expect(normActionMenu.props("metadata")).toEqual(legislationWork);
  });

  // "shows metadata" test removed as this behavior is now covered by E2E tests

  it("uses the abbreviation as meta title and sets up comprehensive meta tags", async () => {
    mocks.usePrivateFeaturesFlag.mockReturnValue(true);
    mockMetadata();
    await mountComponent();
    await nextTick();
    expect(useHeadMock).toHaveBeenCalled();

    const callArgs = useHeadMock.mock.calls[0]?.[0];

    expect(callArgs.title.value).toBe(
      "abbreviation: Fassung vom 01.01.2024, Außer Kraft",
    );
    expect(callArgs.link.value).toEqual([
      { rel: "canonical", href: expect.any(String) },
    ]);
    expect(callArgs.meta.value).toHaveLength(7);

    const metaTags = callArgs.meta.value;
    expect(metaTags).toContainEqual({
      name: "description",
      content: "alternateName",
    });
    expect(metaTags).toContainEqual({
      property: "og:type",
      content: "article",
    });
    expect(metaTags).toContainEqual({
      property: "og:title",
      content: "abbreviation: Fassung vom 01.01.2024, Außer Kraft",
    });
    expect(metaTags).toContainEqual({
      property: "og:description",
      content: "alternateName",
    });
    expect(metaTags).toContainEqual({
      property: "og:url",
      content: expect.any(String),
    });
    expect(metaTags).toContainEqual({
      name: "twitter:title",
      content: "abbreviation: Fassung vom 01.01.2024, Außer Kraft",
    });
    expect(metaTags).toContainEqual({
      name: "twitter:description",
      content: "alternateName",
    });
  });

  it("renders metadata fields", async () => {
    mockMetadata();
    const wrapper = await mountComponent();
    expect(wrapper.findComponent(Metadata).exists()).toBeTruthy();
  });
});
