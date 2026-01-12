<script setup lang="ts">
import type { DocumentView } from "~/layouts/document.vue";
import { type AdministrativeDirective, DocumentKind } from "~/types";
import IcBaselineSubject from "~icons/ic/baseline-subject";
import IcOutlineInfo from "~icons/ic/outline-info";

definePageMeta({ layout: false });

const route = useRoute();

const documentNumber = route.params.documentNumber as string;
if (!documentNumber) showError({ status: 404 });

const documentMetadataUrl = `/v1/administrative-directive/${documentNumber}`;

const { data, error: metadataError } =
  await useRisBackend<AdministrativeDirective>(documentMetadataUrl);
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

const title = computed(() => data.value?.headline);
const isEmptyDocument = isDocumentEmpty(html.value);

const breadcrumbs = computed(() => [
  {
    label: formatDocumentKind(DocumentKind.AdministrativeDirective),
    route: {
      name: "search",
      query: { category: DocumentKind.AdministrativeDirective },
    },
  },
  { label: title.value ?? "Titelzeile nicht vorhanden" },
]);

const metadata = computed(() =>
  getAdministrativeDirectiveMetadataItems(data.value),
);

const detailItems = computed(() =>
  getAdministrativeDirectiveDetailItems(data.value),
);
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
      <DocumentsActionMenuAdministrativeDirectiveActionMenu
        :administrative-directive="data"
      />
    </template>

    <template #details>
      <section :aria-labelledby="detailsSectionId">
        <h2 :id="detailsSectionId" class="ris-heading3-bold my-24">Details</h2>
        <DocumentsIncompleteDataMessage class="my-24" />
        <DetailsList>
          <template v-for="item in detailItems" :key="item.label">
            <DetailsListEntry :label="item.label" :value="item.value" />
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
            <div class="administrative-directive" v-html="html"></div>
          </section>
        </template>
      </SidebarLayout>
    </template>
  </NuxtLayout>
</template>

<style scoped>
@reference "~/assets/main.css";

.administrative-directive {
  @apply max-w-prose print:max-w-none;
}

:deep(.administrative-directive h1) {
  @apply hidden;
}

:deep(.administrative-directive h2) {
  @apply ris-heading3-bold my-24 inline-block;
}

:deep(.administrative-directive h3) {
  @apply ris-body1-bold my-8;
}

:deep(.administrative-directive p) {
  @apply mb-16 overflow-x-auto;
}

:deep(.administrative-directive ul) {
  @apply mb-16;
}
</style>
