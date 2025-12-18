<script setup lang="ts">
import type { DocumentView } from "~/layouts/document.vue";
import { DocumentKind, type Literature } from "~/types";
import IcBaselineSubject from "~icons/ic/baseline-subject";
import IcOutlineInfo from "~icons/ic/outline-info";

definePageMeta({ layout: false });

const route = useRoute();

const documentNumber = route.params.documentNumber?.toString();
if (!documentNumber) showError({ status: 404 });

const documentMetadataUrl = `/v1/literature/${documentNumber}`;

const { data: literature, error: metadataError } =
  await useRisBackend<Literature>(documentMetadataUrl);
if (metadataError?.value) showError(metadataError.value);

const { data: html, error: contentError } = await useRisBackend<string>(
  `${documentMetadataUrl}.html`,
  { headers: { Accept: "text/html" } },
);
if (contentError?.value) showError(contentError.value);

// Page contents ------------------------------------------

const views: DocumentView[] = [
  { path: "text", label: "Text", icon: IcBaselineSubject },
  { path: "details", label: "Details", icon: IcOutlineInfo },
];

const textSectionId = useId();
const detailsSectionId = useId();

const title = computed(() => getTitle(literature.value));
const isEmptyDocument = computed(() => isLiteratureEmpty(literature.value));

const breadcrumbs = computed(() => [
  {
    label: formatDocumentKind(DocumentKind.Literature),
    route: `/search?category=${DocumentKind.Literature}`,
  },
  { label: title.value ?? "Titelzeile nicht vorhanden" },
]);

const metadata = computed(() => getLiteratureMetadataItems(literature.value));

const detailItems = computed(() => getLiteratureDetailItems(literature.value));
</script>

<template>
  <NuxtLayout
    name="document"
    :breadcrumbs
    :is-empty-document="isEmptyDocument"
    :metadata
    :title
    :views
  >
    <template #actionMenu>
      <DocumentsActionMenuLiteratureActionMenu :literature="literature" />
    </template>

    <template #details>
      <section :aria-labelledby="detailsSectionId">
        <h2 :id="detailsSectionId" class="ris-heading3-bold my-24">Details</h2>
        <DocumentsIncompleteDataMessage class="my-24" />
        <DetailsList>
          <template v-for="item in detailItems" :key="item.label">
            <DetailsListEntry
              :label="item.label"
              :value="item.value"
              :value-list="item.valueList"
            />
          </template>
        </DetailsList>
      </section>
    </template>

    <template #text>
      <SidebarLayout>
        <template #content>
          <section :aria-labelledby="textSectionId">
            <h2 :id="textSectionId" class="sr-only">Text</h2>
            <DocumentsIncompleteDataMessage class="mb-16" />
            <div class="literature" v-html="html"></div>
          </section>
        </template>
      </SidebarLayout>
    </template>
  </NuxtLayout>
</template>
