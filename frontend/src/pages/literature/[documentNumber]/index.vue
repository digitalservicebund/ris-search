<script setup lang="ts">
import { computed } from "vue";
import LiteratureActionsMenu from "~/components/ActionMenu/LiteratureActionsMenu.vue";
import LiteratureDetails from "~/components/Literature/LiteratureDetails.vue";
import LiteratureMetadata from "~/components/Literature/LiteratureMetadata.vue";
import RisDocument from "~/components/Ris/RisDocument.vue";
import { DocumentKind, type Literature } from "~/types";
import { formatDocumentKind } from "~/utils/displayValues";
import { getTitle, LITERATURE_TITLE_PLACEHOLDER } from "~/utils/literature";

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

const tabsProps = computed(() => {
  return {
    tabsLabel: "Ansichten des Literaturnachweises",
    textTabAriaLabel: "Text des Literaturnachweises",
    detailsTabAriaLabel: "Details zum Literaturnachweis",
    documentHtmlClass: "literature",
    html: html.value,
  };
});

if (metadataError?.value) {
  showError(metadataError.value);
}
if (contentError?.value) {
  showError(contentError.value);
}
</script>

<template>
  <RisDocument
    :title="title"
    :title-placeholder="LITERATURE_TITLE_PLACEHOLDER"
    :is-empty-document="isEmptyDocument"
    :breadcrumb-items="breadcrumbItems"
    :tabs-props="tabsProps"
  >
    <template #actionsMenu>
      <client-only
        ><LiteratureActionsMenu :literature="literature"
      /></client-only>
    </template>
    <template #metadata>
      <LiteratureMetadata
        v-if="literature"
        :document-types="literature.documentTypes"
        :references="literature.dependentReferences"
        :authors="literature.authors"
        :years-of-publication="literature.yearsOfPublication"
      />
    </template>
    <template #details>
      <LiteratureDetails :details="details" />
    </template>
  </RisDocument>
</template>
