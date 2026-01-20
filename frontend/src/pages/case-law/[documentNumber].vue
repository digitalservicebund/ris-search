<script setup lang="ts">
import type { ComputedRef } from "vue";
import type { TableOfContentsEntry } from "~/components/documents/caseLaw/TableOfContents.vue";
import type { MetadataItem } from "~/components/Metadata.vue";
import type { DocumentView } from "~/layouts/document.vue";
import { type CaseLaw, DocumentKind } from "~/types";
import IcBaselineSubject from "~icons/ic/baseline-subject";
import IcOutlineFileDownload from "~icons/ic/outline-file-download";
import IcOutlineInfo from "~icons/ic/outline-info";

definePageMeta({ layout: false });

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

// Page head ----------------------------------------------

function buildOgTitle(caseLaw: CaseLaw) {
  const court = caseLaw.courtName?.trim() || "";
  const dtype = caseLaw.documentType || "Gerichtsentscheidung";
  const date = caseLaw.decisionDate
    ? dateFormattedDDMMYYYY(caseLaw.decisionDate)
    : "";
  const file = caseLaw.fileNumbers?.[0] || "";

  const parts = [
    court && `${court}:`,
    dtype,
    date && `vom ${date}`,
    file && `– ${file}`,
  ]
    .filter(Boolean)
    .join(" ");

  return truncateAtWord(parts, 55) || undefined;
}

const ogTitle = computed(() => {
  return caseLaw.value ? buildOgTitle(caseLaw.value) : undefined;
});

const description = computed<string>(() => {
  if (caseLaw.value?.guidingPrinciple) {
    const sentences = caseLaw.value.guidingPrinciple
      .split(/(?<=[.!?])\s+/)
      .filter(Boolean);

    return truncateAtWord(sentences.slice(0, 2).join(" "), 150);
  }

  if (html.value) {
    const doc = parseDocument(html.value);
    const firstParagraph = doc.querySelector("section p");
    const firstParagraphText = firstParagraph?.textContent?.trim();
    if (firstParagraphText) {
      return truncateAtWord(firstParagraphText, 150);
    }
  }

  return "Gerichtsentscheidung";
});

const url = useRequestURL();

const link = computed(() => [{ rel: "canonical", href: url.href }]);

const meta = computed(() =>
  [
    { name: "description", content: description.value },
    { property: "og:type", content: "article" },
    { property: "og:title", content: ogTitle.value },
    { property: "og:description", content: description.value },
    { property: "og:url", content: url.href },
    { name: "twitter:title", content: ogTitle.value },
    { name: "twitter:description", content: description.value },
  ].filter(
    (tag) => typeof tag.content === "string" && tag.content.trim() !== "",
  ),
);

useHead({ title: ogTitle, link, meta });

// Page contents ------------------------------------------

const views: DocumentView[] = [
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

const breadcrumbs = computed(() => [
  {
    label: formatDocumentKind(DocumentKind.CaseLaw),
    route: `/search?category=${DocumentKind.CaseLaw}`,
  },
  { label: title.value ?? "Titelzeile nicht vorhanden" },
]);

const tocEntries: ComputedRef<TableOfContentsEntry[] | null> = computed(() => {
  return html.value ? getAllSectionsFromHtml(html.value, "section") : null;
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
      <DocumentsActionMenuCaseLawActionMenu :case-law />
    </template>

    <template #details>
      <h2 class="ris-heading3-bold my-24">Details</h2>
      <DocumentsIncompleteDataMessage class="my-24" />
      <DetailsList>
        <DetailsListEntry
          label="Spruchkörper:"
          :value="detailsMetadata.judicialBody"
        />
        <DetailsListEntry label="ECLI:" :value="detailsMetadata.ecli" />
        <DetailsListEntry label="Normen:" value="" />
        <DetailsListEntry
          label="Entscheidungsname:"
          :value="detailsMetadata.decisionNames"
        />
        <DetailsListEntry label="Vorinstanz:" value="" />

        <DetailsListEntry v-if="detailsMetadata.zipUrl" label="Download:">
          <NuxtLink
            data-attr="xml-zip-view"
            class="ris-link1-regular"
            external
            :to="detailsMetadata.zipUrl"
          >
            <IcOutlineFileDownload class="mr-2 inline" />
            {{ detailsMetadata.documentNumber }} als ZIP herunterladen
          </NuxtLink>
        </DetailsListEntry>
      </DetailsList>
    </template>

    <template #text>
      <SidebarLayout>
        <template #content>
          <DocumentsIncompleteDataMessage class="mb-16" />
          <div class="case-law" v-html="html"></div>
        </template>

        <template #sidebar>
          <client-only>
            <DocumentsCaseLawTableOfContents
              v-if="tocEntries?.length"
              :table-of-content-entries="tocEntries"
            />
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
  @apply max-w-prose print:max-w-none;
}

:deep(.case-law table[border="1"] :is(th, td)) {
  @apply border border-solid border-black px-4;
}

:deep(.case-law table) {
  @apply inline-block max-w-full overflow-x-auto;
}

:deep(.case-law h2) {
  @apply ris-heading3-bold my-24 inline-block;
}

:deep(.case-law .border-number) {
  @apply flex items-start;
}

:deep(.case-law .border-number .number) {
  @apply mr-8 text-gray-900;
  min-width: calc(var(--border-number-min-width) - 0.5rem);
}

:deep(.case-law .border-number .content) {
  @apply min-w-0 flex-1;
}

:deep(.case-law .border-number-link) {
  @apply ris-link1-regular pl-[0.25ch];
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
  @apply mb-16 wrap-break-word;
  unicode-bidi: isolate;
}
</style>
