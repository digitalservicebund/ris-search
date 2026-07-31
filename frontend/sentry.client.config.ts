import * as Sentry from "@sentry/nuxt";

const config = useRuntimeConfig();

if (config.public.sentryDSN) {
  Sentry.init({
    dsn: config.public.sentryDSN,
    environment: config.public.sentryEnvironment,
    tracesSampleRate: 1,

    beforeSend(event, hint) {
      const error = hint.originalException;
      const shouldSuppress = isNuxtError(error) && error.status === 404;

      return shouldSuppress ? null : event;
    },
  });
}
