<script setup lang="ts">
import { debounce } from "lodash-es";
import type { ComboboxOption } from "~/components/ui/Combobox.vue";
import type { CourtSearchResult, CourtsSearchParams } from "~/types/api";
import {
  courtFilterDefaultSuggestions,
  knownCourtLabels,
} from "~/utils/search/courtFilter";

const { appendTo } = defineProps<{
  /**
   * Where to portal the suggestion panel. Defaults to `document.body`. Pass a
   * Drawer's `append-target` when rendering inside one to guarantee correct
   * placement of the overlay.
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

watch(
  searchResults,
  (results) => {
    for (const result of results) {
      if (result.id && result.label)
        knownCourtLabels.set(result.id, result.label);
    }
  },
  { immediate: true },
);

// Shows immediately for a modelValue whose label we already know, e.g. right
// after this component remounts (mobile filter drawer) with a court already
// selected.
const initialLabel = computed(() =>
  modelValue.value ? knownCourtLabels.get(modelValue.value) : undefined,
);

// searchTerm also holds the selected option's label after selecting it, not
// just typed text. Tracked so we can tell those apart below.
const lastSelectedLabel = ref<string>();

watch(modelValue, (selectedId) => {
  lastSelectedLabel.value = selectedId
    ? knownCourtLabels.get(selectedId)
    : undefined;
  searchResults.value = [];
});

watch(open, (isOpen) => {
  if (!isOpen) {
    searchResults.value = [];
    return;
  }

  // Ignore searchTerm if it's just the selected label, not a real query.
  if (searchTerm.value && searchTerm.value !== lastSelectedLabel.value) {
    searchDebounced(searchTerm.value);
  } else if (modelValue.value) {
    search(modelValue.value);
  } else {
    searchResults.value = [...courtFilterDefaultSuggestions];
  }
});

const id = useId();
</script>

<template>
  <div class="flex flex-col gap-4">
    <label class="typo-label1-bold" :id="id">Gericht</label>
    <small class="ris-label2-regular text-pretty">
      Bundesgericht auswählen oder weiteres Gericht suchen
    </small>

    <UiCombobox
      v-model="modelValue"
      v-model:open="open"
      v-model:search-term="searchTerm"
      :append-to="appendTo"
      :aria-labelledby="id"
      :initial-label="initialLabel"
      :loading="loading"
      :options="options"
      placeholder="Auswählen oder suchen"
    />
  </div>
</template>
