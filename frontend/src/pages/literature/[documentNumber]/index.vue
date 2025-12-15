<script setup lang="ts">
import { computed } from "vue";
import LiteratureActionMenu from "~/components/documents/actionMenu/LiteratureActionMenu.vue";
import DocumentDetailPage from "~/components/documents/DocumentDetailPage.vue";
import IncompleteDataMessage from "~/components/documents/IncompleteDataMessage.vue";
import { DocumentKind, type Literature } from "~/types";
import { formatDocumentKind } from "~/utils/displayValues";
import {
  getLiteratureMetadataItems,
  getTitle,
  LITERATURE_TITLE_PLACEHOLDER,
} from "~/utils/literature";

definePageMeta({ layout: "document" });

const route = useRoute();
const documentNumber = route.params.documentNumber as string;
const documentMetadataUrl = `/v1/literature/${documentNumber}`;

const { data: literature, error: metadataError } =
  await useRisBackend<Literature>(documentMetadataUrl);

const { data: html, error: contentError } = await useRisBackend<string>(
  `${documentMetadataUrl}.html`,
  {
    headers: { Accept: "text/html" },
  },
);

const title = computed(() => getTitle(literature.value));
const isEmptyDocument = computed(() => isLiteratureEmpty(literature.value));

const breadcrumbItems = computed(() => [
  {
    label: formatDocumentKind(DocumentKind.Literature),
    route: `/search?category=${DocumentKind.Literature}`,
  },
  {
    label: title.value ?? LITERATURE_TITLE_PLACEHOLDER,
  },
]);

const metadataItems = computed(() =>
  getLiteratureMetadataItems(literature.value),
);

const detailItems = computed(() => getLiteratureDetailItems(literature.value));

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
    :title-placeholder="LITERATURE_TITLE_PLACEHOLDER"
    :is-empty-document="isEmptyDocument"
    :breadcrumb-items="breadcrumbItems"
    :metadata-items="metadataItems"
    document-html-class="literature"
    :html="html"
  >
    <template #actionMenu>
      <client-only
        ><LiteratureActionMenu :literature="literature"
      /></client-only>
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
