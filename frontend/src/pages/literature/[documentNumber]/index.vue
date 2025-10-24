<script setup lang="ts">
import { computed, ref, onMounted } from "vue";
import { useFetch } from "#app";
import ContentWrapper from "~/components/CustomLayouts/ContentWrapper.vue";
import SidebarLayout from "~/components/CustomLayouts/SidebarLayout.vue";
import IncompleteDataMessage from "~/components/IncompleteDataMessage.vue";
import {
  linkTabBase,
  linkTabActive,
  linkTabInactive,
  linkTabNav,
  linkTabNavContainer,
  linkTabPanel,
} from "~/components/LinkTabs.styles";
import LiteratureMetadata from "~/components/LiteratureMetadata.vue";
import RisBreadcrumb from "~/components/Ris/RisBreadcrumb.vue";
import RisDocumentTitle from "~/components/Ris/RisDocumentTitle.vue";
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

const isClient = ref(false);
onMounted(() => (isClient.value = true));

const activeSection = ref<"text" | "details">("text");

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
    <nav :class="linkTabNav" aria-label="Ansichten des Literaturnachweises">
      <div :class="linkTabNavContainer">
        <a
          href="#text"
          :aria-current="activeSection === 'text' ? 'page' : undefined"
          aria-label="Text des Literaturnachweises"
          :class="[
            linkTabBase,
            activeSection === 'text' ? linkTabActive : linkTabInactive,
          ]"
          @click.prevent="activeSection = 'text'"
        >
          <IcBaselineSubject aria-hidden="true" />
          Text
        </a>

        <a
          href="#details"
          data-attr="literature-metadata-tab"
          :aria-current="activeSection === 'details' ? 'page' : undefined"
          aria-label="Details zum Literaturnachweis"
          :class="[
            linkTabBase,
            activeSection === 'details' ? linkTabActive : linkTabInactive,
          ]"
          @click.prevent="activeSection = 'details'"
        >
          <IcOutlineInfo aria-hidden="true" />
          Details
        </a>
      </div>
    </nav>

    <section
      id="text"
      :class="linkTabPanel"
      :hidden="isClient && activeSection !== 'text'"
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
      :class="linkTabPanel"
      :hidden="isClient && activeSection !== 'details'"
      aria-labelledby="detailsTabPanelTitle"
    >
      <div class="container pt-24 pb-80">
        <h2 id="detailsTabPanelTitle" class="ris-heading3-bold my-24">
          Details
        </h2>
        <IncompleteDataMessage class="my-24" />
        <Properties>
          <PropertiesItem label="Property:" value="todo" />
        </Properties>
      </div>
    </section>
  </ContentWrapper>
</template>
