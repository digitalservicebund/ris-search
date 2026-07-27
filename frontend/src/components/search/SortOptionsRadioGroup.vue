<script setup lang="ts">
import { RadioButton } from "primevue";
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

const name = useId();
const legendId = useId();
</script>

<template>
  <fieldset :aria-labelledby="legendId" class="flex flex-col gap-16">
    <legend :id="legendId" class="typo-label1-bold mb-16">Sortieren nach</legend>
    <div
      v-for="option in options"
      :key="option.value"
      class="flex items-center gap-8"
    >
      <RadioButton
        v-model="model"
        :input-id="`${name}-${option.value}`"
        :name="name"
        :value="option.value"
      />
      <label :for="`${name}-${option.value}`">{{ option.label }}</label>
    </div>
  </fieldset>
</template>
