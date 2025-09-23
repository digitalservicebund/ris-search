import { mockNuxtImport, mountSuspended } from "@nuxt/test-utils/runtime";
import {
  type DOMWrapper,
  mount,
  type VueWrapper,
  RouterLinkStub,
} from "@vue/test-utils";
import dayjs from "dayjs";
import { expect, it } from "vitest";
import CaseLawActionsMenu from "~/components/ActionMenu/CaseLawActionsMenu.vue";
import CaseLawPage from "~/pages/case-law/[documentNumber]/index.vue";
import type { CaseLaw } from "~/types";

const { useFetchMock, useHeadMock } = vi.hoisted(() => ({
  useFetchMock: vi.fn().mockImplementation(async (url: string) => {
    if (url.includes("html")) {
      return {
        data: ref(htmlData),
        status: ref("success"),
      };
    } else {
      return {
        data: ref(caseLawTestData),
        status: ref("success"),
      };
    }
  }),
  useHeadMock: vi.fn(),
}));

mockNuxtImport("useFetch", async () => {
  return useFetchMock;
});
mockNuxtImport("useHead", async () => {
  return useHeadMock;
});

const caseLawTestData: CaseLaw = {
  "@id": "123",
  "@type": "Decision",
  deviatingDocumentNumber: [],
  encoding: [
    {
      "@type": "DecisionObject",
      "@id": "/v1/case-law/12345/zip",
      contentUrl: "/v1/case-law/12345.zip",
      encodingFormat: "application/zip",
      inLanguage: "de",
    },
  ],
  inLanguage: "",
  keywords: [],
  documentNumber: "12345",
  fileNumbers: ["123", "456"],
  caseFacts: "Sample case facts",
  tenor: "Sample tenor",
  decisionGrounds: "Sample decision grounds",
  headnote: "Sample headnote",
  courtName: "Sample court",
  location: "Sample location",
  decisionDate: "2023-01-01",
  documentType: "Sample type",
  decisionName: ["Sample decision"],
  ecli: "Sample ecli",
  headline: "(Sample headline)",
  judicialBody: "Sample judicial body",
  guidingPrinciple: "Sample guiding principle",
  grounds: "Sample grounds",
};

const htmlData =
  '<!DOCTYPE HTML><html><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></head><body><div id="header"><h1 id="title"><p>(Sample headline)</p></h1></div><h2 id="leitsatz">Leitsatz</h2><h2 id="orientierungssatz">Orientierungssatz</h2><h2 id="tatbestand">Tatbestand</h2><h2 id="tenor">Tenor</h2><h2 id="entscheidungsgruende">Entscheidungsgründe</h2><h2 id="gruende">Gründe</h2></body></html>';

const expectedRenderedHeadline = "Sample headline";

const unavailablePlaceholder = "nicht vorhanden";

function findDefinitions(wrapper: VueWrapper): Record<string, string> {
  const definitions: Record<string, string> = {};
  wrapper.findAll("dt").forEach((dt: DOMWrapper<HTMLElement>) => {
    const key = dt.text();
    const value = dt.element.nextSibling?.textContent;
    if (key && value) {
      definitions[key] = value;
    }
  });
  return definitions;
}

describe("case law single view page", async () => {
  const router = useRouter();
  beforeAll(() => {
    router.addRoute({ path: "/", component: CaseLawPage });
  });

  beforeEach(() => {
    vi.clearAllMocks();
    useFetchMock.mockImplementation(async (url: string) => {
      if (url.includes("html")) {
        return { data: ref(htmlData), status: ref("success") };
      } else {
        return { data: ref(caseLawTestData), status: ref("success") };
      }
    });
  });

  it("displays Inhaltsverzeichnis correctly with links to anchors", async () => {
    const wrapper = await mountSuspended(CaseLawPage);

    const tocLinks = wrapper.findAll(".case-law h2");
    console.log(tocLinks);
    const expectedLinks = [
      { id: "leitsatz", title: "Leitsatz" },
      { id: "orientierungssatz", title: "Orientierungssatz" },
      { id: "tatbestand", title: "Tatbestand" },
      { id: "tenor", title: "Tenor" },
      { id: "entscheidungsgruende", title: "Entscheidungsgründe" },
      { id: "gruende", title: "Gründe" },
    ];

    expect(tocLinks.length).toBe(expectedLinks.length);
    expectedLinks.forEach((link, index) => {
      expect(tocLinks[index].text()).toBe(link.title);
    });
  });

  it("displays case law data correctly", async () => {
    const wrapper = await mountSuspended(CaseLawPage);

    const pageHeader = wrapper.find("h1");
    expect(pageHeader.exists()).toBe(true);
    expect(pageHeader.text()).toBe(expectedRenderedHeadline);

    expect(wrapper.find("#court_name").text()).toContain(
      caseLawTestData.courtName,
    );
    expect(wrapper.find("#document_type").text()).toContain(
      caseLawTestData.documentType,
    );
    expect(wrapper.find("#decision_date").text()).toContain(
      dayjs(caseLawTestData.decisionDate).format("DD.MM.YYYY"),
    );
    expect(wrapper.find("#file_numbers").text()).toContain(
      caseLawTestData.fileNumbers.join(", "),
    );

    const definitions = findDefinitions(wrapper);
    expect(definitions["Spruchkörper:"]).toBe(caseLawTestData.judicialBody);
    expect(definitions["ECLI:"]).toBe(caseLawTestData.ecli);
    expect(definitions["Normen:"]).toBe(unavailablePlaceholder);
    expect(definitions["Entscheidungsname:"]).toBe(
      caseLawTestData.decisionName.join(", "),
    );
    expect(definitions["Vorinstanz:"]).toBe(unavailablePlaceholder);

    expect(wrapper.find("#orientierungssatz").text()).toContain(
      "Orientierungssatz",
    );
    expect(wrapper.find("#tenor").text()).toContain("Tenor");
    expect(wrapper.find("#tatbestand").text()).toContain("Tatbestand");
    expect(wrapper.find("#entscheidungsgruende").text()).toContain(
      "Entscheidungsgründe",
    );
    expect(wrapper.find("#leitsatz").text()).toContain("Leitsatz");
    expect(wrapper.find("#gruende").text()).toContain("Gründe");
  });

  it('displays "Nicht verfügbar" for empty case law data', async () => {
    const caseLawTestData: CaseLaw = {
      "@id": "",
      "@type": "Decision",
      deviatingDocumentNumber: [],
      encoding: [],
      inLanguage: "",
      keywords: [],
      documentNumber: "",
      fileNumbers: [],
      caseFacts: "",
      tenor: "",
      decisionGrounds: "",
      headnote: "",
      courtName: "",
      location: "",
      decisionDate: "",
      documentType: "",
      decisionName: [""],
      ecli: "",
      headline: "",
      judicialBody: "",
    };

    const htmlData = "";

    useFetchMock.mockImplementation(async (url: string) => {
      if (url.includes("html")) {
        return {
          data: ref(htmlData),
          status: ref("success"),
        };
      } else {
        return {
          data: ref(caseLawTestData),
          status: ref("success"),
        };
      }
    });

    const wrapper = await mountSuspended(CaseLawPage);

    const pageHeader = wrapper.find("h1");
    expect(pageHeader.exists()).toBe(true);
    expect(pageHeader.text()).toBe("Titelzeile nicht vorhanden");
    expect(pageHeader.attributes("class")).toContain("text-gray-900");

    expect(wrapper.find("#court_name").text()).toBe(unavailablePlaceholder);
    expect(wrapper.find("#document_type").text()).toBe(unavailablePlaceholder);
    expect(wrapper.find("#decision_date").text()).toBe(unavailablePlaceholder);
    expect(wrapper.find("#file_numbers").text()).toBe(unavailablePlaceholder);

    const definitions = findDefinitions(wrapper);

    expect(definitions["Spruchkörper:"]).toBe(unavailablePlaceholder);
    expect(definitions["ECLI:"]).toBe(unavailablePlaceholder);
    expect(definitions["Normen:"]).toBe(unavailablePlaceholder);
    expect(definitions["Entscheidungsname:"]).toBe(unavailablePlaceholder);
    expect(definitions["Vorinstanz:"]).toBe(unavailablePlaceholder);

    expect(wrapper.find("#headnote").exists()).toBe(false);
    expect(wrapper.find("#tenor").exists()).toBe(false);
    expect(wrapper.find("#case_facts").exists()).toBe(false);
    expect(wrapper.find("#decision_grounds").exists()).toBe(false);
  });

  it("displays 404 error page when case law is not found", async () => {
    useFetchMock.mockImplementation(async () => {
      return {
        data: ref(null),
        status: ref("success"),
      };
    });

    const wrapper = mount(CaseLawPage);

    const pageHeader = wrapper.find("h1");
    expect(pageHeader.exists()).toBe(false);
  });

  it("sets meta title and description", async () => {
    await mountSuspended(CaseLawPage);
    await nextTick();

    expect(useHeadMock).toHaveBeenCalled();

    const headArg = useHeadMock.mock.calls.at(-1)?.[0];
    expect(headArg).toBeTruthy();

    const title = headArg.title as string;
    const date = dayjs(caseLawTestData.decisionDate).format("DD.MM.YYYY");

    expect(title).toContain(`${caseLawTestData.courtName}:`);
    expect(title).toContain(caseLawTestData.documentType);
    expect(title).toContain(`vom ${date}`);
    expect(title).toContain(caseLawTestData.fileNumbers[0]);
    expect(title.length).toBeLessThanOrEqual(55);

    const description = headArg.meta.find(
      (m: { name?: string; property?: string; content?: string }) =>
        m.name === "description",
    )?.content as string;

    expect(description).toContain("Sample guiding principle");
  });

  it("falls back to first <p> from HTML when guidingPrinciple is missing", async () => {
    useFetchMock.mockImplementation(async (url: string) => {
      if (url.includes("html")) {
        return { data: ref(htmlData), status: ref("success") };
      }
      return {
        data: ref({ ...caseLawTestData, guidingPrinciple: "" }),
        status: ref("success"),
      };
    });

    await mountSuspended(CaseLawPage);
    await nextTick();

    const headArg = useHeadMock.mock.calls.at(-1)?.[0];
    const description = headArg.meta.find(
      (m: { name?: string; property?: string; content?: string }) =>
        m.name === "description",
    )?.content as string;

    expect(description).toContain("(Sample headline)");
    expect(description.length).toBeLessThanOrEqual(150);
  });

  it("displays zip link on the details tab", async () => {
    useFetchMock.mockImplementation(async (url: string) => {
      if (url.includes("html")) {
        return {
          data: ref(htmlData),
          status: ref("success"),
        };
      } else {
        return {
          data: ref(caseLawTestData),
          status: ref("success"),
        };
      }
    });

    const wrapper = await mountSuspended(CaseLawPage, {
      global: {
        stubs: {
          NuxtLink: RouterLinkStub,
        },
      },
    });

    const tabButton = wrapper.get("button[aria-label*='Details']");
    await tabButton.trigger("click");

    const zipLink = wrapper.get("[data-attr='xml-zip-view']");
    expect(zipLink).toBeTruthy();
    expect(zipLink.text()).toBe("12345 als ZIP herunterladen");
    expect(zipLink.attributes("href")).toBe("/api/v1/case-law/12345.zip");
  });

  it("passes caseLaw to the CaseLawActionsMenu", async () => {
    const wrapper = await mountSuspended(CaseLawPage);
    const caseLawActionsMenu = wrapper.getComponent(CaseLawActionsMenu);
    expect(caseLawActionsMenu.props("caseLaw")).toEqual(caseLawTestData);
  });
});
