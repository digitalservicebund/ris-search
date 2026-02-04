<script setup lang="ts">
import {
  RisAutoComplete,
  type AutoCompleteSuggestion,
} from "@digitalservicebund/ris-ui/components";
import _ from "lodash";
import type { AutoCompleteDropdownClickEvent } from "primevue/autocomplete";
import useBackendUrl from "~/composables/useBackendUrl";
import type { CourtSearchResult } from "~/types";
import { courtFilterDefaultSuggestions } from "~/utils/search/courtFilter";

const model = defineModel<string | undefined>();

const searchResults = ref<CourtSearchResult[]>([]);

const search = async (prefix?: string) => {
  const params = prefix ? { prefix } : {};
  searchResults.value = await $fetch<CourtSearchResult[]>(
    useBackendUrl("/v1/case-law/courts"),
    {
      params: params,
    },
  );
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
  } else if (model.value) {
    // user has already made a selection, use that as the prefix
    searchDebounced(model.value);
  } else {
    // dropdown was opened without any text entered or value pre-selected
    // a copy of the default suggestions is required since the loading
    searchResults.value = [...courtFilterDefaultSuggestions];
  }
};

const onDropdownClick = (
  event: AutoCompleteDropdownClickEvent | { query: undefined },
) => {
  if (event.query === undefined) {
    // dropdown has been closed
    searchResults.value = [];
  } else {
    // onComplete will also fire, but with an empty query
    // therefore, call it again
    onComplete(event);
  }
};

const onItemSelect = () => {
  searchResults.value = [];
};

const suggestions = computed<AutoCompleteSuggestion[]>(() =>
  searchResults.value.map((i) => ({
    id: i.id,
    label: i.label,
    secondaryLabel: i.id,
  })),
);

const id = useId();
</script>

<template>
  <div class="flex flex-col gap-8">
    <label :for="id" class="ris-label2-regular">Bundesgericht</label>
    <RisAutoComplete
      v-model="model"
      :input-id="id"
      :suggestions
      append-to="self"
      dropdown
      dropdown-mode="blank"
      placeholder="Auswählen oder suchen"
      typeahead
      @complete="onComplete"
      @dropdown-click="onDropdownClick"
      @item-select="onItemSelect"
    />
  </div>
</template>
