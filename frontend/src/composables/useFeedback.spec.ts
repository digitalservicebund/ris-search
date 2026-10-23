import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { useFeedback } from "./useFeedback";

const { postHogMock, consent, useRisBackendMock } = vi.hoisted(() => ({
  postHogMock: {
    get_distinct_id: vi.fn<() => string | undefined>(() => "12345"),
  },
  consent: { isConsentGiven: { value: true }, isPostHogEnabled: true },
  useRisBackendMock: vi.fn(() => ({ error: { value: null as unknown } })),
}));

mockNuxtImport("usePostHog", () => () => postHogMock);

mockNuxtImport("useConsent", () => () => consent);

mockNuxtImport("useRisBackend", () => useRisBackendMock);

describe("useFeedback", () => {
  beforeEach(() => {
    consent.isPostHogEnabled = true;
    consent.isConsentGiven.value = true;
    vi.clearAllMocks();
  });

  it("sends the feedback with the PostHog ID if the user consented", async () => {
    const { sendFeedback } = useFeedback();
    await sendFeedback("good", "bot-trap-value");
    expect(useRisBackendMock).toHaveBeenCalledWith("/v1/feedback", {
      method: "POST",
      body: {
        text: "good",
        url: "/",
        user_id: "12345",
        name: "bot-trap-value",
      },
    });
  });

  it("sends the feedback anonymously if the user didn't consent", async () => {
    consent.isConsentGiven.value = false;
    const { sendFeedback } = useFeedback();
    await sendFeedback("test", "");
    expect(postHogMock.get_distinct_id).not.toHaveBeenCalled();
    expect(useRisBackendMock).toHaveBeenCalledWith("/v1/feedback", {
      method: "POST",
      body: {
        text: "test",
        url: "/",
        user_id: "anonymous_feedback_user",
        name: "",
      },
    });
  });

  it("sends the feedback anonymously if PostHog is disabled", async () => {
    consent.isPostHogEnabled = false;
    const { sendFeedback } = useFeedback();
    await sendFeedback("test", "");
    expect(useRisBackendMock).toHaveBeenCalledWith(
      "/v1/feedback",
      expect.objectContaining({
        body: expect.objectContaining({ user_id: "anonymous_feedback_user" }),
      }),
    );
  });

  it("sends the feedback anonymously if PostHog has no ID", async () => {
    postHogMock.get_distinct_id.mockReturnValueOnce(undefined);
    const { sendFeedback } = useFeedback();
    await sendFeedback("test", "");
    expect(useRisBackendMock).toHaveBeenCalledWith(
      "/v1/feedback",
      expect.objectContaining({
        body: expect.objectContaining({ user_id: "anonymous_feedback_user" }),
      }),
    );
  });

  it("throws if sending the feedback fails", async () => {
    useRisBackendMock.mockReturnValueOnce({ error: { value: new Error() } });
    const { sendFeedback } = useFeedback();
    await expect(sendFeedback("test", "")).rejects.toThrow(
      "Error sending feedback",
    );
  });
});
