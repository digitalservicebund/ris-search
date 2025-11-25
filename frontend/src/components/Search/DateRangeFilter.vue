<script setup lang="ts">
import PrimevueSelect from "primevue/select";
import type { DropdownItem } from "~/components/types";
import {
  useSimpleSearchParamsStore,
  DateSearchMode,
} from "~/stores/searchParams";

const store = useSimpleSearchParamsStore();

const items: DropdownItem[] = [
  { label: "Keine zeitliche Begrenzung", value: DateSearchMode.None },
  { label: "Bis zu einem Datum", value: DateSearchMode.Before },
  { label: "An einem Datum", value: DateSearchMode.Equal },
  { label: "Ab einem Datum", value: DateSearchMode.After },
  { label: "In einem Zeitraum", value: DateSearchMode.Range },
];

const showDateField = computed(
  () => store.dateSearchMode === DateSearchMode.Equal,
);
const showDateAfterField = computed(
  () =>
    store.dateSearchMode === DateSearchMode.After ||
    store.dateSearchMode === DateSearchMode.Range,
);
const showDateBeforeField = computed(
  () =>
    store.dateSearchMode === DateSearchMode.Before ||
    store.dateSearchMode === DateSearchMode.Range,
);
const hasMultipleInputs = computed(
  () => showDateAfterField.value && showDateBeforeField.value,
);
</script>

// the inner div is required, integration tests fail without it
<template>
  <div class="flex flex-col gap-16">
    <span class="flex flex-col gap-8">
      <label for="date-mode-select" class="ris-label2-regular">Zeitraum</label>
      <PrimevueSelect
        id="date-mode-select"
        v-model="store.dateSearchMode"
        :options="items"
        option-label="label"
        option-value="value"
        :placeholder="items[0]?.label"
        scroll-height="20rem"
        append-to="self"
        :pt="{ overlay: { class: 'bg-white w-full' } }"
      />
    </span>
    <InputField v-if="showDateField" id="date" v-slot="slotProps" label="Datum">
      <DateInput
        :id="slotProps.id"
        :key="slotProps.id"
        v-model="store.date"
        aria-label="Datum"
        :has-error="slotProps.hasError"
        @update:validation-error="slotProps.updateValidationError"
      ></DateInput>
    </InputField>
    <InputField
      v-if="showDateAfterField"
      id="dateAfter"
      v-slot="slotProps"
      :label="hasMultipleInputs ? 'Ab dem Datum' : 'Datum'"
    >
      <DateInput
        :id="slotProps.id"
        :key="slotProps.id"
        v-model="store.dateAfter"
        aria-label="Ab dem Datum"
        :has-error="slotProps.hasError"
        @update:validation-error="slotProps.updateValidationError"
      ></DateInput>
    </InputField>
    <InputField
      v-if="showDateBeforeField"
      id="dateBefore"
      v-slot="slotProps"
      :label="hasMultipleInputs ? 'Bis zum Datum' : 'Datum'"
    >
      <DateInput
        :id="slotProps.id"
        :key="slotProps.id"
        v-model="store.dateBefore"
        aria-label="Bis zum Datum"
        :has-error="slotProps.hasError"
        @update:validation-error="slotProps.updateValidationError"
      ></DateInput>
    </InputField>
  </div>
</template>
