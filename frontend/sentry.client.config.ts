import * as Sentry from "@sentry/nuxt";
import { getStringOrDefault } from "./src/utils/textFormatting";

Sentry.init({
  dsn: useRuntimeConfig().public.sentryDSN,
  tracesSampleRate: 1,
  release: getStringOrDefault(process.env.SENTRY_RELEASE, "default-release"),
  environment: getStringOrDefault(process.env.NODE_ENV, "development"),
});
