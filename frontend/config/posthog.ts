import type { NuxtConfig, NuxtModule } from "nuxt/schema";
import { CONSENT_COOKIE_NAME } from "../src/utils/postHog";

export const posthogConfig: NuxtConfig["posthogConfig"] = {
  publicKey: "",
  host: "",

  clientConfig: {
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
};

/**
 * The PostHog module replaces an empty host with PostHog's US cloud at build
 * time. Reset it, so PostHog stays disabled unless a host is configured at
 * runtime (see `plugins/postHogGuard.client.ts`).
 */
export const resetPostHogHostDefault: NuxtModule = (_, nuxt) => {
  nuxt.options.runtimeConfig.public.posthog.host = "";
};
