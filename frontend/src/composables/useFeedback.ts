const ANONYMOUS_FEEDBACK_USER = "anonymous_feedback_user";

/** Composable for sending user feedback. */
export function useFeedback() {
  const router = useRouter();
  const posthog = usePostHog();
  const { isConsentGiven, isPostHogEnabled } = useConsent();

  /** Returns the user's PostHog ID if they consented to tracking. */
  function getUserId() {
    if (!isPostHogEnabled || !isConsentGiven.value) {
      return ANONYMOUS_FEEDBACK_USER;
    }

    return posthog?.get_distinct_id() ?? ANONYMOUS_FEEDBACK_USER;
  }

  /**
   * Sends user feedback to the backend.
   *
   * @param text - The feedback text from the user
   * @param honeypot - A field used for reducing spam sent to posthog
   * @throws Error if the backend request fails
   */
  async function sendFeedback(text: string, honeypot: string) {
    const { error } = await useRisBackend(`/v1/feedback`, {
      method: "POST",
      body: {
        text: text,
        url: router.currentRoute.value.fullPath,
        user_id: getUserId(),
        name: honeypot,
      },
    });

    if (error.value) {
      throw new Error(`Error sending feedback`);
    }
  }

  return { sendFeedback };
}
