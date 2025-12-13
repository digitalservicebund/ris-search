<script setup lang="ts">
import ContactDetails from "~/components/ContactDetails.vue";
import { useStaticPageSeo } from "~/composables/useStaticPageSeo";

definePageMeta({ alias: ["/kontakt"], layout: false });

useStaticPageSeo("kontakt");
</script>

<template>
  <div>
    <NuxtLayout name="static-page">
      <template #breadcrumb>
        <Breadcrumbs :items="[{ label: 'Kontakt' }]" />
      </template>

      <template #pageTitle>Kontakt</template>

      <ContactDetails />
    </NuxtLayout>
  </div>
</template>
