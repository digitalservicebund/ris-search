import * as Sentry from "@sentry/nuxt";

const config = useRuntimeConfig();

if (config.public.sentryDSN) {
  Sentry.init({
    dsn: config.public.sentryDSN,
    environment: config.public.sentryEnvironment,
    tracesSampleRate: 1,

    // Errors thrown via `createError` (e.g. 404s) surface through Nuxt's
    // `vue:error` hook, which the Sentry SDK reports without filtering by status
    // code at all, and through its `app:error` hook, which only filters errors
    // it recognizes as Nuxt errors. Drop client errors here so they don't reach
    // Sentry. Client-only: the server side already filters these via the Nitro
    // error handler.
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
