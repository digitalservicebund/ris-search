import * as Sentry from "@sentry/nuxt";

const config = useRuntimeConfig();

if (config.public.sentryDSN) {
  Sentry.init({
    dsn: config.public.sentryDSN,
    environment: config.public.sentryEnvironment,
    tracesSampleRate: 1,

    // Drops error noise, see `shouldSuppressSentryEvent`:
    beforeSend(event, hint) {
      return shouldSuppressSentryEvent(hint.originalException) ? null : event;
    },

    ignoreErrors: [
      // Client-side chunk loading errors (e.g. after a deployment invalidates
      // old hashed chunk URLs) are already handled by Nuxt's
      // `nuxt:chunk-reload` plugin, which performs a hard reload. Sentry
      // reports them as unhandles errors even though the user is not affected.
      // Drop them as noise. The wording differs per browser.
      //
      // See https://nuxt.com/docs/4.x/getting-started/error-handling#errors-with-js-chunks
      /Failed to fetch dynamically imported module/, // Chrome
      /error loading dynamically imported module/, // Firefox
      /Importing a module script failed/, // Safari
    ],
  });
}
