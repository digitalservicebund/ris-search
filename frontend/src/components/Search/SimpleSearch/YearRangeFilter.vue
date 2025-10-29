<script setup lang="ts">
import InputMask from "primevue/inputmask";
import PrimevueSelect from "primevue/select";
import type { DropdownItem } from "~/components/types";
import {
  useSimpleSearchParamsStore,
  DateSearchMode,
} from "~/stores/searchParams";

const store = useSimpleSearchParamsStore();

const items: DropdownItem[] = [
  { label: "Keine zeitliche Begrenzung", value: DateSearchMode.None },
  { label: "Bis zu einem Jahr", value: DateSearchMode.Before },
  { label: "In einem Jahr", value: DateSearchMode.Equal },
  { label: "Ab einem Jahr", value: DateSearchMode.After },
  { label: "In einem Zeitraum", value: DateSearchMode.Range },
];

// Local reactive values for the year inputs
const yearAfter = ref<string | null>(null);
const yearBefore = ref<string | null>(null);
const yearEqual = ref<string | null>(null);

const showYearField = computed(
  () => store.dateSearchMode === DateSearchMode.Equal,
);
const showYearAfterField = computed(
  () =>
    store.dateSearchMode === DateSearchMode.After ||
    store.dateSearchMode === DateSearchMode.Range,
);
const showYearBeforeField = computed(
  () =>
    store.dateSearchMode === DateSearchMode.Before ||
    store.dateSearchMode === DateSearchMode.Range,
);
const hasMultipleInputs = computed(
  () => showYearAfterField.value && showYearBeforeField.value,
);

function isYearValid(year: string | null) {
  return !!year && /^\d{4}$/.test(year);
}

function formatYearStart(year: string | null) {
  return `${year}-01-01`;
}

function formatYearEnd(year: string | null) {
  return `${year}-12-31`;
}

// watching the local refs for updating the store
watch(
  [yearAfter, yearBefore, yearEqual],
  () => {
    switch (store.dateSearchMode) {
      case DateSearchMode.None:
        store.dateAfter = undefined;
        store.dateBefore = undefined;
        break;
      case DateSearchMode.After:
        if (isYearValid(yearAfter.value)) {
          store.dateAfter = formatYearStart(yearAfter.value);
          store.dateBefore = undefined;
        }
        break;
      case DateSearchMode.Before:
        if (isYearValid(yearBefore.value)) {
          store.dateBefore = formatYearEnd(yearBefore.value);
          store.dateAfter = undefined;
        }
        break;
      case DateSearchMode.Equal:
        if (isYearValid(yearEqual.value)) {
          store.dateAfter = formatYearStart(yearEqual.value);
          store.dateBefore = formatYearEnd(yearEqual.value);
        }
        break;
      case DateSearchMode.Range:
        if (isYearValid(yearAfter.value) && isYearValid(yearBefore.value)) {
          store.dateAfter = formatYearStart(yearAfter.value);
          store.dateBefore = formatYearEnd(yearBefore.value);
        }
        break;
    }
  },
  { immediate: true },
);

//
watch(
  [store.dateSearchMode],
  () => {
    yearAfter.value = null;
    yearBefore.value = null;
    yearEqual.value = null;

    store.dateBefore = undefined;
    store.dateAfter = undefined;
  },
  { immediate: true },
);
</script>

<template>
  <div class="flex flex-col gap-16">
    <span class="flex flex-col gap-8">
      <label for="year-mode-select" class="ris-label2-regular">Zeitraum</label>
      <PrimevueSelect
        id="year-mode-select"
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

    <InputField
      v-if="showYearField"
      id="yearEqual"
      v-slot="slotProps"
      label="Jahr"
    >
      <InputMask
        :id="slotProps.id"
        v-model="yearEqual"
        mask="9999"
        placeholder="JJJJ"
        :has-error="slotProps.hasError"
        @update:validation-error="slotProps.updateValidationError"
      />
    </InputField>

    <InputField
      v-if="showYearAfterField"
      id="yearAfter"
      v-slot="slotProps"
      :label="hasMultipleInputs ? 'Ab dem Jahr' : 'Jahr'"
    >
      <InputMask
        :id="slotProps.id"
        v-model="yearAfter"
        mask="9999"
        placeholder="JJJJ"
        :has-error="slotProps.hasError"
        @update:validation-error="slotProps.updateValidationError"
      />
    </InputField>

    <InputField
      v-if="showYearBeforeField"
      id="yearBefore"
      v-slot="slotProps"
      :label="hasMultipleInputs ? 'Bis zum Jahr' : 'Jahr'"
    >
      <InputMask
        :id="slotProps.id"
        v-model="yearBefore"
        mask="9999"
        placeholder="YYYY"
        :has-error="slotProps.hasError"
        @update:validation-error="slotProps.updateValidationError"
      />
    </InputField>
  </div>
</template>
