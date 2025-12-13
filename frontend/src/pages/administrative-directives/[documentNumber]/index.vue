<script setup lang="ts">
import { computed } from "vue";
import AdministrativeDirectiveActionsMenu from "~/components/documents/actionMenu/AdministrativeDirectiveActionsMenu.vue";
import DocumentDetailPage from "~/components/documents/DocumentDetailPage.vue";
import IncompleteDataMessage from "~/components/documents/IncompleteDataMessage.vue";
import { type AdministrativeDirective, DocumentKind } from "~/types";
import {
  ADMINISTRATIVE_DIRECTIVE_TITLE_PLACEHOLDER,
  getAdministrativeDirectiveMetadataItems,
} from "~/utils/administrativeDirective";
import { formatDocumentKind } from "~/utils/displayValues";

definePageMeta({ layout: "document" });

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

const detailItems = computed(() =>
  getAdministrativeDirectiveDetailItems(data.value),
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
    <template #actionsMenu>
      <client-only>
        <AdministrativeDirectiveActionsMenu :administrative-directive="data" />
      </client-only>
    </template>
    <template #details="{ detailsTabPanelId }">
      <h2 :id="detailsTabPanelId" class="ris-heading3-bold my-24">Details</h2>
      <IncompleteDataMessage class="my-24" />
      <DetailsList :aria-labelledby="detailsTabPanelId">
        <template v-for="item in detailItems" :key="item.label">
          <DetailsListEntry :label="item.label" :value="item.value" />
        </template>
      </DetailsList>
    </template>
  </DocumentDetailPage>
</template>
