// @ts-nocheck
import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { HttpStatusCode } from "axios";
import type { EventHandlerRequest, H3Event } from "h3";
import { beforeEach, describe, expect, it, vi } from "vitest";
import middleware from "./[...]";

const { getRequestURL, mockUseRuntimeConfig } = vi.hoisted(() => {
  return { getRequestURL: vi.fn(), mockUseRuntimeConfig: vi.fn() };
});

vi.mock("h3", () => ({
  defineEventHandler: vi.fn((handler) => handler),
  getRequestURL,
  createError: vi.fn((params) => ({
    statusCode: params.statusCode,
    statusMessage: params.statusMessage,
    cause: params.cause,
  })),
}));

vi.mock("../auth", async (importOriginal) => {
  return {
    ...(await importOriginal()),
    performRefresh: vi.fn(),
  };
});

const mockFetch = vi.fn();

vi.stubGlobal("$fetch", { raw: mockFetch });
vi.stubGlobal("setUserSession", vi.fn());

mockNuxtImport("useRuntimeConfig", () => {
  return mockUseRuntimeConfig;
});

const mockEvent: H3Event<EventHandlerRequest> = {
  headers: new Map([["Accept", "application/json"]]),
} as unknown as H3Event<EventHandlerRequest>;

const mockSession = {
  secure: {
    tokens: {
      accessToken: "valid-token",
      refreshToken: "valid-refresh-token",
    },
  },
  expiresAt: Date.now() + 10000,
};

describe("Access Token Middleware", () => {
  beforeEach(() => {
    vi.clearAllMocks();

    globalThis.getUserSession = vi.fn().mockResolvedValue(mockSession);

    // Mock getRequestURL
    getRequestURL.mockReturnValue({
      pathname: "/v1/resource",
      search: "?query=test",
    });

    mockUseRuntimeConfig.mockImplementation(() => ({
      risBackendUrl: "http://backend.example.com",
      oauth: {
        keycloak: {},
      },
      public: {
        authEnabled: true,
      },
    }));
  });

  it("should forward request with valid token", async () => {
    mockFetch.mockResolvedValueOnce({ data: "success" });

    const result = await middleware(mockEvent);

    expect(mockFetch).toHaveBeenCalledWith(
      "http://backend.example.com/v1/resource?query=test",
      {
        headers: {
          Accept: "application/json",
          Authorization: "Bearer valid-token",
        },
        responseType: "stream",
      },
    );
    expect(result).toEqual({ data: "success" });
  });

  it("should throw unauthorized error when no access token exists", async () => {
    globalThis.getUserSession.mockResolvedValueOnce({
      secure: {
        tokens: {
          accessToken: null,
        },
      },
    });

    await expect(middleware(mockEvent)).rejects.toEqual(
      expect.objectContaining({
        statusCode: HttpStatusCode.Unauthorized,
        statusMessage: "Unauthorized",
        cause: "no access token",
      }),
    );
  });

  it("should handle backend connection refused error", async () => {
    const connectionError = { cause: { cause: { code: "ECONNREFUSED" } } };
    mockFetch.mockRejectedValueOnce(connectionError);

    await expect(middleware(mockEvent)).rejects.toEqual(
      expect.objectContaining({
        statusCode: HttpStatusCode.InternalServerError,
        statusMessage: "Internal Server Error",
      }),
    );
  });

  it("should propagate other errors", async () => {
    const otherError = new Error("Other error");
    mockFetch.mockRejectedValueOnce(otherError);

    await expect(middleware(mockEvent)).rejects.toThrow(otherError);
  });

  it("should throw an error if auth is not enabled", async () => {
    mockUseRuntimeConfig.mockImplementation(() => ({
      public: {
        authEnabled: false,
      },
    }));
    await expect(middleware(mockEvent)).rejects.toThrowError();
  });
});
