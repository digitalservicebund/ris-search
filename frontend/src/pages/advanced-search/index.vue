<script setup lang="ts">
import { PanelMenu } from "primevue";
import type { MenuItem } from "primevue/menuitem";
import { useRoute } from "#app";
import DataFieldPicker from "~/components/AdvancedSearch/DataFieldPicker.vue";
import DateFilter from "~/components/AdvancedSearch/DateFilter.vue";
import {
  isFilterType,
  type DateFilterValue,
} from "~/components/AdvancedSearch/filterType";
import ContentWrapper from "~/components/CustomLayouts/ContentWrapper.vue";
import { useAdvancedSearch } from "~/composables/useAdvancedSearch";
import { queryableDataFields } from "~/pages/advanced-search/dataFields";
import { DocumentKind } from "~/types";
import { formatDocumentKind } from "~/utils/displayValues";
import { isDocumentKind } from "~/utils/documentKind";

const route = useRoute();

useHead({ title: "Erweiterte Suche" });
definePageMeta({ alias: "/erweiterte-suche" });

const setDocumentKind: MenuItem["command"] = (e) => {
  if (!e.item.key) return;
  documentKind.value = e.item.key as DocumentKind;
};

const documentKindMenuItems: MenuItem[] = [
  {
    key: DocumentKind.Norm,
    label: formatDocumentKind(DocumentKind.Norm),
    command: setDocumentKind,
  },
  {
    key: DocumentKind.CaseLaw,
    label: formatDocumentKind(DocumentKind.CaseLaw),
    command: setDocumentKind,
  },
];

const documentKind = ref<DocumentKind>(
  typeof route.query.documentKind === "string" &&
    isDocumentKind(route.query.documentKind)
    ? route.query.documentKind
    : DocumentKind.Norm,
);

const dateFilter = ref<DateFilterValue>({
  type:
    typeof route.query.dateFilterType === "string" &&
    isFilterType(route.query.dateFilterType)
      ? route.query.dateFilterType
      : "allTime",
  from: route.query.dateFilterFrom?.toString(),
  to: route.query.dateFilterTo?.toString(),
});

const query = ref(route.query.q?.toString() ?? "");

function saveFilterStateToRoute() {
  navigateTo({
    query: {
      ...route.query,
      q: query.value,
      documentKind: documentKind.value,
      dateFilterType: dateFilter.value.type,
      dateFilterFrom: dateFilter.value.from ?? "",
      dateFilterTo: dateFilter.value.to ?? "",
    },
  });
}

const { searchResults, submitSearch } = await useAdvancedSearch(
  query,
  documentKind,
  dateFilter,
);

function submit() {
  saveFilterStateToRoute();
  submitSearch();
}
</script>

<template>
  <ContentWrapper class="pb-32 lg:pb-64">
    <RisBreadcrumb title="Erweiterte Suche" base-path="/" />

    <div
      class="mt-24 grid grid-cols-1 gap-40 lg:grid-cols-[330px_1fr] lg:gap-64"
    >
      <div class="lg:col-span-2">
        <h1 class="ris-heading2-bold mb-16">Erweiterte Suche</h1>
        <p class="ris-body1-regular text-balance">
          Nutzen Sie die erweiterte Suche, um genau das zu finden, was Sie
          brauchen – ob im Leitsatz, Titel oder direkt im Volltext. Mit
          Suchoperatoren wie AND, OR und NOT bekommen Sie noch präzisere
          Ergebnisse.
        </p>
      </div>

      <div class="row-start-3 lg:row-start-auto">
        <div class="mb-40">
          <div class="ris-label1-regular mb-8">Dokumentart</div>
          <PanelMenu
            :model="documentKindMenuItems"
            :expanded-keys="{ [documentKind]: true }"
            :pt="{
              headercontent: { class: 'group' },
              headerlink: { class: 'no-underline group-hover:underline' },
              itemlink: { class: 'no-underline group-hover:underline' },
            }"
          />
        </div>

        <DateFilter v-model="dateFilter" :document-kind />
      </div>

      <div class="row-start-2 lg:row-start-auto">
        <DataFieldPicker
          v-model="query"
          :data-fields="queryableDataFields"
          :count="100000"
          :document-kind
          :loading="searchStatus === 'pending'"
          @submit="submit"
        />

        <output v-if="searchResults">
          <!-- eslint-disable-next-line vue/valid-v-for TODO: provide proper key -->
          <SearchResult
            v-for="searchResult in searchResults.member"
            :search-result
            :order="1"
          />
        </output>
      </div>
    </div>
  </ContentWrapper>
</template>
