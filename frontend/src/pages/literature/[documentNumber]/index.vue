<script setup lang="ts">
import { computed } from "vue";
import LiteratureActionsMenu from "~/components/ActionMenu/LiteratureActionsMenu.vue";
import ContentWrapper from "~/components/CustomLayouts/ContentWrapper.vue";
import SidebarLayout from "~/components/CustomLayouts/SidebarLayout.vue";
import IncompleteDataMessage from "~/components/IncompleteDataMessage.vue";
import LiteratureDetails from "~/components/Literature/LiteratureDetails.vue";
import LiteratureMetadata from "~/components/Literature/LiteratureMetadata.vue";
import RisBreadcrumb from "~/components/Ris/RisBreadcrumb.vue";
import RisDocumentTitle from "~/components/Ris/RisDocumentTitle.vue";
import RisTabs from "~/components/Ris/RisTabs.vue";
import { DocumentKind, type Literature } from "~/types";
import { formatDocumentKind } from "~/utils/displayValues";
import { getTitle, LITERATURE_TITLE_PLACEHOLDER } from "~/utils/literature";
import { tabPanelClass } from "~/utils/tabsStyles";
import IcBaselineSubject from "~icons/ic/baseline-subject";
import IcOutlineInfo from "~icons/ic/outline-info";

definePageMeta({ layout: "base" }); // use "base" layout to allow for full-width tab backgrounds

const route = useRoute();
const documentNumber = route.params.documentNumber as string;
const documentMetadataUrl = `/v1/literature/${documentNumber}`;

const {
  status,
  data: literature,
  error: metadataError,
} = await useRisBackend<Literature>(documentMetadataUrl);

const { data: html, error: contentError } = await useRisBackend<string>(
  `${documentMetadataUrl}.html`,
  {
    headers: { Accept: "text/html" },
  },
);

const title = computed(() => getTitle(literature.value));
const isEmptyDocument = computed(() => isDocumentEmpty(literature.value));
const references = computed(() => {
  return literature.value?.dependentReferences?.length
    ? literature.value?.dependentReferences
    : (literature.value?.independentReferences ?? []);
});
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

const tabs = computed(() => [
  {
    id: "text",
    href: "#text",
    label: "Text",
    ariaLabel: "Text des Literaturnachweises",
    icon: IcBaselineSubject,
  },
  {
    id: "details",
    href: "#details",
    label: "Details",
    ariaLabel: "Details zum Literaturnachweis",
    icon: IcOutlineInfo,
  },
]);

if (metadataError?.value) {
  showError(metadataError.value);
}
if (contentError?.value) {
  showError(contentError.value);
}
</script>

<template>
  <ContentWrapper border>
    <div v-if="status == 'pending'" class="container">Lade ...</div>
    <div v-if="!!literature" class="container text-left">
      <div class="flex items-center gap-8 print:hidden">
        <RisBreadcrumb :items="breadcrumbItems" class="grow" />
        <client-only
          ><LiteratureActionsMenu :literature="literature"
        /></client-only>
      </div>
      <RisDocumentTitle
        :title="title"
        :placeholder="LITERATURE_TITLE_PLACEHOLDER"
      />
      <LiteratureMetadata
        :document-types="literature.documentTypes"
        :references="references"
        :authors="literature.authors"
        :years-of-publication="literature.yearsOfPublication"
      />
    </div>
    <div
      v-if="isEmptyDocument"
      class="min-h-96 border-t border-t-gray-400 bg-white print:py-0"
    >
      <div class="container pt-24 pb-80">
        <LiteratureDetails :details="details" />
      </div>
    </div>
    <div v-else>
      <RisTabs :tabs="tabs" label="Ansichten des Literaturnachweises">
        <template #default="{ activeTab, isClient }">
          <section
            id="text"
            :class="tabPanelClass"
            :hidden="isClient && activeTab !== 'text'"
            aria-labelledby="textSectionHeading"
          >
            <SidebarLayout class="container">
              <template #content>
                <h2 id="textSectionHeading" class="sr-only">Text</h2>
                <IncompleteDataMessage class="mb-16" />
                <div class="literature" v-html="html"></div>
              </template>
            </SidebarLayout>
          </section>
          <section
            id="details"
            :class="tabPanelClass"
            :hidden="isClient && activeTab !== 'details'"
            aria-labelledby="detailsTabPanelTitle"
          >
            <div class="container pb-56">
              <LiteratureDetails :details="details" />
            </div>
          </section>
        </template>
      </RisTabs>
    </div>
  </ContentWrapper>
</template>
