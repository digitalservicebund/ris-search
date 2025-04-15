<script setup lang="ts">
import { DocumentKind } from "@/types";
import { type DropdownItem, sortMode } from "@/components/types";
import { computed } from "vue";
import PrimevueSelect from "primevue/select";

const props = defineProps<{ documentKind: DocumentKind }>();
const model = defineModel<DropdownItem["value"]>();

const reversedSortMode = (name: string) => "-" + name;

const sharedSortOptions: DropdownItem[] = [
  { label: "Relevanz", value: "default" },
  { label: "Datum: Älteste zuerst", value: sortMode.date },
  { label: "Datum: Neueste zuerst", value: reversedSortMode(sortMode.date) },
];

const caselawSortOptions: DropdownItem[] = [
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

const legislationSortOptions: DropdownItem[] = [
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
</script>

<template>
  <span class="flex w-auto items-center gap-8">
    <label for="sort-select" class="ris-label2-regular">Sortieren nach</label>
    <PrimevueSelect
      id="sort-select"
      v-model="model"
      scroll-height="20rem"
      option-label="label"
      option-value="value"
      :options="validSortOptions"
    />
  </span>
</template>
