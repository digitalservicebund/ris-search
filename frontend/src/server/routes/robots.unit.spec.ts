import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import type { EventHandlerRequest, H3Event } from "h3";
import { beforeEach, describe, expect, it, test, vi } from "vitest";
import middleware from "./robots.txt.get";

const mockFetch = vi.fn();
vi.stubGlobal("$fetch", mockFetch);

const { mockUseRuntimeConfig } = vi.hoisted(() => {
  return { getRequestURL: vi.fn(), mockUseRuntimeConfig: vi.fn() };
});

mockNuxtImport("useRuntimeConfig", () => {
  return mockUseRuntimeConfig;
});

describe("robots txt route", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("should serve robots txt from backend api on justice crawler", async () => {
    const mockEvent: H3Event<EventHandlerRequest> = {
      node: {
        req: {
          headers: {
            host: "localhost",
            "user-agent": "DG_JUSTICE_CRAWLER",
          },
          originalUrl: "url",
        },
        res: {
          setHeader: vi.fn(),
        },
      },
    } as unknown as H3Event<EventHandlerRequest>;

    vi.mock("../auth", async (importOriginal) => {
      return {
        ...(await importOriginal()),
        requireAccessTokenWithRefresh: () => "mockToken",
      };
    });

    const mockFetch = vi.fn();
    vi.stubGlobal("$fetch", mockFetch);
    vi.stubGlobal("setUserSession", vi.fn());

    await middleware(mockEvent);
    expect(mockFetch).toHaveBeenCalledWith(
      "http://localhost:8090/v1/eclicrawler/robots.txt",
      {
        method: "GET",
        headers: {
          Authorization: "Bearer mockToken",
        },
      },
    );
  });


  const testCases = [
  ["public", "robots.public.txt"],
  ["internal", "robots.staging.txt"],
  ["prototype", "robots.public.txt"],
];

test.for(testCases)("profile %s serves %s", async ([profile, file]) => {
    mockUseRuntimeConfig.mockImplementation(() => ({
      risBackendUrl: "http://backend.example.com",
      public: {
        profile: profile,
      },
    }));

    const mockEvent: H3Event<EventHandlerRequest> = {
      node: {
        req: {
          originalUrl: "url",
          headers: {
            host: "origin",
            "user-agent": "default",
          },
        },
        res: {
          setHeader: vi.fn(),
        },
      },
    } as unknown as H3Event<EventHandlerRequest>;

    await middleware(mockEvent);
    expect(mockFetch).toHaveBeenCalledWith(`http://origin/${file}`, {
      method: "GET",
    });
  });
});


