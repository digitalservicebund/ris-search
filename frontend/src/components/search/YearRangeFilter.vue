<script setup lang="ts">
import PrimevueSelect from "primevue/select";
import YearInput from "~/components/YearInput.vue";
import type { DateFilterValue, FilterType } from "~/utils/search/filterType";

/** Active date filter value */
const filter = defineModel<DateFilterValue>({ required: true });

/**
 * Internal mode for the dropdown. Needed because "equal year" and "range" both
 * map to FilterType "period" but need different UI representations.
 */
type YearMode = "allTime" | "before" | "equal" | "after" | "range";

const yearModeSelectId = useId();
const yearEqualId = useId();
const yearAfterId = useId();
const yearBeforeId = useId();

const items: { label: string; value: YearMode }[] = [
  { label: "Keine zeitliche Begrenzung", value: "allTime" },
  { label: "Bis zu einem Jahr", value: "before" },
  { label: "In einem Jahr", value: "equal" },
  { label: "Ab einem Jahr", value: "after" },
  { label: "In einem Zeitraum", value: "range" },
];

// The key will be set to a random value when the filter type changes to force
// Vue to fully re-render the date input. This is a workaround to fix a
// rendering bug where the date input of the "from" date is sometimes reused for
// the "to" date after switching filter types, resulting in a broken in-between
// state of the input mask.
const key = ref<string>();

function parseYearFromDate(date?: string): string | undefined {
  return date?.split("-")[0];
}

function formatYearStart(year: string) {
  return `${year}-01-01`;
}

function formatYearEnd(year: string) {
  return `${year}-12-31`;
}

/**
 * Derives the internal dropdown mode from a DateFilterValue. For "period" type,
 * checks whether both dates are in the same year ("equal") or different
 * ("range"). Defaults to "range" when dates are missing.
 */
function modeFromFilter(f: DateFilterValue): YearMode {
  switch (f.type) {
    case "after":
      return "after";
    case "before":
      return "before";
    case "period": {
      const fromYear = parseYearFromDate(f.from);
      const toYear = parseYearFromDate(f.to);
      return fromYear && toYear && fromYear === toYear ? "equal" : "range";
    }
    default:
      return "allTime";
  }
}

const mode = ref<YearMode>(modeFromFilter(filter.value));

/** Maps the internal dropdown mode to a FilterType for the model. */
function modeToFilterType(m: YearMode): FilterType {
  switch (m) {
    case "equal":
    case "range":
      return "period";
    case "after":
      return "after";
    case "before":
      return "before";
    default:
      return "allTime";
  }
}

/** Called when the user selects a new mode from the dropdown. Resets dates. */
function onModeChange(newMode: YearMode) {
  mode.value = newMode;
  filter.value = { type: modeToFilterType(newMode) };
  key.value = crypto.randomUUID();
}

/** Writable computed for the year input in "after" and "range" modes. */
const yearAfter = computed<string | undefined>({
  get: () => parseYearFromDate(filter.value.from),
  set: (value) => {
    if (!value) {
      filter.value = {
        type: filter.value.type,
        from: undefined,
        to: filter.value.to,
      };
      return;
    }
    filter.value = {
      type: filter.value.type,
      from: formatYearStart(value),
      to: filter.value.to,
    };
  },
});

/** Writable computed for the year input in "before" and "range" modes. */
const yearBefore = computed<string | undefined>({
  get: () => parseYearFromDate(filter.value.to),
  set: (value) => {
    if (!value) {
      filter.value = {
        type: filter.value.type,
        from: filter.value.from,
        to: undefined,
      };
      return;
    }
    filter.value = {
      type: filter.value.type,
      from: filter.value.from,
      to: formatYearEnd(value),
    };
  },
});

/** Writable computed for the year input in "equal" mode. Sets both dates. */
const yearEqual = computed<string | undefined>({
  get: () => {
    const yAfter = parseYearFromDate(filter.value.from);
    const yBefore = parseYearFromDate(filter.value.to);
    if (yAfter && yBefore && yAfter === yBefore) return yAfter;
    return undefined;
  },
  set: (value) => {
    if (!value) {
      filter.value = { type: "period" };
      return;
    }
    filter.value = {
      type: "period",
      from: formatYearStart(value),
      to: formatYearEnd(value),
    };
  },
});

/** Which year inputs to show based on the current mode. */
const show = computed(() => ({
  equal: mode.value === "equal",
  after: mode.value === "after" || mode.value === "range",
  before: mode.value === "before" || mode.value === "range",
}));

/** Whether multiple year inputs are visible (range mode). */
const hasMultipleInputs = computed(() => show.value.after && show.value.before);
</script>

<template>
  <div class="flex flex-col gap-16">
    <span class="flex flex-col gap-8">
      <label :for="yearModeSelectId" class="ris-label2-regular">Zeitraum</label>
      <PrimevueSelect
        :id="yearModeSelectId"
        :model-value="mode"
        :options="items"
        :placeholder="items[0]?.label"
        :pt="{ overlay: { class: 'bg-white w-full' } }"
        append-to="self"
        option-label="label"
        option-value="value"
        scroll-height="20rem"
        @update:model-value="onModeChange"
      />
    </span>

    <div v-if="show.equal" class="flex flex-col gap-8">
      <label class="ris-label2-regular" :for="yearEqualId">Jahr</label>
      <YearInput :id="yearEqualId" :key v-model="yearEqual" />
    </div>

    <div v-if="show.after" class="flex flex-col gap-8">
      <label class="ris-label2-regular" :for="yearAfterId">
        <template v-if="hasMultipleInputs">Ab dem Jahr</template>
        <template v-else>Jahr</template>
      </label>
      <YearInput :id="yearAfterId" :key v-model="yearAfter" />
    </div>

    <div v-if="show.before" class="flex flex-col gap-8">
      <label class="ris-label2-regular" :for="yearBeforeId">
        <template v-if="hasMultipleInputs">Bis zum Jahr</template>
        <template v-else>Jahr</template>
      </label>
      <YearInput :id="yearBeforeId" :key v-model="yearBefore" />
    </div>
  </div>
</template>
