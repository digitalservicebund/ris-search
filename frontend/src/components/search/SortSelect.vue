<script setup lang="ts">
import PrimevueSelect from "primevue/select";
import { computed } from "vue";
import { DocumentKind } from "~/types";
import { sortMode } from "~/utils/search/sortMode";

const props = defineProps<{ documentKind: DocumentKind }>();
const model = defineModel<string>();

const reversedSortMode = (name: string) => "-" + name;

const sharedSortOptions = [
  { label: "Relevanz", value: "default" },
  { label: "Datum: Älteste zuerst", value: sortMode.date },
  { label: "Datum: Neueste zuerst", value: reversedSortMode(sortMode.date) },
];

const caselawSortOptions = [
  { label: "Relevanz", value: "default" },
  { label: "Gericht: Von A nach Z", value: sortMode.courtName },
  {
    label: "Gericht: Von Z nach A",
    value: reversedSortMode(sortMode.courtName),
  },
  { label: "Entscheidungsdatum: Älteste zuerst", value: sortMode.date },
  {
    label: "Entscheidungsdatum: Neueste zuerst",
    value: reversedSortMode(sortMode.date),
  },
];

const legislationSortOptions = [
  { label: "Relevanz", value: "default" },
  { label: "Ausfertigungsdatum: Älteste zuerst", value: sortMode.date },
  {
    label: "Ausfertigungsdatum: Neueste zuerst",
    value: reversedSortMode(sortMode.date),
  },
];

const validSortOptions = computed(() => {
  switch (props.documentKind) {
    case DocumentKind.All:
      return sharedSortOptions;
    case DocumentKind.Norm:
      return legislationSortOptions;
    case DocumentKind.CaseLaw:
      return caselawSortOptions;
    default:
      return sharedSortOptions;
  }
});

watch(validSortOptions, (newVal) => {
  // Reset to a sensible default if the document kind doesn't support the current
  // sort value
  if (!model.value) return;
  const optionsForDocumentKind = newVal.map((i) => i.value);
  if (!optionsForDocumentKind.includes(model.value)) {
    model.value = newVal[0]?.value;
  }
});

const sortSelectId = useId();
</script>

<template>
  <span class="flex w-auto items-center gap-8">
    <label :for="sortSelectId" class="ris-label2-regular">Sortieren nach</label>
    <PrimevueSelect
      :id="sortSelectId"
      v-model="model"
      scroll-height="20rem"
      option-label="label"
      option-value="value"
      :options="validSortOptions"
    />
  </span>
</template>
