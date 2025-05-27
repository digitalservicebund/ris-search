/*
The client-side Sentry module is imported using useScript instead of the Sentry Nuxt module.
The Sentry Nuxt module could not be used together with the strict CSP configuration of nuxt-security
since its client-side script was added without a nonce.
*/

declare global {
  const Sentry: { init: (options: Record<string, unknown>) => void };
}

export function useSentry() {
  const script = useScript(
    "https://js.sentry-cdn.com/9fb90db5a19e625b82c4d9b52dd80fa8.min.js",
  );

  script.onLoaded(() => {
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
  });
}
