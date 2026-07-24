<script setup lang="ts">
import { debounce } from "lodash-es";
import type {
  AutoCompleteCompleteEvent,
  AutoCompleteDropdownClickEvent,
} from "primevue/autocomplete";
import type { AutoCompleteSuggestion } from "~/components/AutoComplete.vue";
import type { CourtSearchResult, CourtsSearchParams } from "~/types/api";
import { courtFilterDefaultSuggestions } from "~/utils/search/courtFilter";

const { appendTo = "self" } = defineProps<{
  /**
   * Where to render the suggestions overlay. "self" (the default) keeps it
   * width-matched to the field, but gets clipped by any scrollable ancestor
   * (e.g. a Drawer). Use "body" in such contexts instead.
   */
  appendTo?: "self" | "body";
}>();

const model = defineModel<string | undefined>();

const searchResults = ref<CourtSearchResult[]>([]);

const search = async (prefix?: string) => {
  const params: CourtsSearchParams = prefix ? { prefix } : {};
  searchResults.value = await $fetch<CourtSearchResult[]>(
    useBackendUrl("/v1/case-law/courts"),
    {
      params: params,
    },
  );
};

const searchDebounced = debounce(search, 250);

/*
Workaround for loading prop being ignored in PrimeVue AutoComplete:
It is important that the suggestions.value be updated each time. Otherwise, the loading indicator will not disappear
the second time that the default suggestions are invoked using the dropdown.

Both onComplete and onDropdownClick are called when the dropdown is opened,
but only onDropdownClick is called on close.

See https://github.com/primefaces/primevue/issues/5601 for further information.
 */
const onComplete = (
  event:
    | AutoCompleteCompleteEvent
    | AutoCompleteDropdownClickEvent
    | { query: undefined },
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
  searchResults.value
    .filter(
      (i): i is typeof i & { id: string; label: string } => !!i.id && !!i.label,
    )
    .map((i) => ({
      id: i.id,
      label: i.label,
      secondaryLabel: i.id,
    })),
);

const id = useId();
</script>

<template>
  <div class="flex flex-col gap-8">
    <label :id="id" class="typo-label2-regular">Bundesgericht</label>
    <AutoComplete
      v-model="model"
      :aria-labelledby="id"
      :append-to="appendTo"
      :suggestions
      dropdown
      dropdown-mode="blank"
      placeholder="Auswählen oder suchen"
      :pt="
        appendTo === 'body'
          ? {
              // AutoComplete isn't customized by ris-ui, so this reproduces
              // its default overlay classes without `w-full` - which forces
              // the panel to the viewport's width when appended to body,
              // pushing it flush against the screen edges instead of aligning
              // with the field. Providing pt.overlay.class here replaces
              // rather than merges with the default, so all of the classes
              // need to be repeated.
              overlay: {
                class: 'mt-12 overflow-auto bg-white px-8 py-12 shadow-md',
              },
            }
          : undefined
      "
      typeahead
      @complete="onComplete"
      @dropdown-click="onDropdownClick"
      @item-select="onItemSelect"
    />
  </div>
</template>
