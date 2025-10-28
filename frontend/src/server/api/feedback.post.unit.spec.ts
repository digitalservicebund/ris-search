import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import type { EventHandlerRequest, H3Event } from "h3";
import { beforeEach, describe, expect, it, vi } from "vitest";
import feedbackHandler from "./feedback.post";

const {
  mockReadBody,
  mockGetHeader,
  mockGetRequestURL,
  mockSendRedirect,
  mockFetch,
  mockUseRuntimeConfig,
} = vi.hoisted(() => {
  return {
    mockReadBody: vi.fn(),
    mockGetHeader: vi.fn(),
    mockGetRequestURL: vi.fn(),
    mockSendRedirect: vi.fn(),
    mockFetch: vi.fn(),
    mockUseRuntimeConfig: vi.fn(() => ({
      risBackendUrl: "https://backend",
    })),
  };
});

mockNuxtImport("useRuntimeConfig", () => mockUseRuntimeConfig);

vi.mock("h3", () => ({
  defineEventHandler: vi.fn((handler) => handler),
  readBody: mockReadBody,
  getHeader: mockGetHeader,
  getRequestURL: mockGetRequestURL,
  sendRedirect: mockSendRedirect,
}));

vi.stubGlobal("$fetch", mockFetch);

describe("feedback.post", () => {
  let mockEvent: H3Event<EventHandlerRequest>;

  beforeEach(() => {
    vi.clearAllMocks();
    mockEvent = {
      node: { req: {}, res: {} },
    } as unknown as H3Event<EventHandlerRequest>;
  });

  it("forwards feedback to backend", async () => {
    mockReadBody.mockResolvedValue({ text: "Great app!" });
    mockGetHeader.mockReturnValue("https://example.com/search?query=test");
    mockGetRequestURL.mockReturnValue(
      new URL("https://example.com/api/feedback"),
    );
    mockFetch.mockResolvedValue({});

    await feedbackHandler(mockEvent);

    expect(mockFetch).toHaveBeenCalledWith(
      "https://backend/v1/feedback?text=Great+app%21&url=%2Fsearch%3Fquery%3Dtest&user_id=anonymous_feedback_user",
    );
  });

  it("uses provided url and user_id from form body", async () => {
    mockReadBody.mockResolvedValue({
      text: "Feedback text",
      url: "/custom-page",
      user_id: "user123",
    });
    mockGetHeader.mockReturnValue(undefined);
    mockFetch.mockResolvedValue({});

    await feedbackHandler(mockEvent);

    expect(mockFetch).toHaveBeenCalledWith(
      "https://backend/v1/feedback?text=Feedback+text&url=%2Fcustom-page&user_id=user123",
    );
  });

  it("redirects with error parameter when backend fails", async () => {
    mockReadBody.mockResolvedValue({ text: "Feedback" });
    mockGetHeader.mockReturnValue("https://example.com/test");
    mockGetRequestURL.mockReturnValue(
      new URL("https://example.com/api/feedback"),
    );
    mockFetch.mockRejectedValue(new Error("Backend error"));

    await feedbackHandler(mockEvent);

    expect(mockSendRedirect).toHaveBeenCalledWith(
      mockEvent,
      "/test?feedback=error",
    );
  });

  it("prevents open redirect by validating referer origin", async () => {
    mockReadBody.mockResolvedValue({ text: "Feedback" });
    mockGetHeader.mockReturnValue("https://evil.com/steal");
    mockGetRequestURL.mockReturnValue(
      new URL("https://example.com/api/feedback"),
    );
    mockFetch.mockResolvedValue({});

    await feedbackHandler(mockEvent);

    expect(mockSendRedirect).toHaveBeenCalledWith(mockEvent, "/?feedback=sent");
  });

  it("extracts url from referer when no url in body", async () => {
    mockReadBody.mockResolvedValue({ text: "Feedback" });
    mockGetHeader.mockReturnValue("https://example.com/page?param=value");
    mockFetch.mockResolvedValue({});

    await feedbackHandler(mockEvent);

    expect(mockFetch).toHaveBeenCalledWith(
      "https://backend/v1/feedback?text=Feedback&url=%2Fpage%3Fparam%3Dvalue&user_id=anonymous_feedback_user",
    );
  });
});
