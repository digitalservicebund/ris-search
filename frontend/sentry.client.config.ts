import * as Sentry from "@sentry/nuxt";

const config = useRuntimeConfig();

if (config.public.sentryDSN) {
  Sentry.init({
    dsn: config.public.sentryDSN,
    tracesSampleRate: 1,
  });
}
