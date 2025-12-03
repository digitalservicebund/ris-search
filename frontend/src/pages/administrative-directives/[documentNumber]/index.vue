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
const isEmptyDocument = isAdministrativeDirectiveEmpty(data.value);
const breadcrumbItems = computed(() => [
  {
    label: formatDocumentKind(DocumentKind.AdministrativeDirective),
    route: {
      name: "search",
      query: { category: DocumentKind.AdministrativeDirective },
    },
  },
  {
    label: title.value ?? ADMINISTRATIVE_DIRECTIVE_TITLE_PLACEHOLDER,
  },
]);

const metadataItems = computed(() =>
  getAdministrativeDirectiveMetadataItems(data.value),
);

const detailProperties = computed(() =>
  getAdministrativeDirectiveDetailItems(data.value),
);

if (metadataError?.value) {
  showError(metadataError.value);
}
if (contentError?.value) {
  showError(contentError.value);
}

const detailsTitleId = useId();
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
      <h2 :id="detailsTitleId" class="ris-heading3-bold my-24">Details</h2>
      <IncompleteDataMessage class="my-24" />
      <Properties :aria-labelledby="detailsTitleId">
        <template v-for="property in detailProperties" :key="property.label">
          <PropertiesItem :label="property.label" :value="property.value" />
        </template>
      </Properties>
    </template>
  </DocumentDetailPage>
</template>
