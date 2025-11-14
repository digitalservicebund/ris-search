import {
  mockNuxtImport,
  mountSuspended,
  renderSuspended,
} from "@nuxt/test-utils/runtime";
import userEvent from "@testing-library/user-event";
import { screen } from "@testing-library/vue";
import { mount } from "@vue/test-utils";
import dayjs from "dayjs";
import { expect, it, vi } from "vitest";
import { unref } from "vue";
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

  afterEach(() => {
    vi.clearAllMocks();
  });

  it("displays Inhaltsverzeichnis correctly with links to anchors", async () => {
    const wrapper = await mountSuspended(CaseLawPage);

    const tocLinks = wrapper.findAll(".case-law h2");
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
      expect(tocLinks[index]?.text()).toBe(link.title);
    });
  });

  it("displays case law metadata and text", async () => {
    await renderSuspended(CaseLawPage);

    expect(
      screen.getByRole("heading", { name: expectedRenderedHeadline }),
    ).toBeInTheDocument();

    // Metadata
    expect(screen.getByText("Gericht")).toBeInTheDocument();
    expect(screen.getByLabelText("Gericht")).toHaveTextContent("Sample court");

    expect(screen.getByText("Dokumenttyp")).toBeInTheDocument();
    expect(screen.getByLabelText("Dokumenttyp")).toHaveTextContent(
      "Sample type",
    );

    expect(screen.getByText("Entscheidungsdatum")).toBeInTheDocument();
    expect(screen.getByLabelText("Entscheidungsdatum")).toHaveTextContent(
      "01.01.2023",
    );

    expect(screen.getByText("Aktenzeichen")).toBeInTheDocument();
    expect(screen.getByLabelText("Aktenzeichen")).toHaveTextContent("123, 456");

    // Text content
    expect(
      screen.getByRole("heading", { name: "Orientierungssatz" }),
    ).toBeInTheDocument();

    expect(screen.getByRole("heading", { name: "Tenor" })).toBeInTheDocument();
    expect(
      screen.getByRole("heading", { name: "Tatbestand" }),
    ).toBeInTheDocument();
    expect(
      screen.getByRole("heading", { name: "Entscheidungsgründe" }),
    ).toBeInTheDocument();
    expect(
      screen.getByRole("heading", { name: "Leitsatz" }),
    ).toBeInTheDocument();
    expect(screen.getByRole("heading", { name: "Gründe" })).toBeInTheDocument();
  });

  it("displayes detailed information in the details tab", async () => {
    const user = userEvent.setup();
    await renderSuspended(CaseLawPage);

    await user.click(
      screen.getByRole("link", { name: "Details zur Gerichtsentscheidung" }),
    );

    expect(screen.getByText("Spruchkörper:")).toBeInTheDocument();
    expect(screen.getByLabelText("Spruchkörper:")).toHaveTextContent(
      "Sample judicial body",
    );

    expect(screen.getByText("ECLI:")).toBeInTheDocument();
    expect(screen.getByLabelText("ECLI:")).toHaveTextContent("Sample ecli");

    expect(screen.getByText("Normen:")).toBeInTheDocument();
    expect(screen.getByLabelText("Normen:")).toHaveTextContent(
      "nicht vorhanden",
    );

    expect(screen.getByText("Entscheidungsname:")).toBeInTheDocument();
    expect(screen.getByLabelText("Entscheidungsname:")).toHaveTextContent(
      "Sample decision",
    );

    expect(screen.getByText("Vorinstanz:")).toBeInTheDocument();
    expect(screen.getByLabelText("Vorinstanz:")).toHaveTextContent(
      "nicht vorhanden",
    );

    expect(screen.getByText("Download:")).toBeInTheDocument();
    expect(screen.getByLabelText("Download:")).toHaveTextContent(
      "12345 als ZIP herunterladen",
    );
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

    const user = userEvent.setup();
    await renderSuspended(CaseLawPage);

    const pageHeader = screen.getByRole("heading", {
      name: "Titelzeile nicht vorhanden",
    });
    expect(pageHeader).toBeInTheDocument();
    expect(pageHeader).toHaveClass("text-gray-900");

    // Metadata
    expect(screen.getByLabelText("Gericht")).toHaveTextContent("—");
    expect(screen.getByLabelText("Dokumenttyp")).toHaveTextContent("—");
    expect(screen.getByLabelText("Entscheidungsdatum")).toHaveTextContent("—");
    expect(screen.getByLabelText("Aktenzeichen")).toHaveTextContent("—");

    // Text content
    expect(
      screen.queryByRole("heading", { name: "Orientierungssatz" }),
    ).not.toBeInTheDocument();
    expect(
      screen.queryByRole("heading", { name: "Tenor" }),
    ).not.toBeInTheDocument();
    expect(
      screen.queryByRole("heading", { name: "Tatbestand" }),
    ).not.toBeInTheDocument();
    expect(
      screen.queryByRole("heading", { name: "Entscheidungsgründe" }),
    ).not.toBeInTheDocument();
    expect(
      screen.queryByRole("heading", { name: "Leitsatz" }),
    ).not.toBeInTheDocument();
    expect(
      screen.queryByRole("heading", { name: "Gründe" }),
    ).not.toBeInTheDocument();

    await user.click(
      screen.getByRole("link", { name: "Details zur Gerichtsentscheidung" }),
    );

    // Details tab
    expect(screen.getByLabelText("Spruchkörper:")).toHaveTextContent(
      "nicht vorhanden",
    );

    expect(screen.getByLabelText("ECLI:")).toHaveTextContent("nicht vorhanden");

    expect(screen.getByLabelText("Normen:")).toHaveTextContent(
      "nicht vorhanden",
    );

    expect(screen.getByLabelText("Entscheidungsname:")).toHaveTextContent(
      "nicht vorhanden",
    );

    expect(screen.getByLabelText("Vorinstanz:")).toHaveTextContent(
      "nicht vorhanden",
    );

    expect(screen.getByLabelText("Download:")).toHaveTextContent(
      "als ZIP herunterladen",
    );
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

    const title = unref(headArg.title) as string;
    const date = dayjs(caseLawTestData.decisionDate).format("DD.MM.YYYY");

    expect(title).toContain(`${caseLawTestData.courtName}:`);
    expect(title).toContain(caseLawTestData.documentType);
    expect(title).toContain(`vom ${date}`);
    expect(title).toContain(caseLawTestData.fileNumbers[0]);
    expect(title.length).toBeLessThanOrEqual(55);

    const meta = unref(headArg.meta) as Array<{
      name?: string;
      property?: string;
      content?: string;
    }>;
    const description = meta.find(
      (tag: { name?: string; property?: string; content?: string }) =>
        tag.name === "description",
    )?.content as string;

    expect(description).toContain("Sample guiding principle");
  });

  it("falls back to first p from HTML when guidingPrinciple is missing", async () => {
    const htmlDataWithBodyP =
      "<!DOCTYPE HTML><html><body>" +
      '<h1 id="title"><p>(Sample headline)</p></h1>' +
      '<section id="content"><p>First body paragraph used for description.</p></section>' +
      "</body></html>";

    useFetchMock.mockImplementation(async (url: string) => {
      if (url.includes("html")) {
        return { data: ref(htmlDataWithBodyP), status: ref("success") };
      }
      return {
        data: ref({ ...caseLawTestData, guidingPrinciple: "" }),
        status: ref("success"),
      };
    });

    await mountSuspended(CaseLawPage);
    await nextTick();

    const headArg = useHeadMock.mock.calls.at(-1)?.[0];
    const meta = unref(headArg.meta) as Array<{
      name?: string;
      content?: string;
    }>;
    const description = meta.find((metaTag) => metaTag.name === "description")
      ?.content as string;

    expect(description).toContain("First body paragraph used for description.");
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
          NuxtLink: {
            props: ["to"],
            template: `<a :href="to"><slot /></a>`,
          },
        },
      },
    });

    const detailsTabLink = wrapper.get("a[href='#details']");
    await detailsTabLink.trigger("click");

    const zipLink = wrapper.get("[data-attr='xml-zip-view']");
    expect(zipLink).toBeTruthy();
    expect(zipLink.text()).toBe("12345 als ZIP herunterladen");
    expect(zipLink.attributes("href")).toBe("/v1/case-law/12345.zip");
  });

  it("passes caseLaw to the CaseLawActionsMenu", async () => {
    const wrapper = await mountSuspended(CaseLawPage);
    const caseLawActionsMenu = wrapper.getComponent(CaseLawActionsMenu);
    expect(caseLawActionsMenu.props("caseLaw")).toEqual(caseLawTestData);
  });
});
