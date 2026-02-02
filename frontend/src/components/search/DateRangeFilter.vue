<script setup lang="ts">
import PrimevueSelect from "primevue/select";
import type { ValidationError } from "~/components/DateInput.vue";
import type { DateFilterValue, FilterType } from "~/utils/search/filterType";

/** Active date filter value */
const filter = defineModel<DateFilterValue>({ required: true });

const dateModeSelectId = useId();
const fromDateId = useId();
const toDateId = useId();

const fromValidationError = ref<ValidationError | undefined>();
const toValidationError = ref<ValidationError | undefined>();

const items: { label: string; value: FilterType }[] = [
  { label: "Keine zeitliche Begrenzung", value: "allTime" },
  { label: "Bis zu einem Datum", value: "before" },
  { label: "An einem Datum", value: "specificDate" },
  { label: "Ab einem Datum", value: "after" },
  { label: "In einem Zeitraum", value: "period" },
];

const selectedType = computed({
  get: () => filter.value.type,
  set: (type: FilterType) => {
    filter.value = { type };
  },
});

const showFromField = computed(() =>
  (["specificDate", "after", "period"] as FilterType[]).includes(
    filter.value.type,
  ),
);

const showToField = computed(() =>
  (["before", "period"] as FilterType[]).includes(filter.value.type),
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
      <label :for="dateModeSelectId" class="ris-label2-regular">Zeitraum</label>
      <PrimevueSelect
        :id="dateModeSelectId"
        v-model="selectedType"
        :options="items"
        option-label="label"
        option-value="value"
        :placeholder="items[0]?.label"
        scroll-height="20rem"
        append-to="self"
        :pt="{ overlay: { class: 'bg-white w-full' } }"
      />
    </span>

    <div v-if="showFromField" class="flex flex-col gap-8">
      <label :for="fromDateId" class="ris-label2-regular">
        <template v-if="hasMultipleInputs">Ab dem Datum</template>
        <template v-else>Datum</template>
      </label>
      <DateInput
        :id="fromDateId"
        v-model="fromDate"
        v-model:validation-error="fromValidationError"
      />
    </div>

    <div v-if="showToField" class="flex flex-col gap-8">
      <label :for="toDateId" class="ris-label2-regular">
        <template v-if="hasMultipleInputs">Bis zum Datum</template>
        <template v-else>Datum</template>
      </label>
      <DateInput
        :id="toDateId"
        v-model="toDate"
        v-model:validation-error="toValidationError"
      />
    </div>
  </div>
</template>
