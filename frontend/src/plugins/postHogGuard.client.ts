/**
 * Disables PostHog if no host is configured. PostHog would otherwise fall back
 * to its US cloud. PostHog doesn't initialize without a key, so removing the
 * key before the PostHog plugin runs keeps it from sending any data.
 */
export default defineNuxtPlugin({
  name: "posthog-guard",
  enforce: "pre",
  setup() {
    const { posthog } = useRuntimeConfig().public;
    if (!posthog.host) posthog.publicKey = "";
  },
});
