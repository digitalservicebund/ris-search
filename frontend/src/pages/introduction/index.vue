<script setup lang="ts">
import StaticPageWrapper from "~/components/CustomLayouts/StaticPageWrapper.vue";
definePageMeta({ alias: ["/einfuehrung"] });
</script>

<template>
  <StaticPageWrapper>
    <template #breadcrumb>
      <RisBreadcrumb :items="[{ label: 'Einführende Informationen' }]" />
    </template>
    <PageHeader title="Einführende Informationen" />
  </StaticPageWrapper>
</template>
