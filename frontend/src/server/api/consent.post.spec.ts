// @vitest-environment node
import type { EventHandlerRequest, H3Event } from "h3";
import { beforeEach, describe, expect, it, vi } from "vitest";
import consentHandler from "./consent.post";

const {
  mockReadBody,
  mockGetHeader,
  mockGetRequestURL,
  mockSendRedirect,
  mockSetCookie,
} = vi.hoisted(() => {
  return {
    mockReadBody: vi.fn(),
    mockGetHeader: vi.fn(),
    mockGetRequestURL: vi.fn(),
    mockSendRedirect: vi.fn(),
    mockSetCookie: vi.fn(),
  };
});

vi.mock("h3", () => ({
  createError: vi.fn((input) => Object.assign(new Error(), input)),
  defineEventHandler: vi.fn((handler) => handler),
  readBody: mockReadBody,
  getHeader: mockGetHeader,
  getRequestURL: mockGetRequestURL,
  sendRedirect: mockSendRedirect,
  setCookie: mockSetCookie,
}));

describe("consent.post", () => {
  let mockEvent: H3Event<EventHandlerRequest>;

  beforeEach(() => {
    vi.clearAllMocks();
    mockEvent = {
      node: { req: {}, res: {} },
    } as unknown as H3Event<EventHandlerRequest>;
    mockGetHeader.mockReturnValue("same-origin");
    mockGetRequestURL.mockReturnValue(
      new URL("https://example.com/api/consent"),
    );
  });

  it("sets the consent cookie to 1 when consent is granted", async () => {
    mockReadBody.mockResolvedValue({ consent: "granted", redirect: "/" });

    await consentHandler(mockEvent);

    expect(mockSetCookie).toHaveBeenCalledWith(
      mockEvent,
      "consent_given",
      "1",
      {
        maxAge: 365 * 24 * 60 * 60,
        path: "/",
        sameSite: "lax",
        secure: expect.any(Boolean),
      },
    );
  });

  it("sets the consent cookie to 0 when consent is denied", async () => {
    mockReadBody.mockResolvedValue({ consent: "denied", redirect: "/" });

    await consentHandler(mockEvent);

    expect(mockSetCookie).toHaveBeenCalledWith(
      mockEvent,
      "consent_given",
      "0",
      expect.any(Object),
    );
  });

  it("denies consent for unknown values", async () => {
    mockReadBody.mockResolvedValue({ consent: "maybe" });

    await consentHandler(mockEvent);

    expect(mockSetCookie).toHaveBeenCalledWith(
      mockEvent,
      "consent_given",
      "0",
      expect.any(Object),
    );
  });

  it("does not set the cookie as HTTP only so PostHog can change it", async () => {
    mockReadBody.mockResolvedValue({ consent: "granted" });

    await consentHandler(mockEvent);

    expect(mockSetCookie.mock.calls[0]?.[3]).not.toHaveProperty("httpOnly");
  });

  it("redirects back to the given path", async () => {
    mockReadBody.mockResolvedValue({
      consent: "granted",
      redirect: "/suche?query=test#results",
    });

    await consentHandler(mockEvent);

    expect(mockSendRedirect).toHaveBeenCalledWith(
      mockEvent,
      "/suche?query=test#results",
      303,
    );
  });

  it.each([
    undefined,
    "",
    "suche",
    "//evil.example.com/path",
    "/\\evil.example.com",
    "https://evil.example.com/path",
    "/.//evil.example.com",
    "/a/..//evil.example.com",
    "/.\\/evil.example.com",
    ["/a", "/b"],
  ])("redirects to the home page for %j", async (redirect) => {
    mockReadBody.mockResolvedValue({ consent: "granted", redirect });

    await consentHandler(mockEvent);

    expect(mockSendRedirect).toHaveBeenCalledWith(mockEvent, "/", 303);
  });

  it("accepts requests from browsers that don't send Sec-Fetch-Site", async () => {
    mockGetHeader.mockReturnValue(undefined);
    mockReadBody.mockResolvedValue({ consent: "granted", redirect: "/" });

    await consentHandler(mockEvent);

    expect(mockSetCookie).toHaveBeenCalled();
  });

  it.each(["cross-site", "same-site", "none"])(
    "rejects requests with Sec-Fetch-Site %s",
    async (fetchSite) => {
      mockGetHeader.mockReturnValue(fetchSite);
      mockReadBody.mockResolvedValue({ consent: "granted", redirect: "/" });

      await expect(consentHandler(mockEvent)).rejects.toMatchObject({
        statusCode: 403,
      });
      expect(mockSetCookie).not.toHaveBeenCalled();
    },
  );
});
