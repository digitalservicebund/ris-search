/**
 * Composable for reading and changing the user's tracking consent. PostHog
 * persists the decision in a cookie, which is also read during SSR so the
 * consent state is known before the app is hydrated.
 */
export function useConsent() {
  const { publicKey, host } = useRuntimeConfig().public.posthog;
  const consentCookie = useCookie(CONSENT_COOKIE_NAME, { readonly: true });
  const posthog = usePostHog();

  const isPostHogEnabled = !!publicKey && !!host;

  const consentStatus = useState<ConsentStatus>("consentStatus", () =>
    parseConsentCookie(consentCookie.value),
  );

  const isConsentGiven = computed(() => consentStatus.value === "granted");

  /**
   * Whether the consent banner should be shown to the user. True once PostHog
   * is enabled, and no consent decision has been made yet.
   */
  const isBannerVisible = computed(
    () => isPostHogEnabled && consentStatus.value === "pending",
  );

  /**
   * Opts in to or out of tracking. PostHog persists the decision.
   *
   * @param isGranted - Whether the user accepted tracking
   */
  function setConsent(isGranted: boolean) {
    consentStatus.value = isGranted ? "granted" : "denied";
    if (!isPostHogEnabled) return;

    if (isGranted) posthog?.opt_in_capturing({ captureEventName: false });
    else posthog?.opt_out_capturing();
  }

  return {
    consentStatus,
    isConsentGiven,
    isBannerVisible,
    isPostHogEnabled,
    setConsent,
  };
}
