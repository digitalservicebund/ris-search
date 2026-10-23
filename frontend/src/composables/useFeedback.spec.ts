import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { useFeedback } from "./useFeedback";

const { useRisBackendMock } = vi.hoisted(() => ({
  useRisBackendMock: vi.fn(() => ({ error: { value: null as unknown } })),
}));

mockNuxtImport("useRisBackend", () => useRisBackendMock);

describe("useFeedback", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("sends the feedback to the backend", async () => {
    const { sendFeedback } = useFeedback();
    await sendFeedback("good", "bot-trap-value");
    expect(useRisBackendMock).toHaveBeenCalledWith("/v1/feedback", {
      method: "POST",
      body: {
        text: "good",
        url: "/",
        name: "bot-trap-value",
      },
    });
  });

  it("throws if sending the feedback fails", async () => {
    useRisBackendMock.mockReturnValueOnce({ error: { value: new Error() } });
    const { sendFeedback } = useFeedback();
    await expect(sendFeedback("test", "")).rejects.toThrow(
      "Error sending feedback",
    );
  });
});
