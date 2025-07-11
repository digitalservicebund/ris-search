<script setup lang="ts">
import StaticPageWrapper from "~/components/CustomLayouts/StaticPageWrapper.vue";
import ContactDetails from "~/pages/contact/ContactDetails.vue";

useHead({ title: "Kontakt" });
definePageMeta({ alias: ["/kontakt"] });
</script>

<template>
  <StaticPageWrapper>
    <RisBreadcrumb title="Kontakt" base-path="/" />
    <PageHeader title="Kontakt" />
    <main class="mb-80">
      <ContactDetails />
    </main>
  </StaticPageWrapper>
</template>
