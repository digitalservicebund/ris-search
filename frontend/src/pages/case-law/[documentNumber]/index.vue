<script setup lang="ts">
import { computed } from "vue";
import type { ComputedRef } from "vue";
import { useFetch } from "#app";
import CaseLawActionsMenu from "~/components/ActionMenu/CaseLawActionsMenu.vue";
import TableOfContents, {
  type TableOfContentsEntry,
} from "~/components/Caselaw/TableOfContents.vue";
import ContentWrapper from "~/components/CustomLayouts/ContentWrapper.vue";
import SidebarLayout from "~/components/CustomLayouts/SidebarLayout.vue";
import IncompleteDataMessage from "~/components/IncompleteDataMessage.vue";
import MetadataField from "~/components/MetadataField.vue";
import Properties from "~/components/Properties.vue";
import PropertiesItem from "~/components/PropertiesItem.vue";
import RisBreadcrumb from "~/components/Ris/RisBreadcrumb.vue";
import RisDocumentTitle from "~/components/Ris/RisDocumentTitle.vue";
import RisTabs from "~/components/Ris/RisTabs.vue";
import { tabPanelClass } from "~/components/Tabs.styles";
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
import IcBaselineSubject from "~icons/ic/baseline-subject";
import IcOutlineInfo from "~icons/ic/outline-info";
import MaterialSymbolsDownload from "~icons/material-symbols/download";

const route = useRoute();
const documentNumber = route.params.documentNumber as string;
const emptyTitlePlaceholder = "Titelzeile nicht vorhanden";
const {
  status,
  data: caseLaw,
  error: metadataError,
} = await useFetch<CaseLaw>(`/v1/case-law/${documentNumber}`);
const { data: html, error: contentError } = await useFetch<string>(
  `/v1/case-law/${documentNumber}.html`,
  {
    headers: { Accept: "text/html" },
  },
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

const tabs = computed(() => [
  {
    id: "text",
    href: "#text",
    label: "Text",
    ariaLabel: "Text der Gerichtsentscheidung",
    icon: IcBaselineSubject,
  },
  {
    id: "details",
    href: "#details",
    label: "Details",
    ariaLabel: "Details zur Gerichtsentscheidung",
    icon: IcOutlineInfo,
  },
]);
</script>

<template>
  <ContentWrapper border>
    <div v-if="status == 'pending'" class="container">Lade ...</div>
    <div v-if="!!caseLaw" class="container text-left">
      <div class="flex items-center gap-8 print:hidden">
        <RisBreadcrumb :items="breadcrumbItems" class="grow" />
        <CaseLawActionsMenu :case-law="caseLaw" />
      </div>
      <RisDocumentTitle :title="title" :placeholder="emptyTitlePlaceholder" />
      <!-- Metadata -->
      <div class="mb-48 flex flex-row flex-wrap gap-24">
        <MetadataField label="Gericht" :value="caseLaw.courtName" />
        <MetadataField label="Dokumenttyp" :value="caseLaw.documentType" />
        <MetadataField
          label="Entscheidungsdatum"
          :value="formattedDecisionDate"
        />
        <MetadataField label="Aktenzeichen" :value="formattedFileNumbers" />
      </div>
    </div>

    <RisTabs :tabs="tabs" label="Ansichten der Gerichtsentscheidung">
      <template #default="{ activeTab, isClient }">
        <section
          id="text"
          :class="tabPanelClass"
          :hidden="isClient && activeTab !== 'text'"
        >
          <SidebarLayout class="container">
            <template #content>
              <h2 class="sr-only">Text</h2>
              <IncompleteDataMessage class="mb-16" />
              <div class="case-law" v-html="html"></div>
            </template>
            <template #sidebar>
              <client-only>
                <TableOfContents :table-of-content-entries="tocEntries || []" />
              </client-only>
            </template>
          </SidebarLayout>
        </section>

        <section
          id="details"
          :class="tabPanelClass"
          :hidden="isClient && activeTab !== 'details'"
          aria-labelledby="detailsTabPanelTitle"
        >
          <div class="container">
            <h2 id="detailsTabPanelTitle" class="ris-heading3-bold my-24">
              Details
            </h2>
            <IncompleteDataMessage class="my-24" />
            <Properties>
              <PropertiesItem
                label="Spruchkörper:"
                :value="caseLaw?.judicialBody"
              />
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
                  :href="zipUrl"
                >
                  <MaterialSymbolsDownload class="mr-2 inline" />
                  {{ caseLaw?.documentNumber }} als ZIP herunterladen
                </NuxtLink>
              </PropertiesItem>
            </Properties>
          </div>
        </section>
      </template>
    </RisTabs>
  </ContentWrapper>
</template>
