import Cookies from "js-cookie";
import { defineStore } from "pinia";
import posthog from "posthog-js";
import type { PostHog } from "posthog-js";
import type { QueryParams } from "~/stores/searchParams";
import { getStringOrUndefined, stringToBoolean } from "~/utils/textFormatting";

const CONSENT_COOKIE_NAME = "consent_given";

export const usePostHogStore = defineStore("postHog", () => {
  const config = useRuntimeConfig();
  const router = useRouter();
  const key = getStringOrUndefined(config.public.analytics.posthogKey);
  const host = getStringOrUndefined(config.public.analytics.posthogHost);
  const surveyId = getStringOrUndefined(
    config.public.analytics.feedbackSurveyId,
  );
  const postHog: Ref<PostHog | undefined> = ref(undefined);
  const userConsent: Ref<boolean | undefined> = ref(undefined);
  const isBannerVisible = computed(() => {
    const isPostHogEnabled = !isStringEmpty(key) && !isStringEmpty(host);
    const isUserConsentGiven = userConsent.value !== undefined;
    return isPostHogEnabled && !isUserConsentGiven;
  });

  function initialize() {
    userConsent.value = stringToBoolean(Cookies.get(CONSENT_COOKIE_NAME));
    if (userConsent.value === true) {
      activatePostHog();
    }
    if (userConsent.value === false && postHog.value) {
      deactivatePostHog();
    }
  }

  function initializePostHog() {
    if (key && host) {
      postHog.value = posthog.init(key, { api_host: host });
    }
  }
  function activatePostHog() {
    initializePostHog();
    postHog.value?.opt_in_capturing();
  }
  function deactivatePostHog() {
    postHog.value?.opt_out_capturing();
    postHog.value?.clear_opt_in_out_capturing();
    postHog.value = undefined;
    const cookies = Cookies.get();
    if (cookies) {
      Object.keys(cookies).forEach((key) => {
        if (key.startsWith("ph_")) {
          Cookies.remove(key, { path: "/" }); // passing the root path is important here, see https://stackoverflow.com/a/61735396/3357175
        }
      });
    }
  }

  function getUserPostHogId() {
    const cookies = Cookies.get();
    const phCookieString = cookies?.[`ph_${key}_posthog`] ?? "{}";
    const phCookieObject = JSON.parse(phCookieString) as Record<string, string>;
    return phCookieObject.distinct_id ?? "anonymous_feedback_user";
  }

  async function sendFeedbackToPostHog(text: string) {
    if (!host || !key || !surveyId) {
      throw new Error("PostHog configuration is missing!");
    }
    const backendURL = useBackendURL();
    const params = new URLSearchParams({
      text: text,
      url: router.currentRoute.value.fullPath,
      user_id: getUserPostHogId(),
      ph_host: host,
      ph_key: key,
      ph_survey_id: surveyId,
    });
    const result = await useFetch(
      `${backendURL}/v1/feedback?${params.toString()}`,
    );

    if (!result) {
      throw new Error(`Error sending feedback`);
    }
  }
  function setTracking(userHasAccepted: boolean) {
    Cookies.set(CONSENT_COOKIE_NAME, userHasAccepted.toString(), {
      expires: 365,
    });
    userConsent.value = userHasAccepted;
    if (userHasAccepted) {
      activatePostHog();
    } else {
      deactivatePostHog();
    }
  }
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
  function searchResultClicked(url: string, order: number) {
    if (postHog.value && userConsent.value === true) {
      postHog.value.capture("search_result_clicked", {
        url: url,
        order: order,
        searchParams: router.currentRoute.value.query,
      });
    }
  }
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
});
