<script setup lang="ts">
import ContactDetails from "~/components/ContactDetails.vue";
import StaticPageWrapper from "~/components/CustomLayouts/StaticPageWrapper.vue";
import { useStaticPageSeo } from "~/composables/useStaticPageSeo";
definePageMeta({ alias: ["/kontakt"] });

useStaticPageSeo("kontakt");
</script>

<template>
  <StaticPageWrapper>
    <template #breadcrumb>
      <RisBreadcrumb :items="[{ label: 'Kontakt' }]" />
    </template>
    <PageHeader title="Kontakt" />
    <div class="mb-80">
      <ContactDetails />
    </div>
  </StaticPageWrapper>
</template>
