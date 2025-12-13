<script setup lang="ts">
import PrimevueSelect from "primevue/select";
import type { ValidationError } from "~/components/DateInput.vue";
import type { DropdownItem } from "~/components/types";
import { DateSearchMode } from "~/composables/useSimpleSearchParams/useSimpleSearchParams";

const date = defineModel<string | undefined>("date");
const dateAfter = defineModel<string | undefined>("dateAfter");
const dateBefore = defineModel<string | undefined>("dateBefore");
const dateSearchMode = defineModel<DateSearchMode>("dateSearchMode", {
  required: true,
});

const dateId = useId();
const dateAfterId = useId();
const dateBeforeId = useId();

const dateValidationError = ref<ValidationError | undefined>();
const dateAfterValidationError = ref<ValidationError | undefined>();
const dateBeforeValidationError = ref<ValidationError | undefined>();

const items: DropdownItem[] = [
  { label: "Keine zeitliche Begrenzung", value: DateSearchMode.None },
  { label: "Bis zu einem Datum", value: DateSearchMode.Before },
  { label: "An einem Datum", value: DateSearchMode.Equal },
  { label: "Ab einem Datum", value: DateSearchMode.After },
  { label: "In einem Zeitraum", value: DateSearchMode.Range },
];

const showDateField = computed(
  () => dateSearchMode.value === DateSearchMode.Equal,
);

const showDateAfterField = computed(
  () =>
    dateSearchMode.value === DateSearchMode.After ||
    dateSearchMode.value === DateSearchMode.Range,
);

const showDateBeforeField = computed(
  () =>
    dateSearchMode.value === DateSearchMode.Before ||
    dateSearchMode.value === DateSearchMode.Range,
);

const hasMultipleInputs = computed(
  () => showDateAfterField.value && showDateBeforeField.value,
);
</script>

<template>
  <div class="flex flex-col gap-16">
    <span class="flex flex-col gap-8">
      <label for="date-mode-select" class="ris-label2-regular">Zeitraum</label>
      <PrimevueSelect
        id="date-mode-select"
        v-model="dateSearchMode"
        :options="items"
        option-label="label"
        option-value="value"
        :placeholder="items[0]?.label"
        scroll-height="20rem"
        append-to="self"
        :pt="{ overlay: { class: 'bg-white w-full' } }"
      />
    </span>

    <div v-if="showDateField" class="flex flex-col gap-8">
      <label :for="dateId" class="ris-label2-regular">Datum</label>
      <DateInput
        :id="dateId"
        v-model="date"
        v-model:validation-error="dateValidationError"
      />
    </div>

    <div v-if="showDateAfterField" class="flex flex-col gap-8">
      <label :for="dateAfterId" class="ris-label2-regular">
        <template v-if="hasMultipleInputs">Ab dem Datum</template>
        <template v-else>Datum</template>
      </label>
      <DateInput
        :id="dateAfterId"
        v-model="dateAfter"
        v-model:validation-error="dateAfterValidationError"
      />
    </div>

    <div v-if="showDateBeforeField" class="flex flex-col gap-8">
      <label :for="dateBeforeId" class="ris-label2-regular">
        <template v-if="hasMultipleInputs">Bis zum Datum</template>
        <template v-else>Datum</template>
      </label>
      <DateInput
        :id="dateBeforeId"
        v-model="dateBefore"
        v-model:validation-error="dateBeforeValidationError"
      />
    </div>
  </div>
</template>
