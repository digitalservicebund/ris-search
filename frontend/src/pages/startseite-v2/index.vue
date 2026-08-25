<script setup lang="ts">
definePageMeta({
  skipLinks: [
    { label: "Zum Inhalt", to: "#main" },
    { label: "Zum Fußbereich", to: "#footer" },
  ],
  middleware: () => {
    // For some reason our private feature flag composable doesn't work in this
    // context, falling back to the runtime config directly instead
    const config = useRuntimeConfig();
    if (!config.public.privateFeaturesEnabled)
      return navigateTo({ name: "index" });
  },
});

useSeo({
  title: "Schneller und direkter Zugang zu Rechtsinformationen",
  description:
    "Nutzen Sie das neue Rechtsinformationsportal des Bundes – Gesetze, Verordnungen und Urteile auf einen Blick.",
  ogTitle: "Rechtsinformationen des Bundes",
});
</script>

<template>
  <div class="content-wrapper my-24">🚧 Under construction</div>
</template>

<style scoped>
@reference "~/assets/main.css";
</style>
