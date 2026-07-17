<script setup lang="ts">
import IcBaselineSubject from "~icons/ic/baseline-subject";
import IcOutlineInfo from "~icons/ic/outline-info";
import type { TabView } from "~/components/documents/TabsLayout.vue";
import type { TreeItem } from "~/components/TreeView.vue";
import { useSearchBackLink } from "~/composables/useSearchBackLink";
import { type AdministrativeDirective, DocumentKind } from "~/types/api";

definePageMeta({
  layout: false,
  skipLinks: [
    { label: "Zum Inhalt", to: "#main" },
    { label: "Zum Text", to: "#content" },
    { label: "Zum Fußbereich", to: "#footer" },
  ],
});

const route = useRoute();

const documentNumber = route.params.documentNumber as string;
if (!documentNumber) throw createError({ status: 404 });

const documentMetadataUrl = `/v1/administrative-directive/${documentNumber}`;

const { data, error: metadataError } =
  await useRisBackend<AdministrativeDirective>(documentMetadataUrl);
if (metadataError?.value) throw createError(metadataError.value);

useAdministrativeDirectiveSeo({
  documentType: data.value?.documentType,
  entryIntoForceDate: data.value?.entryIntoForceDate,
  headline: data.value?.headline,
});

const { data: html, error: contentError } = await useRisBackend<string>(
  `${documentMetadataUrl}.html`,
  { headers: { Accept: "text/html" } },
);
if (contentError?.value) throw createError(contentError.value);

// Page contents ------------------------------------------

const views: TabView[] = [
  { path: "text", label: "Text", icon: IcBaselineSubject },
  { path: "details", label: "Details", icon: IcOutlineInfo },
];

const textSectionId = useId();
const detailsSectionId = useId();

const title = computed(() => data.value?.headline);

const document = html.value ? parseDocument(html.value) : undefined;
const isEmptyDocument = isDocumentEmpty(document);

const tocEntries = computed<TreeItem[] | null>(() => {
  return document
    ? getAllSectionsFromDocument(document, "section").map((entry) => ({
        key: entry.id,
        subtitle: entry.title, // Subtitle for more subtle appearance
        to: { hash: `#${entry.id}`, query: { from: route.query.from } },
      }))
    : null;
});

const searchBackLink = useSearchBackLink(DocumentKind.AdministrativeDirective);

const breadcrumbs = computed(() => [
  {
    label: searchBackLink.value.label,
    route: searchBackLink.value.route,
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
        class="mb-auto"
        :administrative-directive="data"
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
            class="administrative-directive"
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

:deep(.administrative-directive h1) {
  @apply hidden;
}

:deep(.administrative-directive h2) {
  @apply typo-headline2-bold my-24 inline-block;
}

:deep(.administrative-directive h3) {
  @apply typo-body-bold my-8;
}

:deep(.administrative-directive p) {
  @apply mb-16 wrap-break-word hyphens-auto;
}

:deep(.administrative-directive ul) {
  @apply mb-16;
}
</style>
