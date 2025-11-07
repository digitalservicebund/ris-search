<script setup lang="ts">
import { computed } from "vue";
import { useFetch } from "#app";
import LiteratureActionsMenu from "~/components/ActionMenu/LiteratureActionsMenu.vue";
import ContentWrapper from "~/components/CustomLayouts/ContentWrapper.vue";
import SidebarLayout from "~/components/CustomLayouts/SidebarLayout.vue";
import IncompleteDataMessage from "~/components/IncompleteDataMessage.vue";
import LiteratureDetails from "~/components/Literature/LiteratureDetails.vue";
import LiteratureMetadata from "~/components/Literature/LiteratureMetadata.vue";
import RisBreadcrumb from "~/components/Ris/RisBreadcrumb.vue";
import RisDocumentTitle from "~/components/Ris/RisDocumentTitle.vue";
import RisTabs from "~/components/Ris/RisTabs.vue";
import { tabPanelClass } from "~/components/Tabs.styles";
import { useBackendURL } from "~/composables/useBackendURL";
import { DocumentKind, type Literature } from "~/types";
import { formatDocumentKind } from "~/utils/displayValues";
import IcBaselineSubject from "~icons/ic/baseline-subject";
import IcOutlineInfo from "~icons/ic/outline-info";

const route = useRoute();
const documentNumber = route.params.documentNumber as string;
const documentMetadataUrl = `${useBackendURL()}/v1/literature/${documentNumber}`;

const {
  status,
  data: literature,
  error: metadataError,
} = await useFetch<Literature>(documentMetadataUrl);

const { data: html, error: contentError } = await useFetch<string>(
  `${documentMetadataUrl}.html`,
  {
    headers: { Accept: "text/html" },
  },
);

definePageMeta({ layout: "base" }); // use "base" layout to allow for full-width tab backgrounds

const emptyTitlePlaceholder = "Titelzeile nicht vorhanden";

const titles = computed(() => {
  const titles = [
    literature.value?.headline,
    literature.value?.alternativeHeadline,
    literature.value?.headlineAdditions,
  ];

  const availableTitles = titles.filter((title) => title);
  return {
    mainTitle: availableTitles[0] ?? emptyTitlePlaceholder,
    hasMultipleTitles: availableTitles.length > 1,
  };
});

const isEmptyDocument = computed(() => {
  return (
    !literature.value?.outline &&
    !literature.value?.shortReport &&
    !titles.value.hasMultipleTitles
  );
});

const breadcrumbItems = computed(() => [
  {
    label: formatDocumentKind(DocumentKind.Literature),
    route: `/search?category=${DocumentKind.Literature}`,
  },
  {
    label: titles.value.mainTitle ?? emptyTitlePlaceholder,
  },
]);

if (metadataError?.value) {
  showError(metadataError.value);
}
if (contentError?.value) {
  showError(contentError.value);
}

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
        :title="titles.mainTitle"
        :placeholder="emptyTitlePlaceholder"
      />
      <LiteratureMetadata
        :document-types="literature.documentTypes"
        :references="literature.dependentReferences"
        :authors="literature.authors"
        :years-of-publication="literature.yearsOfPublication"
      />
    </div>
    <div v-if="html && !isEmptyDocument">
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
            <div class="container pt-24 pb-80">
              <LiteratureDetails
                :norm-references="literature?.normReferences ?? []"
                :collaborators="literature?.collaborators ?? []"
                :originators="literature?.originators ?? []"
                :languages="literature?.languages ?? []"
                :conference-notes="literature?.conferenceNotes ?? []"
              />
            </div>
          </section>
        </template>
      </RisTabs>
    </div>
    <div v-else class="min-h-96 border-t border-t-gray-400 bg-white print:py-0">
      <div class="container pt-24 pb-80">
        <LiteratureDetails
          :norm-references="literature?.normReferences ?? []"
          :collaborators="literature?.collaborators ?? []"
          :originators="literature?.originators ?? []"
          :languages="literature?.languages ?? []"
          :conference-notes="literature?.conferenceNotes ?? []"
        />
      </div>
    </div>
  </ContentWrapper>
</template>
