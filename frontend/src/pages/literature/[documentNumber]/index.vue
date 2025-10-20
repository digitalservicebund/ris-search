<script setup lang="ts">
import Tab from "primevue/tab";
import TabList from "primevue/tablist";
import TabPanel from "primevue/tabpanel";
import TabPanels from "primevue/tabpanels";
import Tabs from "primevue/tabs";
import { useFetch } from "#app";
import ContentWrapper from "~/components/CustomLayouts/ContentWrapper.vue";
import SidebarLayout from "~/components/CustomLayouts/SidebarLayout.vue";
import IncompleteDataMessage from "~/components/IncompleteDataMessage.vue";
import LiteratureMetadata from "~/components/LiteratureMetadata.vue";
import RisBreadcrumb from "~/components/Ris/RisBreadcrumb.vue";
import RisDocumentTitle from "~/components/Ris/RisDocumentTitle.vue";
import {
  tabListStyles,
  tabPanelStyles,
  tabStyles,
} from "~/components/Tabs.styles";
import { useBackendURL } from "~/composables/useBackendURL";
import LiteratureDetails from "~/pages/literature/[documentNumber]/LiteratureDetails.vue";
import LiteratureMetadata from "~/pages/literature/[documentNumber]/LiteratureMetadata.vue";
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

const title = computed(() => {
  return (
    literature.value?.headline ??
    literature.value?.alternativeHeadline ??
    undefined
  );
});

const breadcrumbItems = computed(() => [
  {
    label: formatDocumentKind(DocumentKind.Literature),
    route: `/search?category=${DocumentKind.Literature}`,
  },
  {
    label: title.value ?? emptyTitlePlaceholder,
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
      </div>
      <RisDocumentTitle :title="title" :placeholder="emptyTitlePlaceholder" />
      <LiteratureMetadata
        :document-types="literature.documentTypes"
        :references="literature.dependentReferences"
        :authors="literature.authors"
        :years-of-publication="literature.yearsOfPublication"
      />
    </div>
    <Tabs v-if="!!literature" value="0">
      <TabList :pt="tabListStyles">
        <Tab
          class="flex items-center gap-8"
          :pt="tabStyles"
          value="0"
          aria-label="Text des Literaturnachweises"
          ><IcBaselineSubject />Text</Tab
        >
        <Tab
          data-attr="caselaw-metadata-tab"
          class="flex items-center gap-8"
          :pt="tabStyles"
          value="1"
          aria-label="Details zum Literaturnachweis"
          ><IcOutlineInfo />Details</Tab
        >
      </TabList>
      <TabPanels>
        <TabPanel value="0" :pt="tabPanelStyles">
          <SidebarLayout class="container">
            <template #content>
              <IncompleteDataMessage class="mb-16" />
              <div class="literature" v-html="html"></div>
            </template>
          </SidebarLayout>
        </TabPanel>
        <TabPanel value="1" :pt="tabPanelStyles" class="pt-24 pb-80">
          <section aria-labelledby="detailsTabPanelTitle" class="container">
            <LiteratureDetails
              :languages="['FooBar']"
              :authors="['FooBar']"
              :collaborators="['FooBar']"
              :conference-notes="['FooBar']"
              :norm-references="['FooBar']"
            />
          </section>
        </TabPanel>
      </TabPanels> </Tabs
  ></ContentWrapper>
</template>
