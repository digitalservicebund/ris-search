<script setup lang="ts">
import { RadioButton } from "primevue";
import RisDateInput from "../Ris/RisDateInput.vue";
import type { DateFilterValue, FilterType } from "./filterType";
import { DocumentKind } from "~/types";

const { documentKind } = defineProps<{
  /* Document kind for which the filter should be used */
  documentKind: DocumentKind;
}>();

/** The currently active filter */
const filter = defineModel<DateFilterValue>({ default: { type: "allTime" } });

const [
  formId,
  currentlyInForceId,
  allTimeId,
  specificDateId,
  specificDateInputId,
  periodId,
  periodFromInputId,
  periodToInputId,
] = new Array(8).fill("").map(() => useId());

const filterLabel = computed(() => {
  switch (documentKind) {
    case DocumentKind.CaseLaw:
      return "Entscheidungsdatum";
    case DocumentKind.Norm:
      return "Gültigkeit";
    default:
      return "Datum";
  }
});

const visibleFilters = computed(() => {
  return {
    currentlyInForce: documentKind === DocumentKind.Norm,
    allTime: true,
    specificDate: true,
    period: true,
  };
});

watch(
  () => documentKind,
  (is, was) => {
    if (is === was) return;
    // Set a reasonable default filter when the document type is changed
    setFilterType(is === DocumentKind.Norm ? "currentlyInForce" : "allTime");
  },
);

function setFilterType(type: FilterType) {
  filter.value = { type };
}

function setSpecificDate(value: string | undefined) {
  filter.value = { type: "specificDate", from: value };
}

function setPeriodFrom(value: string | undefined) {
  filter.value = { type: "period", from: value, to: filter.value.to };
}

function setPeriodTo(value: string | undefined) {
  filter.value = { type: "period", from: filter.value.from, to: value };
  nextTick().then(() => console.log("setPeriodTo", filter.value, value));
}
</script>

<template>
  <form :aria-labelledby="formId" class="flex flex-col gap-24">
    <label :id="formId" class="ris-label1-bold">
      Filter nach {{ filterLabel }}
    </label>

    <div v-if="visibleFilters.currentlyInForce" class="flex items-center">
      <RadioButton
        :model-value="filter.type"
        :input-id="currentlyInForceId"
        name="filter"
        value="currentlyInForce"
        @update:model-value="setFilterType"
      />
      <label :for="currentlyInForceId" class="ris-label1-regular">
        Aktuell gültig
      </label>
    </div>

    <div v-if="visibleFilters.allTime" class="flex items-center">
      <RadioButton
        :model-value="filter.type"
        :input-id="allTimeId"
        name="filter"
        value="allTime"
        @update:model-value="setFilterType"
      />
      <label :for="allTimeId" class="ris-label1-regular">
        Keine zeitliche Begrenzung
      </label>
    </div>

    <template v-if="visibleFilters.specificDate">
      <fieldset>
        <div class="flex items-center">
          <RadioButton
            :model-value="filter.type"
            :input-id="specificDateId"
            name="filter"
            value="specificDate"
            @update:model-value="setFilterType"
          />
          <label :for="specificDateId" class="ris-label1-regular">
            Bestimmtes Datum
          </label>
        </div>

        <div
          v-if="filter.type === 'specificDate'"
          class="flex flex-col pt-8 pl-40"
        >
          <label :for="specificDateInputId" class="sr-only">Datum</label>
          <RisDateInput
            :id="specificDateInputId"
            :model-value="filter.from"
            @update:model-value="setSpecificDate($event)"
          />
        </div>
      </fieldset>
    </template>

    <template v-if="visibleFilters.period">
      <fieldset>
        <div class="flex items-center">
          <RadioButton
            :model-value="filter.type"
            :input-id="periodId"
            name="filter"
            value="period"
            @update:model-value="setFilterType"
          />
          <label :for="periodId" class="ris-label1-regular">
            Innerhalb einer Zeitspanne
          </label>
        </div>

        <div v-if="filter.type === 'period'" class="flex flex-col pt-8 pl-40">
          <label :for="periodFromInputId" class="ris-body2-regular">
            von
          </label>
          <RisDateInput
            :id="periodFromInputId"
            :model-value="filter.from"
            @update:model-value="setPeriodFrom($event)"
          />

          <label :for="periodToInputId" class="ris-body2-regular mt-8">
            bis
          </label>
          <RisDateInput
            :id="periodToInputId"
            :model-value="filter.to"
            @update:model-value="setPeriodTo($event)"
          />
        </div>
      </fieldset>
    </template>
  </form>
</template>
