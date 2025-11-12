<script setup lang="ts">
import { RisAutoComplete } from "@digitalservicebund/ris-ui/components";
import _ from "lodash";
import type { AutoCompleteDropdownClickEvent } from "primevue/autocomplete";
import { useSimpleSearchParamsStore } from "~/stores/searchParams";
import { DocumentKind } from "~/types";
import { courtFilterDefaultSuggestions } from "~/utils/search/courtFilter";

const store = useSimpleSearchParamsStore();

const isCaseLaw = computed(() =>
  store.category?.startsWith(DocumentKind.CaseLaw),
);

type CourtSearchResult = { id: string; label: string; count?: number };

const suggestions = ref<CourtSearchResult[]>([]);

const search = async (prefix?: string) => {
  const params = prefix ? { prefix } : {};
  suggestions.value = await $fetch<CourtSearchResult[]>("/v1/case-law/courts", {
    params: params,
  });
};
const searchDebounced = _.debounce(search, 250);

/*
Workaround for loading prop being ignored in PrimeVue AutoComplete:
It is important that the suggestions.value be updated each time. Otherwise, the loading indicator will not disappear
the second time that the default suggestions are invoked using the dropdown.

Both onComplete and onDropdownClick are called when the dropdown is opened,
but only onDropdownClick is called on close.

See https://github.com/primefaces/primevue/issues/5601 for further information.
 */
const onComplete = (
  event: AutoCompleteDropdownClickEvent | { query: undefined },
) => {
  if (event.query) {
    // normal search for entered prefix
    searchDebounced(event.query);
  } else if (store.court) {
    // user has already made a selection, use that as the prefix
    searchDebounced(store.court);
  } else {
    // dropdown was opened without any text entered or value pre-selected
    // a copy of the default suggestions is required since the loading
    suggestions.value = [...courtFilterDefaultSuggestions];
  }
};

const onDropdownClick = (
  event: AutoCompleteDropdownClickEvent | { query: undefined },
) => {
  if (event.query === undefined) {
    // dropdown has been closed
    suggestions.value = [];
  } else {
    // onComplete will also fire, but with an empty query
    // therefore, call it again
    onComplete(event);
  }
};

const onItemSelect = () => {
  suggestions.value = [];
};

const autoComplete = ref<typeof RisAutoComplete | null>(null);
</script>

<template>
  <InputField v-if="isCaseLaw" id="courtFilter" v-slot="{ id }" label="Gericht">
    <RisAutoComplete
      ref="autoComplete"
      v-model="store.court"
      typeahead
      dropdown
      dropdown-mode="blank"
      :suggestions="suggestions"
      :input-id="id"
      append-to="self"
      @complete="onComplete"
      @dropdown-click="onDropdownClick"
      @item-select="onItemSelect"
    />
  </InputField>
</template>
