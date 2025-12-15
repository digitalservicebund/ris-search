<script setup lang="ts">
import { Button } from "primevue";
import { ExternalLink } from "#components";
import { useStaticPageSeo } from "~/composables/useStaticPageSeo";

definePageMeta({ layout: false });

useStaticPageSeo("open-source");
</script>

<template>
  <div>
    <NuxtLayout name="static-page">
      <template #breadcrumb>
        <Breadcrumbs :items="[{ label: 'Open Source' }]" />
      </template>

      <template #pageTitle>Open Source</template>

      <div class="space-y-24">
        <p>
          Bei der Entwicklung setzen wir vollständig auf Open Source. Open
          Source bedeutet für uns Transparenz, Vertrauen, Sicherheit, Qualität
          und Flexibilität. Aus diesem Grund ist unsere Arbeit in unserem
          Github-Repository frei zugänglich und offen für Mitarbeit.
        </p>

        <Button
          :as="ExternalLink"
          url="https://github.com/digitalservicebund/ris-search"
        >
          Zum Github Repository
        </Button>
      </div>
    </NuxtLayout>
  </div>
</template>
