import { mockNuxtImport, mountSuspended } from "@nuxt/test-utils/runtime";
import type { VueWrapper } from "@vue/test-utils";
import { describe, it, expect, vi } from "vitest";
import Index from "./index.vue";
import NormActionsMenu from "~/components/ActionMenu/NormActionsMenu.vue";
import NormMetadataFields from "~/components/Norm/Metadatafields/NormMetadataFields.vue";
import type { NormContent } from "~/pages/norms/eli/[jurisdiction]/[agent]/[year]/[naturalIdentifier]/[pointInTime]/[version]/[language]/useNormData";
import type { LegislationWork } from "~/types";

const mocks = vi.hoisted(() => {
  return {
    useFetchNormContent: vi.fn(),
    useFetchNormArticleContent: vi.fn(),
  };
});

vi.mock("./useNormData", () => {
  return {
    useFetchNormContent: mocks.useFetchNormContent,
    useFetchNormArticleContent: mocks.useFetchNormArticleContent,
  };
});

vi.mock("./useNormActions", () => ({
  useNormActions: () => ({ actions: ref([{ key: "mockAction" }]) }),
}));

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
          "/eli/work-LEG12345/expression-LEG12345/manifestation-LEG12345/regelungstext-1.xml",
        inLanguage: "test",
      },
      {
        "@type": "LegislationObject",
        "@id": "id-zip",
        encodingFormat: "application/zip",
        contentUrl:
          "/eli/work-LEG12345/expression-LEG12345/manifestation-LEG12345.zip",
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
        RisBreadcrumb: true,
        NormTableOfContents: true,
        Accordion: true,
        IncompleteDataMessage: true,
        RisExpandableText: {
          template: '<div class="mock-expandable-text"><slot /></div>',
        },
        NormActionMenu: true,
        VersionWarningMessage: true,
      },
    },
  });
}

type StubbedComponent = {
  props: (value: string) => string;
};

function getBreadcrumbStub(wrapper: VueWrapper) {
  return wrapper.findComponent(
    "ris-breadcrumb-stub",
  ) as unknown as StubbedComponent;
}

function getDDElement(wrapper: VueWrapper, label: string): HTMLElement | null {
  const labelElement = wrapper
    .findAll("dt")
    .filter((element) => element.text() === label)[0];
  if (!labelElement) {
    return null;
  }
  let next = labelElement.element.nextSibling as HTMLElement | null;
  if (next?.localName !== "dd") {
    next = next?.nextSibling as HTMLElement | null; // skip in-between text node
  }
  if (next?.localName === "dd") {
    return next;
  }
  return null;
}

describe("index.vue", () => {
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
      Array.of({
        route: "/norms/eli/work-LEG12345",
        label: "abbreviation",
      }),
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
      Array.of({
        route: "/norms/eli/work-LEG12345",
        label: "abbreviation",
      }),
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
      Array.of({
        route: "/norms/eli/work-LEG12345",
        label: "abbreviation",
      }),
    );
  });

  it("passes metadata to the NormActionsMenu", async () => {
    mockMetadata();
    const wrapper = await mountComponent();
    const normActionsMenu = wrapper.getComponent(NormActionsMenu);
    expect(normActionsMenu.props("metadata")).toEqual(legislationWork);
  });

  it("shows metadata", async () => {
    mockMetadata();
    const wrapper = await mountComponent();

    const tabButton = wrapper.get("button[aria-label*='Details']");
    await tabButton.trigger("click");

    const ausfertigungsDatum = getDDElement(wrapper, "Ausfertigungsdatum:");
    expect(ausfertigungsDatum?.textContent).toBe("05.10.2024");

    const vollzitat = getDDElement(wrapper, "Vollzitat:");
    expect(vollzitat?.textContent).toBe("Sample Norm of 05. October 2024");

    const standElement = getDDElement(wrapper, "Stand:");
    expect(standElement?.textContent).toBe("Zuletzt geändert durch X");

    const standHinweisElement = getDDElement(wrapper, "Hinweis zum Stand:");
    expect(standHinweisElement?.textContent).toBe(
      "Änderung durch Y textlich nachgewiesen",
    );

    const besondererHinweisElement = getDDElement(
      wrapper,
      "Besonderer Hinweis:",
    );
    expect(besondererHinweisElement?.textContent).toBe(
      "Mit dieser Verordnung wird…",
    );

    const notes = getDDElement(wrapper, "Fußnoten:");
    expect(notes?.textContent).toBe("Notes");

    const zipDownload = getDDElement(wrapper, "Download:");
    expect(zipDownload?.querySelector("a")?.href).toBe(
      "http://localhost:3000/api/eli/work-LEG12345/expression-LEG12345/manifestation-LEG12345.zip",
    );
  });

  it("uses the abbreviation as meta title and sets up comprehensive meta tags", async () => {
    mockMetadata();
    await mountComponent();
    await nextTick();

    expect(useHeadMock).toHaveBeenCalled();

    const callArgs = useHeadMock.mock.calls[0][0];

    expect(callArgs.title.value).toBe(
      "abbreviation, Fassung vom 01.01.2024, Außer Kraft",
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
      content: "abbreviation, Fassung vom 01.01.2024, Außer Kraft",
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
      content: "abbreviation, Fassung vom 01.01.2024, Außer Kraft",
    });
    expect(metaTags).toContainEqual({
      name: "twitter:description",
      content: "alternateName",
    });
  });

  it("renders metadata fields", async () => {
    mockMetadata();
    const wrapper = await mountComponent();
    expect(wrapper.findComponent(NormMetadataFields).exists()).toBeTruthy();
  });
});
