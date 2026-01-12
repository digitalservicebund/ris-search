import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import type { PostHog } from "posthog-js";
import posthog from "posthog-js";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { resetPostHogState, usePostHog } from "~/composables/usePostHog";
import { addDefaults } from "~/composables/useSimpleSearchParams/getInitialState";
import type { QueryParams } from "~/composables/useSimpleSearchParams/useSimpleSearchParams";
import { getPostHogConfig } from "~/tests/postHogUtils";

const { useRuntimeConfigMock } = vi.hoisted(() => {
  return {
    useRuntimeConfigMock: vi.fn(() => {
      return getPostHogConfig("key", "host");
    }),
  };
});

mockNuxtImport("useRuntimeConfig", () => {
  return useRuntimeConfigMock;
});

const postHogMock = vi.hoisted(() => {
  return {
    capture: vi.fn(),
    opt_in_capturing: vi.fn(),
    opt_out_capturing: vi.fn(),
    clear_opt_in_out_capturing: vi.fn(),
  };
});

vi.mock("posthog-js", () => ({
  default: {
    init: vi.fn().mockReturnValue(postHogMock),
  },
}));

const cookiesMock = vi.hoisted(() => {
  return {
    get: vi.fn(),
    set: vi.fn(),
    remove: vi.fn(),
  };
});

vi.mock("js-cookie", () => ({
  default: cookiesMock,
}));

const { useRisBackendMock } = vi.hoisted(() => {
  return {
    useRisBackendMock: vi.fn(() => {
      return { error: ref(null) };
    }),
  };
});

mockNuxtImport("useRisBackend", () => {
  return useRisBackendMock;
});

const feedbackURL = `/v1/feedback`;

describe("usePostHog", () => {
  beforeEach(() => {
    resetPostHogState();
    vi.clearAllMocks();
  });

  it("initializes postHog when userConsent is true", () => {
    cookiesMock.get.mockReturnValue("true");
    const { initialize, postHog } = usePostHog();
    initialize();
    expect(posthog.init).toHaveBeenCalledWith("key", {
      api_host: "host",
    });
    expect(postHog.value).toBeDefined();
  });

  it("isBannerVisible returns true when PostHog is enabled and no consent cookie exists", () => {
    cookiesMock.get.mockReturnValue(undefined);
    const { isBannerVisible } = usePostHog();
    expect(isBannerVisible.value).toBe(true);
  });

  it("isBannerVisible returns false when a consent cookie is present", () => {
    cookiesMock.get.mockReturnValue("true");
    const { isBannerVisible, initialize } = usePostHog();
    initialize();
    expect(isBannerVisible.value).toBe(false);
  });

  it("userConsent returns the boolean value from the consent cookie", () => {
    cookiesMock.get.mockReturnValue("false");
    const { userConsent, initialize } = usePostHog();
    initialize();
    expect(userConsent.value).toBe(false);
  });

  it("setTracking to false deactivates postHog state, removes cookie and opt out of capturing", () => {
    const { setTracking, postHog, userConsent } = usePostHog();
    cookiesMock.get.mockReturnValue({ ph_key_posthog: "some cookie value" });
    setTracking(false);
    expect(postHog.value).toBeUndefined();
    expect(cookiesMock.set).toHaveBeenCalledWith("consent_given", "false", {
      expires: 365,
      path: "/",
      sameSite: "lax",
      secure: expect.anything(),
    });
    expect(userConsent.value).toBe(false);
    expect(cookiesMock.remove).toHaveBeenCalledWith("ph_key_posthog", {
      path: "/",
    });
  });

  it("setTracking to true activates postHog state, sets cookie and opt in capturing", () => {
    const { setTracking, postHog, userConsent } = usePostHog();
    setTracking(true);
    expect(posthog.init).toHaveBeenCalledWith("key", {
      api_host: "host",
    });
    expect(postHog.value).toBeDefined();
    expect(cookiesMock.set).toHaveBeenCalledWith("consent_given", "true", {
      expires: 365,
      path: "/",
      sameSite: "lax",
      secure: expect.anything(),
    });
    expect(userConsent.value).toBe(true);
    expect(postHog.value?.opt_in_capturing).toHaveBeenCalled();
  });

  it("sendFeedbackToPostHog sends the user feedback and tracking information to backend when user enables tracking", () => {
    const { setTracking, sendFeedbackToPostHog } = usePostHog();
    cookiesMock.get.mockReturnValue({
      ph_key_posthog: '{"distinct_id":"12345"}',
    });
    setTracking(true);
    sendFeedbackToPostHog("good");
    expect(useRisBackendMock).toHaveBeenCalledWith(
      feedbackURL + "?text=good&url=%2F&user_id=12345",
    );
    cookiesMock.get.mockRestore();
  });

  it("sendFeedbackToPostHog sends the data to backend as anonymous user when the user disables tracking", () => {
    const { setTracking, sendFeedbackToPostHog } = usePostHog();
    setTracking(false);
    sendFeedbackToPostHog("test");
    expect(useRisBackendMock).toHaveBeenCalledWith(
      feedbackURL + "?text=test&url=%2F&user_id=anonymous_feedback_user",
    );
  });

  it("captures search event when postHog is initialized and user consent is given", () => {
    const { setTracking, searchPerformed, postHog } = usePostHog();
    setTracking(true);
    searchPerformed("simple", addDefaults({ query: "test query" }), {
      query: "old query",
    } as QueryParams);
    expect(posthog.init).toHaveBeenCalledWith("key", { api_host: "host" });
    expect(postHog.value?.capture).toHaveBeenCalledWith("search_performed", {
      type: "simple",
      newParams: {
        category: "A",
        dateSearchMode: "",
        itemsPerPage: 10,
        pageNumber: 0,
        query: "test query",
        sort: "default",
      },
      previousParams: { query: "old query" },
    });
  });

  it("does not capture search event when user consent is not given", () => {
    const { setTracking, searchPerformed, postHog, userConsent } = usePostHog();
    setTracking(true);
    userConsent.value = false;
    searchPerformed("simple", addDefaults({ query: "test query" }));
    expect(postHog.value?.capture).not.toHaveBeenCalled();
  });

  it("captures search_result_clicked event when postHog exists and user consent is given", () => {
    const { setTracking, searchResultClicked, postHog } = usePostHog();
    const router = useRouter();
    router.currentRoute.value.query = { query: "test query" };
    setTracking(true);
    const captureSpy = vi.spyOn(postHog.value as PostHog, "capture");
    searchResultClicked("testUrl", 1);
    expect(captureSpy).toHaveBeenCalledWith("search_result_clicked", {
      url: "testUrl",
      order: 1,
      searchParams: { query: "test query" },
    });
  });

  it("captures no_search_results event when postHog exists and user consent is given", () => {
    const { setTracking, noSearchResults, postHog } = usePostHog();
    setTracking(true);
    const router = useRouter();
    router.currentRoute.value.query = { query: "test query" };
    const captureSpy = vi.spyOn(postHog.value as PostHog, "capture");
    noSearchResults();
    expect(captureSpy).toHaveBeenCalledWith("no_search_results", {
      searchParams: { query: "test query" },
    });
  });
});
