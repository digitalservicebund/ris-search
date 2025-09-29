<script setup lang="ts">
import DataFieldPicker from "~/components/AdvancedSearch/DataFieldPicker.vue";
import ContentWrapper from "~/components/CustomLayouts/ContentWrapper.vue";
import { queryableDataFields } from "~/pages/advanced-search/dataFields";
import { DocumentKind } from "~/types";

useHead({ title: "Erweiterte Suche" });
definePageMeta({ alias: "/erweiterte-suche" });
</script>

<template>
  <ContentWrapper class="pb-32 lg:pb-64">
    <div class="grid grid-cols-1 gap-40 lg:grid-cols-[330px_1fr] lg:gap-64">
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
        <div class="h-384 w-full bg-blue-200"></div>
      </div>

      <div class="row-start-2 lg:row-start-auto">
        <DataFieldPicker
          :data-fields="queryableDataFields"
          :count="100000"
          :document-kind="DocumentKind.CaseLaw"
        />
      </div>
    </div>
  </ContentWrapper>
</template>
