import * as Sentry from "@sentry/nuxt";
import { isStringEmpty, getStringOrDefault } from "~/utils/textFormatting";

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
  tracesSampleRate: 1.0,
  debug: false,
  release, // this will be set from the CI/CD as the commit SHA
  environment,
});
