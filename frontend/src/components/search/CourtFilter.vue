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

watch(searchTerm, (term) => {
  if (term) searchDebounced(term);
});

watch(open, (isOpen) => {
  if (!isOpen) {
    searchResults.value = [];
    return;
  }
  // Typing already drives a search via the searchTerm watcher above; this
  // only covers opening via the dropdown button, either blank or reusing an
  // existing selection as the prefix.
  if (searchTerm.value) return;
  if (modelValue.value) search(modelValue.value);
  else searchResults.value = [...courtFilterDefaultSuggestions];
});

watch(modelValue, () => {
  searchResults.value = [];
});

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
