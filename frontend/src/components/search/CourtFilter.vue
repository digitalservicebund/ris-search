<script setup lang="ts">
import { debounce } from "lodash-es";
import type { ComboboxOption } from "~/components/ui/Combobox.vue";
import type { CourtSearchResult, CourtsSearchParams } from "~/types/api";
import { courtFilterDefaultSuggestions } from "~/utils/search/courtFilter";

const { appendTo } = defineProps<{
  /**
   * Where to portal the suggestion panel. Pass a Drawer's `append-target` (from
   * its default slot scope) when rendering inside one: a native `<dialog>`
   * makes everything outside its own DOM subtree inert, so the default of
   * `document.body` is unreachable there.
   */
  appendTo?: HTMLElement;
}>();

const modelValue = defineModel<string | undefined>();

const searchResults = ref<CourtSearchResult[]>([]);
const searchTerm = ref("");
const open = ref(false);
const loading = ref(false);

const { $risBackend } = useNuxtApp();

const search = async (prefix?: string) => {
  loading.value = true;
  try {
    const query: CourtsSearchParams = prefix ? { prefix } : {};
    searchResults.value = await $risBackend<CourtSearchResult[]>(
      "/v1/rechtsprechung/courts",
      { query },
    );
  } finally {
    loading.value = false;
  }
};

const searchDebounced = debounce(search, 250);

const options = computed<ComboboxOption[]>(() =>
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

watch(searchTerm, (term) => {
  if (term) searchDebounced(term);
});

// The combobox writes the selected option's expanded label into searchTerm
// once a selection is made (so it displays as the input value), not just
// what the user actually typed. Tracked here to tell the two apart: the
// court search endpoint filters by court key (e.g. "BGH"), and an expanded
// label like "Bundesgerichtshof" wouldn't match a court's key prefix.
const lastSelectedLabel = ref<string>();

watch(open, (isOpen) => {
  if (!isOpen) {
    searchResults.value = [];
    return;
  }
  // Re-run the search whenever the panel opens, rather than relying on the
  // searchTerm watcher above: searchTerm can already hold a value without
  // the user having typed anything since results were last cleared on close.
  if (searchTerm.value && searchTerm.value !== lastSelectedLabel.value) {
    searchDebounced(searchTerm.value);
  } else if (modelValue.value) {
    search(modelValue.value);
  } else {
    searchResults.value = [...courtFilterDefaultSuggestions];
  }
});

watch(modelValue, (selectedId) => {
  lastSelectedLabel.value = selectedId
    ? options.value.find((option) => option.id === selectedId)?.label
    : undefined;
  searchResults.value = [];
});

const id = useId();
</script>

<template>
  <div class="flex flex-col gap-4">
    <label :id="id" class="typo-label1-bold">Gericht</label>
    <small class="ris-label2-regular text-pretty">
      Bundesgericht auswählen oder weiteres Gericht suchen
    </small>
    <UiCombobox
      v-model="modelValue"
      v-model:search-term="searchTerm"
      v-model:open="open"
      :options="options"
      :loading="loading"
      :append-to="appendTo"
      :aria-labelledby="id"
      placeholder="Auswählen oder suchen"
    />
  </div>
</template>
