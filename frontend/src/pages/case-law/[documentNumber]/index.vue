<script setup lang="ts">
import Tab from "primevue/tab";
import TabList from "primevue/tablist";
import TabPanel from "primevue/tabpanel";
import TabPanels from "primevue/tabpanels";
import Tabs from "primevue/tabs";
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
import {
  tabListStyles,
  tabPanelStyles,
  tabStyles,
} from "~/components/Tabs.styles";
import { useBackendURL } from "~/composables/useBackendURL";
import type { CaseLaw } from "~/types";
import { getEncodingURL } from "~/utils/caseLawUtils";
import { dateFormattedDDMMYYYY } from "~/utils/dateFormatting";
import { getAllSectionsFromHtml, parseDocument } from "~/utils/htmlParser";
import { removeOuterParentheses } from "~/utils/textFormatting";
import IcBaselineSubject from "~icons/ic/baseline-subject";
import IcOutlineInfo from "~icons/ic/outline-info";
import MaterialSymbolsDownload from "~icons/material-symbols/download";

const route = useRoute();
const documentNumber = route.params.documentNumber as string;
const emptyTitlePlaceholder = "Titelzeile nicht vorhanden";
const backendURL = useBackendURL();
const {
  status,
  data: caseLaw,
  error: metadataError,
} = await useFetch<CaseLaw>(`${backendURL}/v1/case-law/${documentNumber}`);
const { data: html, error: contentError } = await useFetch<string>(
  `${backendURL}/v1/case-law/${documentNumber}.html`,
  {
    headers: { Accept: "text/html" },
  },
);

const truncate = (s: string, n: number) =>
  s.length <= n ? s : s.slice(0, n).replace(/\s+\S*$/, "") + "…";

const toCourtAbbrev = (full?: string) => {
  if (!full) return "";
  const map: Record<string, string> = {
    Bundesverfassungsgericht: "BVerfG",
    Bundesgerichtshof: "BGH",
    Bundessozialgericht: "BSG",
    Bundesverwaltungsgericht: "BVerwG",
    Bundesfinanzhof: "BFH",
    Bundesarbeitsgericht: "BAG",
  };
  return map[full] ?? full;
};

const buildOgTitle = (cl?: CaseLaw) => {
  if (!cl) return undefined;

  const court = toCourtAbbrev(cl.courtName);
  const dtype = cl.documentType || "Gerichtsentscheidung";
  const date = cl.decisionDate ? dateFormattedDDMMYYYY(cl.decisionDate) : "";
  const file = cl.fileNumbers?.[0] || "";

  const parts = [
    court && `${court}:`,
    dtype,
    date && `vom ${date}`,
    file && `– ${file}`,
  ]
    .filter(Boolean)
    .join(" ");

    return truncate(parts, 55) || undefined;
};

const ogTitle = buildOgTitle(caseLaw.value ?? undefined);

const extractDescription = (
  cl?: CaseLaw | null,
  htmlContent?: string | null,
) => {
  if (!cl) return undefined;

  const leitsatz = cl.guidingPrinciple || cl.headnote || cl.otherHeadnote;

  if (leitsatz) {
    const sentences = leitsatz.split(/(?<=[.!?])\s+/).filter(Boolean);
    const firstTwo = sentences.slice(0, 2).join(" ");
    return truncate(firstTwo, 150);
  }

  if (!htmlContent) return undefined;

  const doc = parseDocument(htmlContent);
  const firstP = doc.querySelector("p");
  const text = firstP?.textContent?.trim();

  if (!text) return undefined;

  const sentences = text.split(/(?<=[.!?])\s+/).filter(Boolean);
  const firstTwo = sentences.slice(0, 2).join(" ");
  return truncate(firstTwo, 150);
};

const ogDescription = extractDescription(
  caseLaw.value ?? undefined,
  html.value ?? undefined,
);

const reqUrl = useRequestURL();
const canonicalUrl = `${reqUrl.origin}${reqUrl.pathname}`;

useHead({
  title: ogTitle,
  link: [{ rel: "canonical", href: canonicalUrl }],
  meta: [
    { name: "description", content: ogDescription || "Gerichtsentscheidung" },
    { property: "og:title", content: ogTitle },
    { property: "og:description", content: ogDescription },
    { property: "og:url", content: canonicalUrl },
    { property: "og:image", content: "/og_image.png" },
    { name: "twitter:title", content: ogTitle },
    { name: "twitter:description", content: ogDescription },
    { name: "twitter:image", content: "/og_image.png" },
  ],
});

definePageMeta({ layout: "base" }); // use "base" layout to allow for full-width tab backgrounds

const tocEntries: ComputedRef<TableOfContentsEntry[] | null> = computed(() => {
  return html.value ? getAllSectionsFromHtml(html.value, "section") : null;
});

const zipUrl = computed(() =>
  getEncodingURL(caseLaw.value, backendURL, "application/zip"),
);

console.log(`Zip url: ${zipUrl.value}` + caseLaw.value?.encoding.values);

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
    <div v-if="!!caseLaw" class="container text-left">
      <div class="flex items-center gap-8 print:hidden">
        <RisBreadcrumb
          class="grow"
          type="caselaw"
          :title="
            removeOuterParentheses(caseLaw.headline) || emptyTitlePlaceholder
          "
        />
        <CaseLawActionsMenu :case-law="caseLaw" />
      </div>
      <h1
        v-if="caseLaw.headline"
        class="ris-heading2-bold max-w-title mt-24 mb-48 overflow-x-auto text-balance"
      >
        {{ removeOuterParentheses(caseLaw.headline) }}
      </h1>
      <h1
        v-else
        class="ris-heading2-bold max-w-title mt-24 mb-48 text-balance text-gray-900"
      >
        {{ emptyTitlePlaceholder }}
      </h1>
      <!-- Metadata -->
      <div class="mb-48 flex flex-row flex-wrap gap-24">
        <MetadataField
          id="court_name"
          label="Gericht"
          :value="caseLaw.courtName"
        />
        <MetadataField
          id="document_type"
          label="Dokumenttyp"
          :value="caseLaw.documentType"
        />
        <MetadataField
          id="decision_date"
          label="Entscheidungsdatum"
          :value="dateFormattedDDMMYYYY(caseLaw.decisionDate)"
        />
        <MetadataField
          id="file_numbers"
          label="Aktenzeichen"
          :value="caseLaw.fileNumbers?.join(', ')"
        />
      </div>
    </div>
    <Tabs v-if="!!caseLaw" value="0">
      <TabList :pt="tabListStyles">
        <Tab
          class="flex items-center gap-8"
          :pt="tabStyles"
          value="0"
          aria-label="Text der Gerichtsentscheidung"
          ><IcBaselineSubject />Text</Tab
        >
        <Tab
          data-attr="caselaw-metadata-tab"
          class="flex items-center gap-8"
          :pt="tabStyles"
          value="1"
          aria-label="Details zur Gerichtsentscheidung"
          ><IcOutlineInfo />Details</Tab
        >
      </TabList>
      <TabPanels>
        <TabPanel value="0" :pt="tabPanelStyles">
          <!-- Content -->
          <SidebarLayout class="container">
            <template #content>
              <IncompleteDataMessage class="mb-16" />
              <div class="case-law" v-html="html"></div>
            </template>
            <template #sidebar>
              <client-only>
                <TableOfContents :table-of-content-entries="tocEntries || []" />
              </client-only>
            </template>
          </SidebarLayout>
        </TabPanel>
        <TabPanel value="1" :pt="tabPanelStyles" class="pt-24 pb-80">
          <section aria-labelledby="detailsTabPanelTitle" class="container">
            <h2 id="detailsTabPanelTitle" class="ris-heading3-bold my-24">
              Details
            </h2>
            <IncompleteDataMessage class="my-24" />

            <Properties>
              <PropertiesItem
                label="Spruchkörper:"
                :value="caseLaw.judicialBody"
              />
              <PropertiesItem label="ECLI:" :value="caseLaw.ecli" />
              <PropertiesItem label="Normen:" value="" />
              <PropertiesItem
                label="Entscheidungsname:"
                :value="caseLaw.decisionName?.join(', ')"
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
                  {{ caseLaw.documentNumber }} als ZIP herunterladen
                </NuxtLink>
              </PropertiesItem>
            </Properties>
          </section>
        </TabPanel>
      </TabPanels>
    </Tabs></ContentWrapper
  >
</template>
