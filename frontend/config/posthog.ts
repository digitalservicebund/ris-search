import type { NuxtConfig } from "nuxt/schema";
import { CONSENT_COOKIE_NAME } from "../src/utils/postHog";

/**
 * PostHog EU cloud. Hardcoded so it can't be forgotten or misconfigured. Also
 * set in the client and server config, because those take precedence over the
 * host from the runtime config, which `NUXT_PUBLIC_POSTHOG_HOST` could
 * override.
 */
const POSTHOG_HOST = "https://eu.i.posthog.com";

export const posthogConfig: NuxtConfig["posthogConfig"] = {
  publicKey: "",
  host: POSTHOG_HOST,

  clientConfig: {
    api_host: POSTHOG_HOST,

    // Consent
    opt_out_capturing_by_default: true,
    opt_out_persistence_by_default: true,
    opt_out_capturing_persistence_type: "cookie",
    consent_persistence_name: CONSENT_COOKIE_NAME,
    cross_subdomain_cookie: false,

    // Page views
    capture_pageview: "history_change",
    capture_pageleave: false,

    // Everything else
    autocapture: false,
    rageclick: false,
    capture_dead_clicks: false,
    capture_heatmaps: false,
    capture_exceptions: false,
    capture_performance: false,
    disable_session_recording: true,
    disable_surveys: true,
    disable_conversations: true,
    disable_product_tours: true,
    advanced_disable_flags: true,
  },

  serverConfig: {
    host: POSTHOG_HOST,
  },
};
