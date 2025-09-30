<script setup lang="ts">
import { PanelMenu } from "primevue";
import type { MenuItem } from "primevue/menuitem";
import DataFieldPicker from "~/components/AdvancedSearch/DataFieldPicker.vue";
import type { DateFilterValue } from "~/components/AdvancedSearch/DateFilter.vue";
import DateFilter from "~/components/AdvancedSearch/DateFilter.vue";
import ContentWrapper from "~/components/CustomLayouts/ContentWrapper.vue";
import { queryableDataFields } from "~/pages/advanced-search/dataFields";
import { DocumentKind } from "~/types";
import { formatDocumentKind } from "~/utils/displayValues";

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

const documentKind = ref<DocumentKind>(DocumentKind.Norm);

const dateFilter = ref<DateFilterValue>({ type: "allTime" });

const query = ref("");
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
        />
      </div>
    </div>
  </ContentWrapper>
</template>
