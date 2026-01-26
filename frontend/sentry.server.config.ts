import * as Sentry from "@sentry/nuxt";
import { getStringOrDefault } from "./src/utils/textFormatting";

Sentry.init({
  // We must source the .env variable in the instance of the app that runs on
  // the server for this to work. It has to be in the environment of the server
  // when building the app. See:
  // https://nuxt.com/docs/guide/directory-structure/env#production
  dsn: process.env.NUXT_PUBLIC_SENTRY_DSN,
  sampleRate: 0.5,
  release: getStringOrDefault(process.env.SENTRY_RELEASE, "default-release"),
  environment: getStringOrDefault(process.env.NODE_ENV, "development"),

  beforeSend: (event) =>
    event.level === "error" || event.level === "fatal" ? event : null,
});
