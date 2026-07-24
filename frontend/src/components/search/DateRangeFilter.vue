<script setup lang="ts">
import { Select } from "primevue";
import type {
  DateFilterValue,
  FilterType,
} from "~/utils/search/dateFilterType";

const { appendTo = "self" } = defineProps<{
  /**
   * Where to render the dropdown overlay. "self" (the default) keeps it
   * width-matched to the field, but gets clipped by any scrollable ancestor
   * (e.g. a Drawer). Use "body" in such contexts instead.
   */
  appendTo?: "self" | "body";
}>();

/** Active date filter value */
const filter = defineModel<DateFilterValue>({ required: true });

const dateModeLabelId = useId();
const fromDateId = useId();
const toDateId = useId();

const items: { label: string; value: FilterType }[] = [
  { label: "Keine zeitliche Begrenzung", value: "allTime" },
  { label: "Bis zu einem Datum", value: "before" },
  { label: "An einem Datum", value: "specificDate" },
  { label: "Ab einem Datum", value: "after" },
  { label: "In einem Zeitraum", value: "period" },
];

// The key will be set to a random value when the filter type changes to force
// Vue to fully re-render the date input. This is a workaround to fix a
// rendering bug where the date input of the "from" date is sometimes reused for
// the "to" date after switching filter types, resulting in a broken in-between
// state of the input mask.
const key = ref<string>();

const selectedType = computed({
  get: () => filter.value.type,
  set: (type: FilterType) => {
    filter.value = { type };
    key.value = crypto.randomUUID();
  },
});

const showFromField = computed(() =>
  ["specificDate", "after", "period"].includes(filter.value.type),
);

const showToField = computed(() =>
  ["before", "period"].includes(filter.value.type),
);

const hasMultipleInputs = computed(
  () => showFromField.value && showToField.value,
);

const fromDate = computed({
  get: () => filter.value.from,
  set: (value) => {
    filter.value = {
      type: filter.value.type,
      from: value,
      to: filter.value.to,
    };
  },
});

const toDate = computed({
  get: () => filter.value.to,
  set: (value) => {
    filter.value = {
      type: filter.value.type,
      from: filter.value.from,
      to: value,
    };
  },
});
</script>

<template>
  <div class="flex flex-col gap-16">
    <span class="flex flex-col gap-8">
      <label :id="dateModeLabelId" class="typo-label2-regular">Zeitraum</label>
      <Select
        v-model="selectedType"
        :aria-labelledby="dateModeLabelId"
        :append-to="appendTo"
        :options="items"
        :placeholder="items[0]?.label"
        :pt="{
          overlay: {
            class: appendTo === 'self' ? 'bg-white w-full' : 'bg-white',
          },
        }"
        option-label="label"
        option-value="value"
        scroll-height="20rem"
      />
    </span>

    <div v-if="showFromField" class="flex flex-col gap-8">
      <label :for="fromDateId" class="typo-label2-regular">
        <template v-if="hasMultipleInputs">Ab dem Datum</template>
        <template v-else>Datum</template>
      </label>
      <DateInput :id="fromDateId" :key v-model="fromDate" />
    </div>

    <div v-if="showToField" class="flex flex-col gap-8">
      <label :for="toDateId" class="typo-label2-regular">
        <template v-if="hasMultipleInputs">Bis zum Datum</template>
        <template v-else>Datum</template>
      </label>
      <DateInput :id="toDateId" :key v-model="toDate" />
    </div>
  </div>
</template>
