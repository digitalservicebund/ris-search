<script setup lang="ts">
import { DocumentKind } from "~/types/api";
import { validSortOptions } from "~/utils/search/sortOptions";

const props = defineProps<{ documentKind: DocumentKind }>();
const model = defineModel<string>();

const options = computed(() => validSortOptions(props.documentKind));

watch(options, (newVal) => {
  // Reset to a sensible default if the document kind doesn't support the current
  // sort value
  if (!model.value) return;
  const optionsForDocumentKind = newVal.map((i) => i.value);
  if (!optionsForDocumentKind.includes(model.value)) {
    model.value = newVal[0]?.value;
  }
});

const sortLabelId = useId();
</script>

<template>
  <span class="flex w-auto items-center gap-8">
    <label :id="sortLabelId" class="typo-label2-regular">Sortieren nach</label>
    <UiSelect
      :aria-labelledby="sortLabelId"
      v-model="model"
      :options="options"
    />
  </span>
</template>
