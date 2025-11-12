import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { createPinia, setActivePinia } from "pinia";
import type { PostHog } from "posthog-js";
import posthog from "posthog-js";
import { beforeEach, describe, expect, it, vi } from "vitest";
import type { QueryParams } from "~/stores/searchParams";
import { addDefaults } from "~/stores/searchParams/getInitialState";
import { usePostHogStore } from "~/stores/usePostHogStore";
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

const { useFetchMock } = vi.hoisted(() => {
  return {
    useFetchMock: vi.fn(() => {
      return { error: ref(null) };
    }),
  };
});

mockNuxtImport("useFetch", () => {
  return useFetchMock;
});

const feedbackURL = `/v1/feedback`;

describe("usePostHogStore", () => {
  beforeEach(() => {
    setActivePinia(createPinia());
    vi.clearAllMocks();
  });

  it("initializes postHog when userConsent is true", () => {
    cookiesMock.get.mockReturnValue("true");
    const store = usePostHogStore();
    store.initialize();
    expect(posthog.init).toHaveBeenCalledWith("key", {
      api_host: "host",
    });
    expect(store.postHog).toBeDefined();
  });

  it("isBannerVisible returns true when PostHog is enabled and no consent cookie exists", () => {
    cookiesMock.get.mockReturnValue(undefined);
    const { isBannerVisible } = storeToRefs(usePostHogStore());
    expect(isBannerVisible.value).toBe(true);
  });

  it("isBannerVisible returns false when a consent cookie is present", () => {
    cookiesMock.get.mockReturnValue("true");
    const store = usePostHogStore();
    const { isBannerVisible } = storeToRefs(store);
    store.initialize();
    expect(isBannerVisible.value).toBe(false);
  });

  it("userConsent returns the boolean value from the consent cookie", () => {
    cookiesMock.get.mockReturnValue("false");
    const store = usePostHogStore();
    const { userConsent } = storeToRefs(store);
    store.initialize();
    expect(userConsent.value).toBe(false);
  });

  it("setTracking to false deactivates postHog state, removes cookie and opt out of capturing", () => {
    const store = usePostHogStore();
    cookiesMock.get.mockReturnValue({ ph_key_posthog: "some cookie value" });
    store.setTracking(false);
    expect(store.postHog).toBeUndefined();
    expect(cookiesMock.set).toHaveBeenCalledWith("consent_given", "false", {
      expires: 365,
      path: "/",
      sameSite: "lax",
      secure: expect.anything(),
    });
    expect(store.userConsent).toBe(false);
    expect(cookiesMock.remove).toHaveBeenCalledWith("ph_key_posthog", {
      path: "/",
    });
  });

  it("setTracking to true activates postHog state, sets cookie and opt in capturing", () => {
    const store = usePostHogStore();
    store.setTracking(true);
    expect(posthog.init).toHaveBeenCalledWith("key", {
      api_host: "host",
    });
    expect(store.postHog).toBeDefined();
    expect(cookiesMock.set).toHaveBeenCalledWith("consent_given", "true", {
      expires: 365,
      path: "/",
      sameSite: "lax",
      secure: expect.anything(),
    });
    expect(store.userConsent).toBe(true);
    expect(store.postHog?.opt_in_capturing).toHaveBeenCalled();
  });

  it("sendFeedbackToPostHog sends the user feedback and tracking information to backend when user enables tracking", () => {
    const store = usePostHogStore();
    cookiesMock.get.mockReturnValue({
      ph_key_posthog: '{"distinct_id":"12345"}',
    });
    store.setTracking(true);
    store.sendFeedbackToPostHog("good");
    expect(useFetchMock).toHaveBeenCalledWith(
      feedbackURL + "?text=good&url=%2F&user_id=12345",
      expect.anything(),
    );
    cookiesMock.get.mockRestore();
  });

  it("sendFeedbackToPostHog sends the data to backend as anonymous user when the user disables tracking", () => {
    const store = usePostHogStore();
    store.setTracking(false);
    store.sendFeedbackToPostHog("test");
    expect(useFetchMock).toHaveBeenCalledWith(
      feedbackURL + "?text=test&url=%2F&user_id=anonymous_feedback_user",
      expect.anything(),
    );
  });

  it("captures search event when postHog is initialized and user consent is given", () => {
    const store = usePostHogStore();
    store.setTracking(true);
    store.searchPerformed("simple", addDefaults({ query: "test query" }), {
      query: "old query",
    } as QueryParams);
    expect(posthog.init).toHaveBeenCalledWith("key", { api_host: "host" });
    expect(store.postHog?.capture).toHaveBeenCalledWith("search_performed", {
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
    const store = usePostHogStore();
    store.setTracking(true);
    store.userConsent = false;
    store.searchPerformed("simple", addDefaults({ query: "test query" }));
    expect(store.postHog?.capture).not.toHaveBeenCalled();
  });

  it("captures search_result_clicked event when postHog exists and user consent is given", () => {
    const store = usePostHogStore();
    const router = useRouter();
    router.currentRoute.value.query = { query: "test query" };
    store.setTracking(true);
    const captureSpy = vi.spyOn(store.postHog as PostHog, "capture");
    store.searchResultClicked("testUrl", 1);
    expect(captureSpy).toHaveBeenCalledWith("search_result_clicked", {
      url: "testUrl",
      order: 1,
      searchParams: { query: "test query" },
    });
  });

  it("captures no_search_results event when postHog exists and user consent is given", () => {
    const store = usePostHogStore();
    store.setTracking(true);
    const router = useRouter();
    router.currentRoute.value.query = { query: "test query" };
    const captureSpy = vi.spyOn(store.postHog as PostHog, "capture");
    store.noSearchResults();
    expect(captureSpy).toHaveBeenCalledWith("no_search_results", {
      searchParams: { query: "test query" },
    });
  });
});
