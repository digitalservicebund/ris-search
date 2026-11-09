/** Composable for sending user feedback. */
export function useFeedback() {
  const router = useRouter();

  /**
   * Sends user feedback to the backend. Feedback is always anonymous.
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
        name: honeypot,
      },
    });

    if (error.value) {
      throw new Error(`Error sending feedback`);
    }
  }

  return { sendFeedback };
}
