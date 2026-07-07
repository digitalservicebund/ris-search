<script setup lang="ts">
import IcBaselineSubject from "~icons/ic/baseline-subject";
import IcOutlineFileDownload from "~icons/ic/outline-file-download";
import IcOutlineInfo from "~icons/ic/outline-info";
import type { MetadataItem } from "~/components/documents/Metadata.vue";
import type { TabView } from "~/components/documents/TabsLayout.vue";
import type { TreeItem } from "~/components/TreeView.vue";
import { useSearchBackLink } from "~/composables/useSearchBackLink";
import { type CaseLaw, DocumentKind } from "~/types/api";

definePageMeta({
  layout: false,
  skipLinks: [
    { label: "Zum Inhalt", to: "#main" },
    { label: "Zum Entscheidungstext", to: "#content" },
    { label: "Zum Fußbereich", to: "#footer" },
  ],
});

const route = useRoute();

const documentNumber = route.params.documentNumber?.toString();
if (!documentNumber) showError({ status: 404 });

const { data: caseLaw, error: metadataError } = await useRisBackend<CaseLaw>(
  `/v1/case-law/${documentNumber}`,
);
if (metadataError?.value) showError(metadataError.value);

const { data: html, error: contentError } = await useRisBackend<string>(
  `/v1/case-law/${documentNumber}.html`,
);
if (contentError?.value) showError(contentError.value);

const document = computed(() => {
  if (html.value) {
    return parseDocument(html.value);
  }
});

useCaselawSeo({ caseLaw: caseLaw.value, document: document.value });

// Page contents ------------------------------------------

const views: TabView[] = [
  { path: "text", label: "Text", icon: IcBaselineSubject },
  {
    path: "details",
    label: "Details",
    icon: IcOutlineInfo,
    analyticsId: "caselaw-metadata-tab",
  },
];

const title = computed(() => {
  return caseLaw.value?.headline
    ? removeOuterParentheses(caseLaw.value?.headline)
    : undefined;
});

const searchBackLink = useSearchBackLink(DocumentKind.CaseLaw);

const breadcrumbs = computed(() => [
  {
    label: searchBackLink.value.label,
    route: searchBackLink.value.route,
  },
  { label: title.value ?? "Titelzeile nicht vorhanden" },
]);

const tocEntries = computed<TreeItem[] | null>(() => {
  return document.value
    ? getAllSectionsFromDocument(document.value, "section").map((entry) => ({
        key: entry.id,
        subtitle: entry.title, // Subtitle for more subtle appearance
        to: { hash: `#${entry.id}`, query: { from: route.query.from } },
      }))
    : null;
});

const headerMetadata = computed<MetadataItem[]>(() => [
  { label: "Gericht", value: caseLaw.value?.courtName },
  { label: "Dokumenttyp", value: caseLaw.value?.documentType },
  {
    label: "Entscheidungsdatum",
    value: dateFormattedDDMMYYYY(caseLaw.value?.decisionDate),
  },
  {
    label: "Aktenzeichen",
    value: formatArray(caseLaw.value?.fileNumbers ?? []),
  },
]);

const detailsMetadata = computed(() => {
  const zipUrl = getEncodingURL(caseLaw.value, "application/zip");
  const decisionNames = formatArray(caseLaw.value?.decisionName ?? []);

  return {
    documentNumber: caseLaw.value?.documentNumber,
    ecli: caseLaw.value?.ecli,
    decisionNames,
    judicialBody: caseLaw.value?.judicialBody,
    zipUrl,
  };
});

const textSectionId = useId();
const detailsSectionId = useId();
</script>

<template>
  <NuxtLayout
    name="document"
    :breadcrumbs
    :metadata="headerMetadata"
    :title
    :views
  >
    <template #actionMenu>
      <DocumentsActionMenuCaseLawActionMenu :case-law class="mb-auto" />
    </template>

    <template #details>
      <section
        role="tabpanel"
        :aria-labelledby="detailsSectionId"
        class="pt-32 pb-32 md:pb-56"
      >
        <h2 :id="detailsSectionId" class="typo-headline3-bold">Details</h2>
        <DocumentsIncompleteDataMessage class="my-24" />
        <DocumentsDetailsList>
          <DocumentsDetailsListEntry
            label="Spruchkörper:"
            :value="detailsMetadata.judicialBody"
          />
          <DocumentsDetailsListEntry
            label="ECLI:"
            :value="detailsMetadata.ecli"
            valueClass="break-all"
          />
          <DocumentsDetailsListEntry label="Normen:" value="" />
          <DocumentsDetailsListEntry
            label="Entscheidungsname:"
            :value="detailsMetadata.decisionNames"
          />
          <DocumentsDetailsListEntry label="Vorinstanz:" value="" />

          <DocumentsDetailsListEntry
            v-if="detailsMetadata.zipUrl"
            label="Download:"
          >
            <NuxtLink
              data-attr="xml-zip-view"
              class="typo-link-regular"
              external
              :to="detailsMetadata.zipUrl"
            >
              <IcOutlineFileDownload class="mr-2 inline" />
              Diese Gerichtsentscheidung als ZIP herunterladen
            </NuxtLink>
          </DocumentsDetailsListEntry>
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
            class="case-law"
            v-html="document.body.innerHTML"
          ></div>
        </section>

        <template #sidebar v-if="tocEntries?.length">
          <client-only>
            <DocumentsTableOfContents :table-of-contents="tocEntries" />
          </client-only>
        </template>
      </SidebarLayout>
    </template>
  </NuxtLayout>
</template>

<style scoped>
@reference "~/assets/main.css";

.case-law {
  --border-number-min-width: 3rem;
}

:deep(.case-law table[border="1"] :is(th, td)) {
  @apply border border-solid border-black px-4;
}

:deep(.case-law table) {
  @apply inline-block max-w-full overflow-x-auto text-sm;

  td,
  th {
    @apply p-4 align-top;
  }
}

:deep(.case-law h2) {
  @apply typo-headline2-bold my-24 inline-block;
}

:deep(.case-law .border-number) {
  @apply flex items-start;
}

:deep(.case-law .border-number .number) {
  @apply mr-8 text-gray-900;
  min-width: calc(var(--border-number-min-width) - 0.5rem);
}

:deep(.case-law .border-number .content) {
  @apply min-w-0 flex-1 wrap-break-word hyphens-auto;
}

:deep(.case-law .border-number-link) {
  @apply typo-link-regular pl-[0.25ch];
}

:deep(.case-law section > p) {
  @apply ml-(--border-number-min-width);
}

:deep(.case-law #gliederung blockquote) {
  @apply ml-32;
}

:deep(.case-law #gruende blockquote) {
  @apply ml-(--border-number-min-width) border-l-2 border-gray-700 pl-16 sm:ml-[calc(var(--border-number-min-width)+2rem)];

  & + * {
    @apply mt-16;
  }

  & + blockquote {
    @apply mt-0;
  }
}

:deep(.case-law #title) {
  @apply hidden;
}

:deep(.case-law p) {
  @apply mb-16 wrap-break-word hyphens-auto;
  unicode-bidi: isolate;
}
</style>
