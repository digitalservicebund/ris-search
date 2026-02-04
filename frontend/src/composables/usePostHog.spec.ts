import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import type { PostHog } from "posthog-js";
import { posthog } from "posthog-js";
import { beforeEach, describe, expect, it, vi } from "vitest";
import {
  resetPostHogState,
  usePostHog,
  type PostHogSearchParams,
} from "~/composables/usePostHog";
import { cookieStoreBackend, cookieStoreMock } from "~/tests/cookieStoreMock";
import { getPostHogConfig } from "~/tests/postHogUtils";
import { DocumentKind } from "~/types";

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
  posthog: {
    init: vi.fn().mockReturnValue(postHogMock),
  },
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
    cookieStoreBackend.clear();
    vi.clearAllMocks();
  });

  it("initializes postHog when userConsent is true", async () => {
    cookieStoreBackend.set("consent_given", {
      name: "consent_given",
      value: "true",
    });
    const { initialize, postHog } = usePostHog();
    await initialize();
    expect(posthog.init).toHaveBeenCalledWith("key", {
      api_host: "host",
    });
    expect(postHog.value).toBeDefined();
  });

  it("isBannerVisible returns true when PostHog is enabled and no consent cookie exists", () => {
    const { isBannerVisible } = usePostHog();
    expect(isBannerVisible.value).toBe(true);
  });

  it("isBannerVisible returns false when a consent cookie is present", async () => {
    cookieStoreBackend.set("consent_given", {
      name: "consent_given",
      value: "true",
    });
    const { isBannerVisible, initialize } = usePostHog();
    await initialize();
    expect(isBannerVisible.value).toBe(false);
  });

  it("userConsent returns the boolean value from the consent cookie", async () => {
    cookieStoreBackend.set("consent_given", {
      name: "consent_given",
      value: "false",
    });
    const { userConsent, initialize } = usePostHog();
    await initialize();
    expect(userConsent.value).toBe(false);
  });

  it("setTracking to false deactivates postHog state, sets cookie and opt out of capturing", async () => {
    cookieStoreBackend.set("ph_key_posthog", {
      name: "ph_key_posthog",
      value: "some cookie value",
    });
    const { setTracking, postHog, userConsent } = usePostHog();
    await setTracking(false);
    expect(postHog.value).toBeUndefined();
    expect(cookieStoreMock.set).toHaveBeenCalledWith(
      expect.objectContaining({
        name: "consent_given",
        value: "false",
        path: "/",
        sameSite: "lax",
      }),
    );
    expect(userConsent.value).toBe(false);
    expect(cookieStoreMock.delete).toHaveBeenCalledWith({
      name: "ph_key_posthog",
      path: "/",
    });
  });

  it("setTracking to true activates postHog state, sets cookie and opt in capturing", async () => {
    const { setTracking, postHog, userConsent } = usePostHog();
    await setTracking(true);
    expect(posthog.init).toHaveBeenCalledWith("key", {
      api_host: "host",
    });
    expect(postHog.value).toBeDefined();
    expect(cookieStoreMock.set).toHaveBeenCalledWith(
      expect.objectContaining({
        name: "consent_given",
        value: "true",
        path: "/",
        sameSite: "lax",
      }),
    );
    expect(userConsent.value).toBe(true);
    expect(postHog.value?.opt_in_capturing).toHaveBeenCalled();
  });

  it("sendFeedbackToPostHog sends the user feedback and tracking information to backend when user enables tracking", async () => {
    const { setTracking, sendFeedbackToPostHog } = usePostHog();
    cookieStoreBackend.set("ph_key_posthog", {
      name: "ph_key_posthog",
      value: '{"distinct_id":"12345"}',
    });
    await setTracking(true);
    await sendFeedbackToPostHog("good", "bot-trap-value");
    expect(useRisBackendMock).toHaveBeenCalledWith(
      feedbackURL + "?text=good&url=%2F&user_id=12345&name=bot-trap-value",
    );
  });

  it("sendFeedbackToPostHog sends the data to backend as anonymous user when the user disables tracking", async () => {
    const { setTracking, sendFeedbackToPostHog } = usePostHog();
    await setTracking(false);
    await sendFeedbackToPostHog("test", "");
    expect(useRisBackendMock).toHaveBeenCalledWith(
      feedbackURL + "?text=test&url=%2F&user_id=anonymous_feedback_user&name=",
    );
  });

  it("captures search event when postHog is initialized and user consent is given", async () => {
    const { setTracking, searchPerformed, postHog } = usePostHog();
    await setTracking(true);

    const params: PostHogSearchParams = {
      searchTerm: "test query",
      size: "10",
      pageIndex: 0,
      sort: "default",
      documentKind: DocumentKind.CaseLaw,
    };

    const previousParams: PostHogSearchParams = {
      searchTerm: "old query",
      size: "10",
      pageIndex: 0,
      sort: "default",
      documentKind: DocumentKind.CaseLaw,
    };

    searchPerformed("simple", params, previousParams);
    expect(posthog.init).toHaveBeenCalledWith("key", { api_host: "host" });
    expect(postHog.value?.capture).toHaveBeenCalledWith("search_performed", {
      type: "simple",
      newParams: {
        category: DocumentKind.CaseLaw,
        court: undefined,
        date: undefined,
        dateAfter: undefined,
        dateBefore: undefined,
        dateSearchMode: "",
        itemsPerPage: 10,
        pageNumber: 0,
        query: "test query",
        sort: "default",
      },
      previousParams: {
        category: DocumentKind.CaseLaw,
        court: undefined,
        date: undefined,
        dateAfter: undefined,
        dateBefore: undefined,
        dateSearchMode: "",
        itemsPerPage: 10,
        pageNumber: 0,
        query: "old query",
        sort: "default",
      },
    });
  });

  it("does not capture search event when user consent is not given", async () => {
    const { setTracking, searchPerformed, postHog, userConsent } = usePostHog();
    await setTracking(true);
    userConsent.value = false;
    searchPerformed("simple", {
      searchTerm: "test query",
      size: "10",
      pageIndex: 0,
      sort: "default",
      documentKind: DocumentKind.CaseLaw,
    });
    expect(postHog.value?.capture).not.toHaveBeenCalled();
  });

  it("captures search_result_clicked event when postHog exists and user consent is given", async () => {
    const { setTracking, searchResultClicked, postHog } = usePostHog();
    const router = useRouter();
    router.currentRoute.value.query = { query: "test query" };
    await setTracking(true);
    const captureSpy = vi.spyOn(postHog.value as PostHog, "capture");
    searchResultClicked("testUrl", 1);
    expect(captureSpy).toHaveBeenCalledWith("search_result_clicked", {
      url: "testUrl",
      order: 1,
      searchParams: { query: "test query" },
    });
  });

  it("captures no_search_results event when postHog exists and user consent is given", async () => {
    const { setTracking, noSearchResults, postHog } = usePostHog();
    await setTracking(true);

    const params: PostHogSearchParams = {
      searchTerm: "test query",
      size: "10",
      pageIndex: 0,
      sort: "default",
      documentKind: DocumentKind.CaseLaw,
    };

    const captureSpy = vi.spyOn(postHog.value as PostHog, "capture");
    noSearchResults(params);
    expect(captureSpy).toHaveBeenCalledWith("no_search_results", {
      searchParams: {
        category: DocumentKind.CaseLaw,
        court: undefined,
        date: undefined,
        dateAfter: undefined,
        dateBefore: undefined,
        dateSearchMode: "",
        itemsPerPage: 10,
        pageNumber: 0,
        query: "test query",
        sort: "default",
      },
    });
  });
});
