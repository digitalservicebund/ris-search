import * as Sentry from "@sentry/nuxt";

const config = useRuntimeConfig();

if (config.public.sentryDSN) {
  Sentry.init({
    dsn: config.public.sentryDSN,
    environment: config.public.sentryEnvironment,
    tracesSampleRate: 1,

    // Errors thrown via `createError` (e.g. 404s) surface through Nuxt's
    // `vue:error` hook, which the Sentry SDK reports without filtering by status
    // code (unlike its `app:error` hook). Drop client errors here so they don't
    // reach Sentry. Client-only: the server side already filters these via the
    // Nitro error handler.
    beforeSend(event, hint) {
      const error = hint.originalException;
      const status = isNuxtError(error) ? error.status : undefined;
      const shouldSuppress =
        status !== undefined && status >= 300 && status < 500;

      return shouldSuppress ? null : event;
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
