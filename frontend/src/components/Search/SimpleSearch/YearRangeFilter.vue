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

// Helpers
function parseYearFromDate(date?: string): string | null {
  return date?.split("-")[0] ?? null;
}
function isYearValid(year: string | null) {
  return !!year && /^\d{4}$/.test(year);
}
function formatYearStart(year: string | null) {
  return `${year}-01-01`;
}
function formatYearEnd(year: string | null) {
  return `${year}-12-31`;
}

//  Years Computed(s)
const yearAfter = computed<string | null>({
  get() {
    return parseYearFromDate(store.dateAfter);
  },
  set(value) {
    if (!isYearValid(value)) {
      store.dateAfter = undefined;
      return;
    }
    switch (store.dateSearchMode) {
      case DateSearchMode.After:
        store.dateAfter = formatYearStart(value);
        store.dateBefore = undefined;
        break;
      case DateSearchMode.Range:
        store.dateAfter = formatYearStart(value);
        // keep dateBefore only if valid
        if (!isYearValid(parseYearFromDate(store.dateBefore))) {
          store.dateBefore = undefined;
        }
        break;
    }
  },
});

const yearBefore = computed<string | null>({
  get() {
    return parseYearFromDate(store.dateBefore);
  },
  set(value) {
    if (!isYearValid(value)) {
      store.dateBefore = undefined;
      return;
    }
    switch (store.dateSearchMode) {
      case DateSearchMode.Before:
        store.dateBefore = formatYearEnd(value);
        store.dateAfter = undefined;
        break;
      case DateSearchMode.Range:
        store.dateBefore = formatYearEnd(value);
        if (!isYearValid(parseYearFromDate(store.dateAfter))) {
          store.dateAfter = undefined;
        }
        break;
    }
  },
});

const yearEqual = computed<string | null>({
  get() {
    const yAfter = parseYearFromDate(store.dateAfter);
    const yBefore = parseYearFromDate(store.dateBefore);
    // Only show value if both exist and equal
    if (yAfter && yBefore && yAfter === yBefore) return yAfter;
    return null;
  },
  set(value) {
    if (!isYearValid(value)) {
      store.dateAfter = undefined;
      store.dateBefore = undefined;
      return;
    }
    store.dateAfter = formatYearStart(value);
    store.dateBefore = formatYearEnd(value);
  },
});

// UI logic
const show = computed(() => ({
  equal: store.dateSearchMode === DateSearchMode.Equal,
  after: [DateSearchMode.After, DateSearchMode.Range].includes(
    store.dateSearchMode,
  ),
  before: [DateSearchMode.Before, DateSearchMode.Range].includes(
    store.dateSearchMode,
  ),
}));
const hasMultipleInputs = computed(() => show.value.after && show.value.before);

// Reset on mode change
watch(
  () => store.dateSearchMode,
  () => {
    store.dateAfter = undefined;
    store.dateBefore = undefined;
  },
  { immediate: false },
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
      v-if="show.equal"
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
      v-if="show.after"
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
      v-if="show.before"
      id="yearBefore"
      v-slot="slotProps"
      :label="hasMultipleInputs ? 'Bis zum Jahr' : 'Jahr'"
    >
      <InputMask
        :id="slotProps.id"
        v-model="yearBefore"
        mask="9999"
        placeholder="JJJJ"
        :has-error="slotProps.hasError"
        @update:validation-error="slotProps.updateValidationError"
      />
    </InputField>
  </div>
</template>
