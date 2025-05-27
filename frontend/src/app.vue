<script setup lang="ts">
useHead({
  titleTemplate: (pageTitle) =>
    pageTitle
      ? `${pageTitle} | Rechtsinformationen des Bundes`
      : "Rechtsinformationen des Bundes",
});
const script = useScript(
  "https://js.sentry-cdn.com/9fb90db5a19e625b82c4d9b52dd80fa8.min.js",
);

declare global {
  const Sentry: { init: (options: Record<string, unknown>) => void };
}

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
</script>

<template>
  <NuxtLayout>
    <NuxtPage />
  </NuxtLayout>
</template>
