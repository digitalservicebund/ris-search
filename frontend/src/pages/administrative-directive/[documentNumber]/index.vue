<script setup lang="ts">
import { computed } from "vue";
import DocumentDetailPage from "~/components/Ris/DocumentDetailPage.vue";

definePageMeta({ layout: "base" }); // use "base" layout to allow for full-width tab backgrounds

const title = "Verwaltungsvorschrift";
const isEmptyDocument = false;
const breadcrumbItems = computed(() => [
  {
    label: "Verwaltungsvorschriften",
    route: `/search?category=V`,
  },
  {
    label: "Verwaltungsvorschrift",
  },
]);
</script>

<template>
  <DocumentDetailPage
    :title="title"
    title-placeholder="Titelzeile nicht vorhanden"
    :is-empty-document="isEmptyDocument"
    :breadcrumb-items="breadcrumbItems"
    document-html-class=""
    html="<h2>Coming soon</h2>"
  >
    <template #metadata>
      <div class="mb-48 flex flex-row flex-wrap gap-24">
        <MetadataField label="Metadaten" value="coming soon" />
      </div>
    </template>
    <template #details>
      <h2 id="detailsTabPanelTitle" class="ris-heading3-bold my-24">Details</h2>
      <IncompleteDataMessage class="my-24" />
      <Properties aria-labelledby="detailsTabPanelTitle">
        <PropertiesItem label="Detail" value="Coming soon" />
      </Properties>
    </template>
  </DocumentDetailPage>
</template>
