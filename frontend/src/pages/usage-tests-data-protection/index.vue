<script setup lang="ts">
import text from "./content.md?raw";

definePageMeta({
  alias: ["/nutzungstests-datenschutz"],
  layout: false,
});

useStaticPageSeo("nutzungstests-datenschutz");
</script>

<template>
  <NuxtLayout name="markdown-page" :static-content="text">
    <template #breadcrumb>
      <Breadcrumbs
        :items="[
          {
            label:
              'Datenschutzerklärung für die Registrierung für Nutzungsstudien für das Projekt Testphase Rechtsinformationsportal',
          },
        ]"
      />
    </template>
  </NuxtLayout>
</template>
