import * as Sentry from "@sentry/nuxt";
import { getStringOrDefault, isStringEmpty } from "~/utils/textFormatting";

const dsn = process.env.NUXT_PUBLIC_SENTRY_DSN;
const release = getStringOrDefault(
  process.env.SENTRY_RELEASE,
  "default-release",
);
const environment = getStringOrDefault(process.env.NODE_ENV, "development");
const enabled = !isStringEmpty(dsn);

Sentry.init({
  enabled,
  dsn,
  tracesSampleRate: 1.0,
  debug: false,
  release,
  environment,
});
