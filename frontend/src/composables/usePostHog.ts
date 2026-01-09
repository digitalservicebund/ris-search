import Cookies from "js-cookie";
import type { PostHog } from "posthog-js";
import posthog from "posthog-js";
import type { QueryParams } from "~/composables/useSimpleSearchParams/useSimpleSearchParams";
import {
  getStringOrUndefined,
  isStringEmpty,
  stringToBoolean,
} from "~/utils/textFormatting";

const CONSENT_COOKIE_NAME = "consent_given";

// Declared on module level rather than inside of `usePostHog` to ensure state
// is shared across all usage of the composable, rather than declaring them again
// every time the composable is used.
const postHog = ref<PostHog | undefined>(undefined);
const userConsent = ref<boolean | undefined>(undefined);

/** Resets the module-level state. Only exported for testing purposes. */
export function resetPostHogState() {
  postHog.value = undefined;
  userConsent.value = undefined;
}

/** Composable for managing PostHog analytics and user consent. */
export function usePostHog() {
  const config = useRuntimeConfig();
  const router = useRouter();
  const key = getStringOrUndefined(config.public.analytics.posthogKey);
  const host = getStringOrUndefined(config.public.analytics.posthogHost);

  /**
   * Whether the consent banner should be shown to the user. True once PostHog is
   * enabled, and no consent decision has been made yet.
   */
  const isBannerVisible = computed(() => {
    const isPostHogEnabled = !isStringEmpty(key) && !isStringEmpty(host);
    const isUserConsentGiven = userConsent.value !== undefined;
    return isPostHogEnabled && !isUserConsentGiven;
  });

  /**
   * Initializes the PostHog state. This should only be called once when the
   * application starts.
   */
  function initialize() {
    userConsent.value = stringToBoolean(Cookies.get(CONSENT_COOKIE_NAME));
    if (userConsent.value === true) {
      activatePostHog();
    }
    if (userConsent.value === false && postHog.value) {
      deactivatePostHog();
    }
  }

  /** Initializes the PostHog SDK if configured. */
  function initializePostHog() {
    if (key && host) {
      postHog.value = posthog.init(key, { api_host: host });
    }
  }

  /** Activates PostHog tracking. */
  function activatePostHog() {
    initializePostHog();
    postHog.value?.opt_in_capturing();
  }

  /** Deactivates PostHog tracking and clears all PostHog cookies. */
  function deactivatePostHog() {
    postHog.value?.opt_out_capturing();
    postHog.value?.clear_opt_in_out_capturing();
    postHog.value = undefined;
    const cookies = Cookies.get();
    if (cookies) {
      for (const key of Object.keys(cookies)) {
        if (key.startsWith("ph_")) {
          Cookies.remove(key, { path: "/" });
        }
      }
    }
  }

  /** Retrieves the user's PostHog distinct ID from cookies. */
  function getUserPostHogId() {
    const cookies = Cookies.get();
    const phCookieString = cookies?.[`ph_${key}_posthog`] ?? "{}";
    const phCookieObject = JSON.parse(phCookieString) as Record<string, string>;
    return phCookieObject.distinct_id ?? "anonymous_feedback_user";
  }

  /**
   * Sends user feedback to the backend.
   *
   * @param text - The feedback text from the user
   * @throws Error if the backend request fails
   */
  async function sendFeedbackToPostHog(text: string) {
    const params = new URLSearchParams({
      text: text,
      url: router.currentRoute.value.fullPath,
      user_id: getUserPostHogId(),
    });
    const { error } = await useRisBackend(`/v1/feedback?${params.toString()}`);

    if (error.value) {
      throw new Error(`Error sending feedback`);
    }
  }

  /**
   * Sets the user's tracking consent and persists it in a cookie.
   *
   * @param userHasAccepted - Whether the user accepted tracking
   */
  function setTracking(userHasAccepted: boolean) {
    const isDevMode = process.env.NODE_ENV === "development";
    Cookies.set(CONSENT_COOKIE_NAME, userHasAccepted.toString(), {
      expires: 365,
      path: "/",
      sameSite: "lax",
      secure: !isDevMode,
    });
    userConsent.value = userHasAccepted;
    if (userHasAccepted) {
      activatePostHog();
    } else {
      deactivatePostHog();
    }
  }

  /**
   * Tracks a search event in PostHog.
   *
   * @param type - Simple or advanced search
   * @param newParams - The current search parameters
   * @param previousParams - The previous search parameters for comparison
   */
  function searchPerformed(
    type: "simple" | "advanced",
    newParams?: Partial<QueryParams>,
    previousParams?: Partial<QueryParams>,
  ) {
    if (postHog.value && userConsent.value === true) {
      postHog.value.capture("search_performed", {
        type: type,
        newParams: newParams,
        previousParams: previousParams,
      });
    }
  }

  /**
   * Tracks when a user clicks on a search result.
   *
   * @param url - The URL of the clicked result
   * @param order - The position of the result in the list
   */
  function searchResultClicked(url: string, order: number) {
    if (postHog.value && userConsent.value === true) {
      postHog.value.capture("search_result_clicked", {
        url: url,
        order: order,
        searchParams: router.currentRoute.value.query,
      });
    }
  }

  /** Tracks when a search returns no results. */
  function noSearchResults() {
    if (postHog.value && userConsent.value === true) {
      postHog.value.capture("no_search_results", {
        searchParams: router.currentRoute.value.query,
      });
    }
  }

  return {
    userConsent,
    isBannerVisible,
    postHog,
    searchPerformed,
    searchResultClicked,
    noSearchResults,
    initialize,
    setTracking,
    sendFeedbackToPostHog,
  };
}
