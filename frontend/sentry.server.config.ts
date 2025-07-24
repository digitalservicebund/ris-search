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
  // We must source the .env variable in the instance of the app that runs on the server for this to work
  // It has to be in the environment of the server when building the app
  // See: https://nuxt.com/docs/guide/directory-structure/env#production
  dsn,
  tracesSampleRate: 0.1,
  debug: false,
  release, // this will be set from the CI/CD as the commit SHA
  beforeSend: (event) =>
    event.level === "error" || event.level === "fatal" ? event : null,
  environment,
});
