<script setup lang="ts">
import StaticPageWrapper from "~/components/CustomLayouts/StaticPageWrapper.vue";
import { useStaticPageSeo } from "~/composables/useStaticPageSeo";
import ContactDetails from "~/pages/contact/ContactDetails.vue";

definePageMeta({ alias: ["/kontakt"] });

useStaticPageSeo("kontakt");
</script>

<template>
  <StaticPageWrapper>
    <template #breadcrumb>
      <RisBreadcrumb title="Kontakt" base-path="/" />
    </template>
    <PageHeader title="Kontakt" />
    <div class="mb-80">
      <ContactDetails />
    </div>
  </StaticPageWrapper>
</template>
