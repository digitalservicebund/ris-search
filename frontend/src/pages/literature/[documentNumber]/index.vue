<script setup lang="ts">
import { computed } from "vue";
import LiteratureActionsMenu from "~/components/ActionMenu/LiteratureActionsMenu.vue";
import DocumentDetailPage from "~/components/DocumentDetailPage.vue";
import LiteratureDetails from "~/components/Literature/LiteratureDetails.vue";
import { DocumentKind, type Literature } from "~/types";
import { formatDocumentKind } from "~/utils/displayValues";
import {
  getLiteratureMetadataItems,
  getTitle,
  LITERATURE_TITLE_PLACEHOLDER,
} from "~/utils/literature";

definePageMeta({ layout: "base" }); // use "base" layout to allow for full-width tab backgrounds

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
const isEmptyDocument = computed(() => isDocumentEmpty(literature.value));
const details = computed(() => {
  return {
    normReferences: literature.value?.normReferences ?? [],
    collaborators: literature.value?.collaborators ?? [],
    originators: literature.value?.originators ?? [],
    languages: literature.value?.languages ?? [],
    conferenceNotes: literature.value?.conferenceNotes ?? [],
  };
});

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
    <template #actionsMenu>
      <client-only
        ><LiteratureActionsMenu :literature="literature"
      /></client-only>
    </template>
    <template #details>
      <LiteratureDetails :details="details" />
    </template>
  </DocumentDetailPage>
</template>
