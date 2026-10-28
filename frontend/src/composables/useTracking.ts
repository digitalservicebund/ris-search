import { DocumentKind } from "~/types/api";

/**
 * Names of the document kinds in tracked events. They match the routes of the
 * document detail pages.
 */
export const trackingDocumentKinds = {
  [DocumentKind.All]: "alle",
  [DocumentKind.CaseLaw]: "gerichtsentscheidungen",
  [DocumentKind.Norm]: "gesetze",
  [DocumentKind.Literature]: "literaturnachweise",
  [DocumentKind.AdministrativeDirective]: "verwaltungsregelungen",
} as const satisfies Record<DocumentKind, string>;

type TrackingDocumentKind = (typeof trackingDocumentKinds)[DocumentKind];

/** Custom events sent to PostHog, with the properties of each event. */
export interface TrackingEvents {
  /** The user started a search in the simple search */
  search_submitted: {
    query: string;
    document_kind: TrackingDocumentKind;
  };
}

/**
 * Composable for sending custom events to PostHog. Events are only captured
 * once the user has given their consent: until then, PostHog discards them.
 */
export function useTracking() {
  const { publicKey } = useRuntimeConfig().public.posthog;
  const posthog = usePostHog();

  /**
   * Sends a custom event to PostHog. Does nothing if PostHog is disabled or
   * during SSR.
   *
   * @param event - Name of the event
   * @param properties - Properties of the event
   */
  function track<T extends keyof TrackingEvents>(
    event: T,
    properties: TrackingEvents[T],
  ) {
    if (!publicKey) return;
    posthog?.capture(event, properties);
  }

  return { track };
}
