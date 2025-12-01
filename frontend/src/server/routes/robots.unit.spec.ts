import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import type { EventHandlerRequest, H3Event } from "h3";
import { beforeEach, describe, expect, it, test, vi } from "vitest";
import middleware from "./robots.txt.get";
import useBackendUrl from "~/composables/useBackendUrl";

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
      public: {
        privateFeaturesEnabled: false,
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

    await middleware(mockEvent);
    expect(mockFetch).toHaveBeenCalledWith(
      useBackendUrl("/v1/eclicrawler/robots.txt"),
      {
        method: "GET",
      },
    );
  });

  const testCases = [
    [false, "robots.public.txt"],
    [true, "robots.staging.txt"],
  ];

  test.for(testCases)(
    "privateFeaturesEnabled flag = %s serves %s",
    async ([privateFeaturesEnabled, file]) => {
      mockUseRuntimeConfig.mockImplementation(() => ({
        public: {
          privateFeaturesEnabled,
        },
      }));

      const mockEvent: H3Event<EventHandlerRequest> = {
        node: {
          req: {
            originalUrl: "url",
            headers: {
              host: "origin",
              "user-agent": "default",
              authorization: "auth",
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
        headers: {
          Authorization: "auth",
        },
      });
    },
  );
});
