import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import type { EventHandlerRequest, H3Event } from "h3";
import { beforeEach, describe, expect, vi, test } from "vitest";
import middleware from "./robots.txt.get";

const mockFetch = vi.fn();
vi.stubGlobal("$fetch", mockFetch);

const { mockUseRuntimeConfig } = vi.hoisted(() => {
  return { getRequestURL: vi.fn(), mockUseRuntimeConfig: vi.fn() };
});

mockNuxtImport("useRuntimeConfig", () => {
  return mockUseRuntimeConfig;
});

const testCases = [
  ["public", "robots.public.txt"],
  ["internal", "robots.staging.txt"],
  ["prototype", "robots.public.txt"],
];

describe("robots txt route", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

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
