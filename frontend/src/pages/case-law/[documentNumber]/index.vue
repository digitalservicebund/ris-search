<script setup lang="ts">
import type { ComputedRef } from "vue";
import { computed } from "vue";
import CaseLawActionsMenu from "~/components/ActionMenu/CaseLawActionsMenu.vue";
import TableOfContents, {
  type TableOfContentsEntry,
} from "~/components/Caselaw/TableOfContents.vue";
import DocumentDetailPage from "~/components/DocumentDetailPage.vue";
import IncompleteDataMessage from "~/components/IncompleteDataMessage.vue";
import MetadataField from "~/components/MetadataField.vue";
import Properties from "~/components/Properties.vue";
import PropertiesItem from "~/components/PropertiesItem.vue";
import { type CaseLaw, DocumentKind } from "~/types";
import { getEncodingURL } from "~/utils/caseLaw";
import { dateFormattedDDMMYYYY } from "~/utils/dateFormatting";
import { formatDocumentKind } from "~/utils/displayValues";
import { getAllSectionsFromHtml, parseDocument } from "~/utils/htmlParser";
import {
  formatArray,
  removeOuterParentheses,
  truncateAtWord,
} from "~/utils/textFormatting";
import MaterialSymbolsDownload from "~icons/material-symbols/download";

const route = useRoute();
const documentNumber = route.params.documentNumber as string;
const emptyTitlePlaceholder = "Titelzeile nicht vorhanden";
const { data: caseLaw, error: metadataError } = await useRisBackend<CaseLaw>(
  `/v1/case-law/${documentNumber}`,
);
const { data: html, error: contentError } = await useRisBackend<string>(
  `/v1/case-law/${documentNumber}.html`,
);

const buildOgTitle = (caseLaw: CaseLaw) => {
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
};

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

useHead({
  title: ogTitle,
  link,
  meta,
});

definePageMeta({ layout: "base" }); // use "base" layout to allow for full-width tab backgrounds

const tocEntries: ComputedRef<TableOfContentsEntry[] | null> = computed(() => {
  return html.value ? getAllSectionsFromHtml(html.value, "section") : null;
});

const zipUrl = computed(() => getEncodingURL(caseLaw.value, "application/zip"));

const title = computed(() => {
  return caseLaw.value?.headline
    ? removeOuterParentheses(caseLaw.value?.headline)
    : undefined;
});

const breadcrumbItems = computed(() => [
  {
    label: formatDocumentKind(DocumentKind.CaseLaw),
    route: `/search?category=${DocumentKind.CaseLaw}`,
  },
  {
    label:
      removeOuterParentheses(caseLaw.value?.headline) || emptyTitlePlaceholder,
  },
]);

if (metadataError?.value) {
  showError(metadataError.value);
}
if (contentError?.value) {
  showError(contentError.value);
}

const formattedDecisionDate = computed(() =>
  dateFormattedDDMMYYYY(caseLaw.value?.decisionDate),
);
const formattedFileNumbers = computed(() =>
  formatArray(caseLaw.value?.fileNumbers ?? []),
);
const formattedDescisionNames = computed(() =>
  formatArray(caseLaw.value?.decisionName ?? []),
);
</script>

<template>
  <DocumentDetailPage
    :title="title"
    title-placeholder="Titelzeile nicht vorhanden"
    :breadcrumb-items="breadcrumbItems"
    document-html-class="case-law"
    :html="html"
  >
    <template #actionsMenu>
      <CaseLawActionsMenu :case-law="caseLaw" />
    </template>
    <template #metadata>
      <div class="mb-48 flex flex-row flex-wrap gap-24">
        <MetadataField label="Gericht" :value="caseLaw?.courtName" />
        <MetadataField label="Dokumenttyp" :value="caseLaw?.documentType" />
        <MetadataField
          label="Entscheidungsdatum"
          :value="formattedDecisionDate"
        />
        <MetadataField label="Aktenzeichen" :value="formattedFileNumbers" />
      </div>
    </template>
    <template #sidebar>
      <client-only>
        <TableOfContents :table-of-content-entries="tocEntries || []" />
      </client-only>
    </template>
    <template #details>
      <h2 id="detailsTabPanelTitle" class="ris-heading3-bold my-24">Details</h2>
      <IncompleteDataMessage class="my-24" />
      <Properties>
        <PropertiesItem label="Spruchkörper:" :value="caseLaw?.judicialBody" />
        <PropertiesItem label="ECLI:" :value="caseLaw?.ecli" />
        <PropertiesItem label="Normen:" value="" />
        <PropertiesItem
          label="Entscheidungsname:"
          :value="formattedDescisionNames"
        />
        <PropertiesItem label="Vorinstanz:" value="" />
        <PropertiesItem label="Download:">
          <NuxtLink
            data-attr="xml-zip-view"
            class="ris-link1-regular"
            external
            :to="zipUrl"
          >
            <MaterialSymbolsDownload class="mr-2 inline" />
            {{ caseLaw?.documentNumber }} als ZIP herunterladen
          </NuxtLink>
        </PropertiesItem>
      </Properties>
    </template>
  </DocumentDetailPage>
</template>
