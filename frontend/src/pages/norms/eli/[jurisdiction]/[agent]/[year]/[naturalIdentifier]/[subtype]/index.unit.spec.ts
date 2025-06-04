import { beforeEach, describe, expect, it, type Mock, vi } from "vitest";
import { config } from "@vue/test-utils";
import LegislationWorkRedirectComponent from "./index.vue";
import { getMostRelevantExpression } from "./index.logic";
import type {
  JSONLDList,
  LegislationExpression,
  LegislationWork,
  SearchResult,
} from "~/types";
import { mockNuxtImport, mountSuspended } from "@nuxt/test-utils/runtime";

const { mockUseFetch, mockUseBackendURL, mockNavigateTo, mockShowError } =
  vi.hoisted(() => ({
    mockUseFetch: vi.fn(),
    mockUseBackendURL: vi.fn(() => "http://localhost:3000/api"),
    mockNavigateTo: vi.fn(),
    mockShowError: vi.fn(),
  }));

mockNuxtImport("useFetch", () => mockUseFetch);
mockNuxtImport("useRoute", () => {
  return () => ({
    params: {
      jurisdiction: "bund",
      agent: "bgbl-1",
      year: "2025",
      naturalIdentifier: "s1000",
      subtype: "regelungstext-1",
    },
  });
});
mockNuxtImport("showError", () => mockShowError);
mockNuxtImport("navigateTo", () => mockNavigateTo);

vi.mock(
  "~/pages/norms/eli/[jurisdiction]/[agent]/[year]/[naturalIdentifier]/[subtype]/index.logic",
  () => ({
    getMostRelevantExpression: vi.fn(),
  }),
);

vi.mock("~/composables/useBackendURL", () => ({
  useBackendURL: mockUseBackendURL,
}));

describe("LegislationWorkRedirectComponent", () => {
  beforeEach(() => {
    vi.restoreAllMocks();
    // Reset mocks for each test
    mockUseFetch.mockReturnValue({ data: ref(null), error: ref(null) });
  });
  beforeAll(() => {
    // required to verify the message show by stubbed LoadingMessage component
    config.global.renderStubDefaultSlot = true;
  });

  afterAll(() => {
    config.global.renderStubDefaultSlot = false;
  });

  const mountComponent = async () => {
    return mountSuspended(LegislationWorkRedirectComponent, {
      shallow: true,
    });
  };

  it("constructs the correct work ELI from route params", async () => {
    await mountComponent();

    expect(mockUseFetch).toHaveBeenCalledWith(
      "http://localhost:3000/api/v1/legislation",
      {
        params: {
          eli: "eli/bund/bgbl-1/2025/s1000/regelungstext-1",
        },
      },
      expect.any(String),
    );
  });

  it("calls getMostRelevantExpression and navigates if an expression is found", async () => {
    const mockLegislationWork = {
      workExample: {
        legislationIdentifier: "mock-expression-eli",
      } as LegislationExpression,
    } as LegislationWork;

    const mockData: JSONLDList<SearchResult<LegislationWork>> = {
      "@type": "hydra:Collection",
      totalItems: 0,
      view: { first: "", last: "", next: null, previous: null },
      member: [
        {
          item: mockLegislationWork,
          textMatches: [],
        },
      ],
    };

    mockUseFetch.mockReturnValue({ data: ref(mockData), error: ref(null) });
    (getMostRelevantExpression as Mock).mockReturnValue("mock-expression-eli");

    await mountComponent();

    expect(getMostRelevantExpression).toHaveBeenCalledWith(mockData.member);
    expect(mockNavigateTo).toHaveBeenCalledWith("/norms/mock-expression-eli", {
      replace: true,
    });
    expect(mockShowError).not.toHaveBeenCalled();
  });

  it("shows an error if loadError is present", async () => {
    const mockError = {
      statusCode: 500,
      statusMessage: "Internal Server Error",
    };
    mockUseFetch.mockReturnValue({ data: ref(null), error: ref(mockError) });

    await mountComponent();

    expect(mockShowError).toHaveBeenCalledWith(mockError);
    expect(mockNavigateTo).not.toHaveBeenCalled();
  });

  it("shows a 404 error if no norms are found (member array is empty)", async () => {
    const mockData: JSONLDList<SearchResult<LegislationWork>> = {
      "@type": "hydra:Collection",
      totalItems: 0,
      view: { first: "", last: "", next: null, previous: null },
      member: [],
    };
    mockUseFetch.mockReturnValue({ data: ref(mockData), error: ref(null) });

    await mountComponent();

    expect(mockShowError).toHaveBeenCalledWith({
      statusCode: 404,
      statusMessage: "no norms found matching work ELI",
    });
    expect(mockNavigateTo).not.toHaveBeenCalled();
  });

  it("renders the loading message initially", async () => {
    mockUseFetch.mockReturnValue({ data: ref(null), error: ref(null) });
    const wrapper = await mountComponent();

    expect(wrapper.text()).toContain(
      "Suche aktuelle, zukünftige oder historische Fassung…",
    );
  });
});
