<script setup lang="ts">
import { RadioButton } from "primevue";
import DateInput from "~/components/DateInput.vue";
import YearInput from "~/components/YearInput.vue";
import { DocumentKind } from "~/types";
import type { DateFilterValue, FilterType } from "~/utils/search/filterType";

const { documentKind } = defineProps<{
  /* Document kind for which the filter should be used */
  documentKind: DocumentKind;
}>();

/** The currently active filter */
const filter = defineModel<DateFilterValue>({ default: { type: "allTime" } });

const formId = useId();
const currentlyInForceId = useId();
const allTimeId = useId();
const specificDateId = useId();
const specificDateInputId = useId();
const periodId = useId();
const periodFromDateInputId = useId();
const periodToDateInputId = useId();
const periodFromYearInputId = useId();
const periodToYearInputId = useId();

const filterLabel = computed(() => {
  switch (documentKind) {
    case DocumentKind.CaseLaw:
      return "Entscheidungsdatum";
    case DocumentKind.Norm:
      return "Gültigkeit";
    case DocumentKind.Literature:
      return "Veröffentlichungsjahr";
    case DocumentKind.AdministrativeDirective:
      return "Inkrafttreten";
    default:
      return "Datum";
  }
});

const visibleFilters = computed(() => {
  return {
    currentlyInForce: documentKind === DocumentKind.Norm,
    allTime: true,
    specificDate: [
      DocumentKind.Norm,
      DocumentKind.CaseLaw,
      DocumentKind.AdministrativeDirective,
    ].includes(documentKind),
    period: true,
  };
});

watch(
  () => documentKind,
  (is, was) => {
    if (is === was) return;
    filter.value = {
      type: is === DocumentKind.Norm ? "currentlyInForce" : "allTime",
    };
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
}
</script>

<template>
  <form :aria-labelledby="formId" class="flex flex-col gap-24">
    <p :id="formId" class="ris-label1-bold">Filter nach {{ filterLabel }}</p>

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
          <DateInput
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

        <template v-if="filter.type === 'period'">
          <div
            v-if="documentKind === DocumentKind.Literature"
            class="flex flex-col gap-8 pt-8 pl-40"
          >
            <label :for="periodFromYearInputId" class="ris-label2-regular">
              von
            </label>
            <YearInput
              :id="periodFromYearInputId"
              :model-value="filter.from"
              @update:model-value="setPeriodFrom($event)"
            />

            <label :for="periodToYearInputId" class="ris-label2-regular mt-8">
              bis
            </label>
            <YearInput
              :id="periodToYearInputId"
              :model-value="filter.to"
              @update:model-value="setPeriodTo($event)"
            />
          </div>

          <div v-else class="flex flex-col gap-8 pt-8 pl-40">
            <label :for="periodFromDateInputId" class="ris-label2-regular">
              von
            </label>
            <DateInput
              :id="periodFromDateInputId"
              :model-value="filter.from"
              @update:model-value="setPeriodFrom($event)"
            />

            <label :for="periodToDateInputId" class="ris-label2-regular mt-8">
              bis
            </label>
            <DateInput
              :id="periodToDateInputId"
              :model-value="filter.to"
              @update:model-value="setPeriodTo($event)"
            />
          </div>
        </template>
      </fieldset>
    </template>
  </form>
</template>
