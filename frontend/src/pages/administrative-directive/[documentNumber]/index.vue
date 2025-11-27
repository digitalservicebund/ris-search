<script setup lang="ts">
import { computed } from "vue";
import DocumentDetailPage from "~/components/DocumentDetailPage.vue";
import { type AdministrativeDirective, DocumentKind } from "~/types";
import {
  ADMINISTRATIVE_DIRECTIVE_TITLE_PLACEHOLDER,
  getAdministrativeDirectiveMetadataItems,
} from "~/utils/administrativeDirective";
import { formatDocumentKind } from "~/utils/displayValues";

definePageMeta({ layout: "base" }); // use "base" layout to allow for full-width tab backgrounds

const route = useRoute();
const documentNumber = route.params.documentNumber as string;
const documentMetadataUrl = `/v1/administrative-directive/${documentNumber}`;

const { data, error: metadataError } =
  await useRisBackend<AdministrativeDirective>(documentMetadataUrl);

const { data: html, error: contentError } = await useRisBackend<string>(
  `${documentMetadataUrl}.html`,
  {
    headers: { Accept: "text/html" },
  },
);

const title = computed(() => data.value?.headline);
const isEmptyDocument = false;
const breadcrumbItems = computed(() => [
  {
    label: formatDocumentKind(DocumentKind.AdministrativeDirective),
    route: `/search?category=${DocumentKind.AdministrativeDirective}`,
  },
  {
    label: title.value ?? ADMINISTRATIVE_DIRECTIVE_TITLE_PLACEHOLDER,
  },
]);

const metadataItems = computed(() =>
  getAdministrativeDirectiveMetadataItems(data.value),
);

if (metadataError?.value) {
  showError(metadataError.value);
}
if (contentError?.value) {
  showError(contentError.value);
}
</script>

<template>
  <DocumentDetailPage
    :title="title"
    :title-placeholder="ADMINISTRATIVE_DIRECTIVE_TITLE_PLACEHOLDER"
    :is-empty-document="isEmptyDocument"
    :breadcrumb-items="breadcrumbItems"
    :metadata-items="metadataItems"
    document-html-class="administrative-directive"
    :html="html"
  >
    <template #details>
      <h2 id="detailsTabPanelTitle" class="ris-heading3-bold my-24">Details</h2>
      <IncompleteDataMessage class="my-24" />
      <Properties aria-labelledby="detailsTabPanelTitle">
        <PropertiesItem label="Detail" value="Coming soon" />
      </Properties>
    </template>
  </DocumentDetailPage>
</template>
