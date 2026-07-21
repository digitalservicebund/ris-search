<script setup lang="ts">
import IcBaselineSubject from "~icons/ic/baseline-subject";
import IcOutlineInfo from "~icons/ic/outline-info";
import type { TabView } from "~/components/documents/TabsLayout.vue";
import type { TreeItem } from "~/components/TreeView.vue";
import { useSearchBackLink } from "~/composables/useSearchBackLink";
import { DocumentKind, type Literature } from "~/types/api";

definePageMeta({
  layout: false,
  skipLinks: [
    { label: "Zum Inhalt", to: "#main" },
    { label: "Zum Text", to: "#content" },
    { label: "Zum Fußbereich", to: "#footer" },
  ],
});

const route = useRoute();

const documentNumber = route.params.documentNumber?.toString();
if (!documentNumber) throw createError({ status: 404 });

const documentMetadataUrl = `/v1/literature/${documentNumber}`;

const { data: literature, error: metadataError } =
  await useRisBackend<Literature>(documentMetadataUrl);
if (metadataError?.value) throw createError(metadataError.value);

useLiteratureSeo({
  documentTypes: literature.value?.documentTypes ?? [],
  yearsOfPublication: literature.value?.yearsOfPublication ?? [],
  headline: literature.value?.headline,
  alternativeHeadline: literature.value?.alternativeHeadline,
});

const { data: html, error: contentError } = await useRisBackend<string>(
  `${documentMetadataUrl}.html`,
  { headers: { Accept: "text/html" } },
);
if (contentError?.value) throw createError(contentError.value);

// Page contents ------------------------------------------

const views: TabView[] = [
  { path: "text", label: "Text", icon: IcBaselineSubject },
  {
    path: "details",
    label: "Details",
    icon: IcOutlineInfo,
    analyticsId: "literature-metadata-tab",
  },
];

const textSectionId = useId();
const detailsSectionId = useId();

const title = computed(() => getTitle(literature.value));
const document = html.value ? parseDocument(html.value) : undefined;
const isEmptyDocument = computed(() => isDocumentEmpty(document));

const tocEntries = computed<TreeItem[] | null>(() => {
  return document
    ? getAllSectionsFromDocument(document, "section").map((entry) => ({
        key: entry.id,
        subtitle: entry.title, // Subtitle for more subtle appearance
        to: { hash: `#${entry.id}`, query: { from: route.query.from } },
      }))
    : null;
});

const searchBackLink = useSearchBackLink(DocumentKind.Literature);

const breadcrumbs = computed(() => [
  {
    label: searchBackLink.value.label,
    route: searchBackLink.value.route,
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
      <DocumentsActionMenuLiteratureActionMenu
        :literature="literature"
        class="mb-auto"
      />
    </template>

    <template #details>
      <section
        :role="isEmptyDocument ? undefined : 'tabpanel'"
        :aria-labelledby="detailsSectionId"
        class="pt-32 pb-32 md:pb-56"
      >
        <h2 :id="detailsSectionId" class="typo-headline3-bold">Details</h2>
        <DocumentsIncompleteDataMessage class="my-24" />
        <DocumentsDetailsList>
          <template v-for="item in detailItems" :key="item.label">
            <DocumentsDetailsListEntry
              :label="item.label"
              :value="item.value"
              :value-list="item.valueList"
            />
          </template>
        </DocumentsDetailsList>
      </section>
    </template>

    <template #text>
      <SidebarLayout>
        <section role="tabpanel" :aria-labelledby="textSectionId">
          <h2 :id="textSectionId" class="sr-only">Text</h2>
          <DocumentsIncompleteDataMessage class="my-24" />
          <div
            v-if="document"
            class="literature"
            v-html="document.body.innerHTML"
          ></div>
        </section>

        <template #sidebar>
          <client-only>
            <DocumentsTableOfContents
              v-if="tocEntries?.length"
              :table-of-contents="tocEntries"
            />
          </client-only>
        </template>
      </SidebarLayout>
    </template>
  </NuxtLayout>
</template>

<style scoped>
@reference "~/assets/main.css";

:deep(.literature h1) {
  @apply hidden;
}

:deep(.literature h2) {
  @apply typo-headline2-bold my-24 inline-block;
}

:deep(.literature h3) {
  @apply typo-body-bold my-8;
}

:deep(.literature p) {
  @apply mb-16 wrap-break-word hyphens-auto;
}

:deep(.literature ul) {
  @apply mb-16;
}
</style>
