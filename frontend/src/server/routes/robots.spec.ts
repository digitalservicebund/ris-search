import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import type { EventHandlerRequest, H3Event } from "h3";
import { afterEach, describe, expect, it, test, vi } from "vitest";
import middleware from "./robots.txt.get";
import useBackendUrl from "~/composables/useBackendUrl";

vi.mock("~/plugins/risBackend", () => ({
  default: vi.fn(),
  extendOnRequest: (...cbs: unknown[]) => cbs,
}));

const mockPrivateFeaturesEnabled = vi.fn(() => false);
vi.mock("~/composables/usePrivateFeaturesFlag", () => ({
  usePrivateFeaturesFlag: () => mockPrivateFeaturesEnabled(),
}));

const mockBasicAuth = vi.hoisted(() => vi.fn(() => ""));

mockNuxtImport<() => ReturnType<typeof useRuntimeConfig>>(
  "useRuntimeConfig",
  (original) => {
    return () => ({
      ...original(),
      basicAuth: mockBasicAuth(),
    });
  },
);

describe("robots txt route", () => {
  afterEach(() => {
    vi.resetAllMocks();
  });

  it("should serve robots txt from backend api on justice crawler", async () => {
    const fetchSpy = vi
      .spyOn(globalThis as any, "$fetch")
      .mockResolvedValue("");
    mockPrivateFeaturesEnabled.mockReturnValue(false);

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
    expect(fetchSpy).toHaveBeenCalledWith(
      useBackendUrl("/v1/eclicrawler/robots.txt"),
      {
        method: "GET",
      },
    );
  });

  const testCases: [boolean, string][] = [
    [false, "robots.public.txt"],
    [true, "robots.staging.txt"],
  ];

  test.for(testCases)(
    "privateFeaturesEnabled flag = %s serves %s",
    async ([privateFeaturesEnabled, file]) => {
      const fetchSpy = vi
        .spyOn(globalThis as any, "$fetch")
        .mockResolvedValue("");
      mockPrivateFeaturesEnabled.mockReturnValue(privateFeaturesEnabled);
      mockBasicAuth.mockReturnValue("auth");

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
      expect(fetchSpy).toHaveBeenCalledWith(`http://origin/${file}`, {
        method: "GET",
        headers: {
          Authorization: "Basic auth",
        },
      });
    },
  );
});
