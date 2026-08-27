<script setup lang="ts">
import { NuxtLink } from "#components";
import type { RecentSectionDocumentKind } from "~/composables/useRecentSectionSearchResults";
import { DocumentKind } from "~/types/api";

const labels: Record<
  RecentSectionDocumentKind,
  { tab: string; button: string }
> = {
  [DocumentKind.Norm]: {
    tab: "Gesetze und Verordnungen",
    button: "Zu den Gesetzen und Verordnungen",
  },
  [DocumentKind.CaseLaw]: {
    tab: "Gerichtsentscheidungen",
    button: "Zu den Gerichtsentscheidungen",
  },
  [DocumentKind.AdministrativeDirective]: {
    tab: "Verwaltungsvorschriften",
    button: "Zu den Verwaltungsvorschriften",
  },
  [DocumentKind.Literature]: {
    tab: "Literaturnachweise",
    button: "Zu den Literaturnachweisen",
  },
};

const idBase = useId();

const tabId = (documentKind: RecentSectionDocumentKind) =>
  `${idBase}-${documentKind}`;

const searches = await useRecentSectionSearchResults();

const selected = ref<RecentSectionDocumentKind>(
  RECENT_SECTION_DOCUMENT_KINDS[0],
);

const tabs = searches.map(({ documentKind }) => ({
  documentKind,
  id: tabId(documentKind),
  label: labels[documentKind].tab,
}));

const activeTab = computed(() => {
  const documentKind = selected.value;
  const search = searches.find((s) => s.documentKind === documentKind);

  return {
    documentKind,
    id: tabId(documentKind),
    buttonLabel: labels[documentKind].button,
    results: search?.searchResults.value ?? [],
    error: search?.searchError.value,
  };
});
</script>

<template>
  <h2 class="typo-headline2-bold mb-4 wrap-break-word hyphens-auto md:mb-12">
    Aktuelles
  </h2>

  <UiTabs aria-label="Aktuelles nach Dokumentart" scroller-class="px-16 -mx-16">
    <UiTab
      v-for="tab in tabs"
      :id="tab.id"
      :key="tab.documentKind"
      :active="tab.documentKind === selected"
      @click="selected = tab.documentKind"
    >
      {{ tab.label }}
    </UiTab>
  </UiTabs>

  <div role="tabpanel" :aria-labelledby="activeTab.id" class="mt-16 md:mt-24">
    <UiMessage v-if="activeTab.error" severity="error" role="alert">
      Die Ergebnisse konnten nicht geladen werden.
    </UiMessage>

    <p v-else-if="!activeTab.results.length" class="typo-body-regular">
      Derzeit keine Einträge verfügbar.
    </p>

    <ul v-else class="flex flex-col gap-4 md:gap-8">
      <li
        v-for="(searchResult, order) in activeTab.results"
        :key="getIdentifier(searchResult.item)"
        class="border border-gray-400 bg-white p-16"
      >
        <SearchResult :search-result :order heading-level="3" />
      </li>
    </ul>

    <UiButton
      class="mt-16 w-full md:mt-24 md:w-auto"
      :as="NuxtLink"
      :to="{
        name: 'suche',
        query: { documentKind: activeTab.documentKind },
      }"
    >
      {{ activeTab.buttonLabel }}
    </UiButton>
  </div>
</template>
