import type { PostHog } from "posthog-js";
import { posthog } from "posthog-js";
import type { QueryParams } from "~/composables/useSimpleSearchParams/useSimpleSearchParams";
import {
  getStringOrUndefined,
  isStringEmpty,
  stringToBoolean,
} from "~/utils/textFormatting";

export const CONSENT_COOKIE_NAME = "consent_given";

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
  async function initialize() {
    const cookie = await cookieStore.get(CONSENT_COOKIE_NAME);
    userConsent.value = cookie ? stringToBoolean(cookie.value) : undefined;
    if (userConsent.value) activatePostHog();
    else await deactivatePostHog();
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
  async function deactivatePostHog() {
    postHog.value?.opt_out_capturing();
    postHog.value?.clear_opt_in_out_capturing();
    postHog.value = undefined;

    (await cookieStore.getAll()).forEach(({ name }) => {
      if (name?.startsWith("ph_")) cookieStore.delete({ name, path: "/" });
    });
  }

  /** Retrieves the user's PostHog distinct ID from cookies. */
  async function getUserPostHogId() {
    if (import.meta.server || typeof cookieStore === "undefined" || !key) {
      return "anonymous_feedback_user";
    }

    try {
      const cookie = await cookieStore.get(`ph_${key}_posthog`);
      if (cookie?.value) {
        const phCookieObject = JSON.parse(cookie.value);
        return phCookieObject.distinct_id ?? "anonymous_feedback_user";
      }
    } catch (exception) {
      console.warn("PostHog cookie parsing failed:", exception);
    }

    return "anonymous_feedback_user";
  }

  /**
   * Sends user feedback to the backend.
   *
   * @param text - The feedback text from the user
   * @param honeypot - A field used for reducing spam sent to posthog
   * @throws Error if the backend request fails
   */
  async function sendFeedbackToPostHog(text: string, honeypot: string) {
    const userId = await getUserPostHogId();

    const params = new URLSearchParams({
      text: text,
      url: router.currentRoute.value.fullPath,
      user_id: userId,
      name: honeypot,
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
  async function setTracking(userHasAccepted: boolean) {
    const expiresAt = new Date();
    expiresAt.setFullYear(expiresAt.getFullYear() + 1);

    await cookieStore.set({
      name: CONSENT_COOKIE_NAME,
      value: userHasAccepted.toString(),
      expires: expiresAt.getTime(),
      path: "/",
      sameSite: "lax",
    });

    userConsent.value = userHasAccepted;
    if (userHasAccepted) activatePostHog();
    else await deactivatePostHog();
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
