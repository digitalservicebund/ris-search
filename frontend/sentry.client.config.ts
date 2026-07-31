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
  });
}
