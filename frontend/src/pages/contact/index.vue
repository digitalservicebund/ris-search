<script setup lang="ts">
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

      <StaticContentContactDetails />
    </NuxtLayout>
  </div>
</template>
