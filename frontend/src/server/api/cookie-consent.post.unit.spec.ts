import type { EventHandlerRequest, H3Event } from "h3";
import { beforeEach, describe, expect, it, vi } from "vitest";
import cookieConsentHandler from "./cookie-consent.post";

const {
  mockReadBody,
  mockSetCookie,
  mockGetHeader,
  mockGetRequestURL,
  mockSendRedirect,
} = vi.hoisted(() => {
  return {
    mockReadBody: vi.fn(),
    mockSetCookie: vi.fn(),
    mockGetHeader: vi.fn(),
    mockGetRequestURL: vi.fn(),
    mockSendRedirect: vi.fn(),
  };
});

vi.mock("h3", () => ({
  defineEventHandler: vi.fn((handler) => handler),
  readBody: mockReadBody,
  setCookie: mockSetCookie,
  getHeader: mockGetHeader,
  getRequestURL: mockGetRequestURL,
  sendRedirect: mockSendRedirect,
}));

describe("cookie-consent.post", () => {
  let mockEvent: H3Event<EventHandlerRequest>;

  beforeEach(() => {
    vi.clearAllMocks();
    mockEvent = {
      node: { req: {}, res: {} },
    } as unknown as H3Event<EventHandlerRequest>;
  });

  it("sets consent cookie to true when consent is 'true' string", async () => {
    mockReadBody.mockResolvedValue({ consent: "true" });
    mockGetHeader.mockReturnValue(undefined);

    await cookieConsentHandler(mockEvent);

    expect(mockSetCookie).toHaveBeenCalledWith(
      mockEvent,
      "consent_given",
      "true",
      expect.objectContaining({
        maxAge: 365 * 24 * 60 * 60,
        path: "/",
        sameSite: "lax",
      }),
    );
    expect(mockSendRedirect).toHaveBeenCalledWith(mockEvent, "/");
  });

  it("sets consent cookie to false when consent is 'false' string", async () => {
    mockReadBody.mockResolvedValue({ consent: "false" });
    mockGetHeader.mockReturnValue(undefined);

    await cookieConsentHandler(mockEvent);

    expect(mockSetCookie).toHaveBeenCalledWith(
      mockEvent,
      "consent_given",
      "false",
      expect.objectContaining({
        maxAge: 365 * 24 * 60 * 60,
        path: "/",
        sameSite: "lax",
      }),
    );
  });

  it("redirects to referer path when same-origin referer provided", async () => {
    mockReadBody.mockResolvedValue({ consent: "true" });
    mockGetHeader.mockReturnValue(
      "https://example.com/cookie-einstellungen?test=1",
    );
    mockGetRequestURL.mockReturnValue(
      new URL("https://example.com/api/cookie-consent"),
    );

    await cookieConsentHandler(mockEvent);

    expect(mockSendRedirect).toHaveBeenCalledWith(
      mockEvent,
      "/cookie-einstellungen?test=1",
    );
  });

  it("redirects to home when referer URL is invalid", async () => {
    mockReadBody.mockResolvedValue({ consent: "true" });
    mockGetHeader.mockReturnValue("not-a-valid-url");

    await cookieConsentHandler(mockEvent);

    expect(mockSendRedirect).toHaveBeenCalledWith(mockEvent, "/");
  });

  it("redirects to home when referer origin does not match (prevents open redirect)", async () => {
    mockReadBody.mockResolvedValue({ consent: "true" });
    mockGetHeader.mockReturnValue("https://evil.com/steal-cookies");
    mockGetRequestURL.mockReturnValue(
      new URL("https://example.com/api/cookie-consent"),
    );

    await cookieConsentHandler(mockEvent);

    expect(mockSendRedirect).toHaveBeenCalledWith(mockEvent, "/");
  });
});
