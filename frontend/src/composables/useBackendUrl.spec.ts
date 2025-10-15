import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { useBackendURL } from "./useBackendURL";

const metaMock = vi.hoisted(() => ({
  isServer: true,
}));
vi.mock("~/utils/importMeta", () => metaMock);

const { mockUseRuntimeConfig } = vi.hoisted(() => {
  return { mockUseRuntimeConfig: vi.fn() };
});

mockNuxtImport("useRuntimeConfig", () => {
  return mockUseRuntimeConfig;
});

describe("useBackendUrl", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("should return SSR backend URL when process is server", async () => {
    metaMock.isServer = true;

    mockUseRuntimeConfig.mockImplementation(() => ({
      ssrBackendUrl: "http://backendservice",
      public: {
        backendURL: "publicBackendUrl",
      },
    }));

    const backendUrl = useBackendURL();
    expect(backendUrl).toEqual("http://backendservice");
  });

  it("should return the public backend URL when no SSR is required", async () => {
    metaMock.isServer = false;

    mockUseRuntimeConfig.mockImplementation(() => ({
      ssrBackendUrl: "http://backendservice",
      public: {
        backendURL: "publicBackendUrl",
      },
    }));

    const backendUrl = useBackendURL();
    expect(backendUrl).toEqual("publicBackendUrl");
  });
});
