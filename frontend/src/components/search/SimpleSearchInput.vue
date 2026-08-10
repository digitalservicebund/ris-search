<script setup lang="ts">
import IconSearch from "~icons/ic/search";

const {
  inputLabel = "Suchbegriff eingeben",
  inputPlaceholder = "Suchbegriff eingeben",
  submitLabel = "Suchen",
} = defineProps<{
  fullWidth?: boolean;
  inputLabel?: string;
  inputPlaceholder?: string;
  submitLabel?: string;
}>();

const model = defineModel<string>();

// currentText is decoupled from the model, we want to update
// the model only when the user performs a search
const currentText = ref<string | undefined>(model.value);

// propagate model updates back to the input
watch(model, (newValue) => {
  currentText.value = newValue;
});

const emit = defineEmits(["emptySearch"]);

const performSearch = () => {
  // if the user hasn't entered any text, updating the model will have no effect
  // since they might still want to trigger an empty search, use "emit"
  if (!currentText.value) emit("emptySearch");

  model.value = currentText.value;
};

const searchInputId = useId();
</script>

<template>
  <search :class="{ 'max-w-md': !fullWidth }">
    <form action="/suche" @submit.prevent="performSearch">
      <div class="flex">
        <UiInputText
          :id="searchInputId"
          v-model="currentText"
          :aria-label="inputLabel"
          :placeholder="inputPlaceholder"
          class="grow"
          clearable
          name="query"
          type="search"
          size="large"
        />
        <UiButton :aria-label="submitLabel" type="submit" size="large">
          <template #icon><IconSearch /></template>
        </UiButton>
      </div>
    </form>
  </search>
</template>
