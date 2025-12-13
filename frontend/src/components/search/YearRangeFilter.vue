<script setup lang="ts">
import PrimevueSelect from "primevue/select";
import YearInput from "~/components/YearInput.vue";
import { DateSearchMode } from "~/composables/useSimpleSearchParams/useSimpleSearchParams";

const dateAfter = defineModel<string>("dateAfter");
const dateBefore = defineModel<string>("dateBefore");
const dateSearchMode = defineModel<DateSearchMode>("dateSearchMode", {
  required: true,
});

const yearEqualId = useId();
const yearAfterId = useId();
const yearBeforeId = useId();

const items = [
  { label: "Keine zeitliche Begrenzung", value: DateSearchMode.None },
  { label: "Bis zu einem Jahr", value: DateSearchMode.Before },
  { label: "In einem Jahr", value: DateSearchMode.Equal },
  { label: "Ab einem Jahr", value: DateSearchMode.After },
  { label: "In einem Zeitraum", value: DateSearchMode.Range },
];

// Helpers
function parseYearFromDate(date?: string): string | undefined {
  return date?.split("-")[0];
}

function formatYearStart(year: string) {
  return `${year}-01-01`;
}

function formatYearEnd(year: string) {
  return `${year}-12-31`;
}

//  Years Computed(s)
const yearAfter = computed<string | undefined>({
  get() {
    return parseYearFromDate(dateAfter.value);
  },
  set(value) {
    if (!value) {
      dateAfter.value = undefined;
      return;
    }
    switch (dateSearchMode.value) {
      case DateSearchMode.After:
        dateAfter.value = formatYearStart(value);
        dateBefore.value = undefined;
        break;
      case DateSearchMode.Range:
        dateAfter.value = formatYearStart(value);
        break;
    }
  },
});

const yearBefore = computed<string | undefined>({
  get() {
    return parseYearFromDate(dateBefore.value);
  },
  set(value) {
    if (!value) {
      dateBefore.value = undefined;
      return;
    }
    switch (dateSearchMode.value) {
      case DateSearchMode.Before:
        dateBefore.value = formatYearEnd(value);
        dateAfter.value = undefined;
        break;
      case DateSearchMode.Range:
        dateBefore.value = formatYearEnd(value);
        break;
    }
  },
});

const yearEqual = computed<string | undefined>({
  get() {
    const yAfter = parseYearFromDate(dateAfter.value);
    const yBefore = parseYearFromDate(dateBefore.value);
    // Only show value if both exist and equal
    if (yAfter && yBefore && yAfter === yBefore) return yAfter;
    return undefined;
  },
  set(value) {
    if (!value) {
      dateAfter.value = undefined;
      dateBefore.value = undefined;
      return;
    }
    dateAfter.value = formatYearStart(value);
    dateBefore.value = formatYearEnd(value);
  },
});

// UI logic
const show = computed(() => ({
  equal: dateSearchMode.value === DateSearchMode.Equal,
  after: [DateSearchMode.After, DateSearchMode.Range].includes(
    dateSearchMode.value,
  ),
  before: [DateSearchMode.Before, DateSearchMode.Range].includes(
    dateSearchMode.value,
  ),
}));

const hasMultipleInputs = computed(() => show.value.after && show.value.before);

// Reset on mode change
watch(
  () => dateSearchMode.value,
  () => {
    dateAfter.value = undefined;
    dateBefore.value = undefined;
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
        v-model="dateSearchMode"
        :options="items"
        :placeholder="items[0]?.label"
        :pt="{ overlay: { class: 'bg-white w-full' } }"
        append-to="self"
        option-label="label"
        option-value="value"
        scroll-height="20rem"
      />
    </span>

    <div v-if="show.equal" class="flex flex-col gap-8">
      <label class="ris-label2-regular" :for="yearEqualId">Jahr</label>
      <YearInput :id="yearEqualId" v-model="yearEqual" />
    </div>

    <div v-if="show.after" class="flex flex-col gap-8">
      <label class="ris-label2-regular" :for="yearAfterId">
        <template v-if="hasMultipleInputs">Ab dem Jahr</template>
        <template v-else>Jahr</template>
      </label>
      <YearInput :id="yearAfterId" v-model="yearAfter" />
    </div>

    <div v-if="show.before" class="flex flex-col gap-8">
      <label class="ris-label2-regular" :for="yearBeforeId">
        <template v-if="hasMultipleInputs">Bis zum Jahr</template>
        <template v-else>Jahr</template>
      </label>
      <YearInput :id="yearBeforeId" v-model="yearBefore" />
    </div>
  </div>
</template>
