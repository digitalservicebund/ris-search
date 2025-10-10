import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import type { EventHandlerRequest, H3Event } from "h3";
import { beforeEach, describe, expect, it, vi, test } from "vitest";
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
    mockUseRuntimeConfig.mockImplementation(() => ({
      risBackendUrl: "backendUrl",
      public: {
        profile: "public",
      },
    }));

    const mockEvent: H3Event<EventHandlerRequest> = {
      node: {
        req: {
          headers: {
            host: "origin",
            "user-agent": "DG_JUSTICE_CRAWLER",
          },
          originalUrl: "url",
        },
        res: {
          setHeader: vi.fn(),
        },
      },
    } as unknown as H3Event<EventHandlerRequest>;

    vi.mock("../auth", async () => {
      return {
        requireAccessTokenWithRefresh: () => "mockToken",
      };
    });

    await middleware(mockEvent);
    expect(mockFetch).toHaveBeenCalledWith(
      "backendUrl/v1/eclicrawler/robots.txt",
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
      public: {
        profile: profile,
        backendUrl: "http://backend.example.com",
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
