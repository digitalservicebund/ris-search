<script setup lang="ts">
import { RadioButton } from "primevue";
import RisDateInput from "~/components/Ris/RisDateInput.vue";
import RisYearInput from "~/components/Ris/RisYearInput.vue";
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
    default:
      return "Datum";
  }
});

const visibleFilters = computed(() => {
  return {
    currentlyInForce: documentKind === DocumentKind.Norm,
    allTime: true,
    specificDate: [DocumentKind.Norm, DocumentKind.CaseLaw].includes(
      documentKind,
    ),
    period: true,
  };
});

/** Whether the document kind uses year-only date values (as opposed to full dates). */
function usesYearOnly(kind: DocumentKind): boolean {
  return kind === DocumentKind.Literature;
}

/** Extracts the year from a full date string (YYYY-MM-DD). */
function dateToYear(date: string | undefined): string | undefined {
  if (!date) return undefined;
  const match = date.match(/^(\d{4})-\d{2}-\d{2}$/);
  return match ? match[1] : undefined;
}

/** Converts a year to a full date string for 'from' values (Jan 1st). */
function yearToDateFrom(year: string | undefined): string | undefined {
  if (!year) return undefined;
  const match = year.match(/^\d{4}$/);
  return match ? `${year}-01-01` : undefined;
}

/** Converts a year to a full date string for 'to' values (Dec 31st). */
function yearToDateTo(year: string | undefined): string | undefined {
  if (!year) return undefined;
  const match = year.match(/^\d{4}$/);
  return match ? `${year}-12-31` : undefined;
}

watch(
  () => documentKind,
  (is, was) => {
    if (is === was) return;

    if (was === DocumentKind.Norm && filter.value.type === "currentlyInForce") {
      setFilterType("allTime");
    }

    // When switching between filters that use a full date and filters that only
    // use the year, convert the values of the `from` and `to` fields of the
    // filter. If the value is a year, set it to the beginning (for the `from`
    // value) or the end (for the `to` value) of that year to ˚keep the meaning
    // of the filter intact. If the value is a full date, simply extract the year.
    const wasYearOnly = usesYearOnly(was);
    const isYearOnly = usesYearOnly(is);

    if (wasYearOnly !== isYearOnly && filter.value.type === "period") {
      if (isYearOnly) {
        // Full date → Year
        filter.value = {
          type: "period",
          from: dateToYear(filter.value.from),
          to: dateToYear(filter.value.to),
        };
      } else {
        // Year → Full date
        filter.value = {
          type: "period",
          from: yearToDateFrom(filter.value.from),
          to: yearToDateTo(filter.value.to),
        };
      }
    }
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

        <template v-if="filter.type === 'period'">
          <div
            v-if="documentKind === DocumentKind.Literature"
            class="flex flex-col pt-8 pl-40"
          >
            <label :for="periodFromYearInputId" class="ris-body2-regular">
              von
            </label>
            <RisYearInput
              :id="periodFromYearInputId"
              :model-value="filter.from"
              @update:model-value="setPeriodFrom($event)"
            />

            <label :for="periodToYearInputId" class="ris-body2-regular mt-8">
              bis
            </label>
            <RisYearInput
              :id="periodToYearInputId"
              :model-value="filter.to"
              @update:model-value="setPeriodTo($event)"
            />
          </div>

          <div v-else class="flex flex-col pt-8 pl-40">
            <label :for="periodFromDateInputId" class="ris-body2-regular">
              von
            </label>
            <RisDateInput
              :id="periodFromDateInputId"
              :model-value="filter.from"
              @update:model-value="setPeriodFrom($event)"
            />

            <label :for="periodToDateInputId" class="ris-body2-regular mt-8">
              bis
            </label>
            <RisDateInput
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
