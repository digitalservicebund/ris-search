<script setup lang="ts">
import { computed, reactive, ref } from "vue";
import { toString } from "@hyperdx/lucene";
import Button from "primevue/button";
import {
  FIELD_TYPE,
  fields,
  fieldTypeToLogicOptions,
  INPUT_ELEMENT,
  type QueryBuilderRow,
  rowLogicOperators,
} from "./data";
import { type AstNode, buildTerm } from "~/components/Search/Query/logic";

const userInputDisabled = ref(true);
onNuxtReady(() => {
  userInputDisabled.value = false;
});

const emit = defineEmits<{
  "update:luceneQuery": [string | undefined];
}>();

const props = defineProps<{
  currentDocumentKind: string;
}>();

const filteredFields = computed(() =>
  fields.filter((field) => {
    return field.documentKind === props.currentDocumentKind;
  }),
);

const luceneQuery = ref("");
const rows: QueryBuilderRow[] = reactive([]);

function handleAddRow() {
  rows.push({
    rowLogicOp: rowLogicOperators[0],
    selectedField: "",
    selectedLogic: "",
    searchValue: "",
  });
}

function handleResetRows() {
  rows.length = 0;
  updateLuceneQuery();
}

watch(
  () => props.currentDocumentKind,
  (newValue, oldValue) => {
    if (newValue !== oldValue) {
      rows.length = 0;
      updateLuceneQuery();
    }
  },
);

function handleDeleteRow(index: number) {
  rows.splice(index, 1);
  updateLuceneQuery();
}

function updateLuceneQuery() {
  let constructedQueryObject: AstNode | null = null;

  rows.forEach((row) => {
    if (!constructedQueryObject) {
      constructedQueryObject = { left: buildTerm(row) };
    } else {
      // the tree might have to be constructed differently
      constructedQueryObject = {
        left: constructedQueryObject,
        operator: row.rowLogicOp.value,
        right: buildTerm(row),
      };
    }
  });

  if (rows.length === 0 || !constructedQueryObject) {
    luceneQuery.value = "";
    emit("update:luceneQuery", luceneQuery.value);
    return;
  }

  luceneQuery.value = toString(constructedQueryObject);
  console.log("Lucene query:", luceneQuery.value);
  emit("update:luceneQuery", luceneQuery.value);
}
</script>

<template>
  <div class="container">
    <div
      v-for="(row, index) in rows"
      :key="index"
      style="margin-bottom: 8px"
      class="flex"
      data-testid="builder-row"
    >
      <!-- row logic operator, only AND for now -->
      <select
        v-if="index > 0"
        v-model="row.rowLogicOp"
        style="margin-right: 10px"
        class="ds-select ds-select-small basis-1/5"
        data-testid="row-logic-operator"
        @change="() => updateLuceneQuery()"
      >
        <option
          v-for="rowLogicOp in rowLogicOperators"
          :key="rowLogicOp.value"
          :value="rowLogicOp"
        >
          {{ rowLogicOp.label }}
        </option>
      </select>
      <!-- field -->

      <select
        v-model="row.selectedField"
        style="margin-right: 10px"
        class="ds-select ds-select-small basis-1/4"
        data-testid="field"
        @change="() => updateLuceneQuery()"
      >
        <option disabled value="">Rubrik auswählen</option>
        <option
          v-for="field in filteredFields"
          :key="field.value"
          :value="field"
          :disabled="
            !(field.type === FIELD_TYPE.TEXT || field.type === FIELD_TYPE.DATE)
          "
        >
          {{ field.label }}
        </option>
      </select>
      <!-- logic operator -->
      <select
        v-if="row.selectedField"
        v-model="row.selectedLogic"
        style="margin-right: 10px"
        class="ds-select ds-select-small basis-1/4"
        data-testid="operator"
        @change="() => updateLuceneQuery()"
      >
        <option disabled value="">Operator auswählen</option>
        <option
          v-for="option in fieldTypeToLogicOptions(row.selectedField.type)"
          :key="option.value"
          :value="option"
          :disabled="
            !(
              option.value.includes('ExactPhrase') ||
              option.inputElement === INPUT_ELEMENT.DATE
            )
          "
        >
          {{ option.label }}
        </option>
      </select>
      <!-- search value -->
      <div v-if="row.selectedField && row.selectedLogic" class="grow">
        <input
          v-if="row.selectedLogic.inputElement === INPUT_ELEMENT.CHIPS"
          v-model="row.searchValue"
          type="text"
          placeholder="Chips input (TODO)"
          @input="() => updateLuceneQuery()"
        />
        <input
          v-if="row.selectedLogic.inputElement === INPUT_ELEMENT.TEXT"
          v-model="row.searchValue"
          type="text"
          placeholder="Text input"
          class="ds-input ds-input-small"
          @input="() => updateLuceneQuery()"
        />

        <input
          v-if="row.selectedLogic.inputElement === INPUT_ELEMENT.DATE"
          v-model="row.searchValue"
          type="text"
          placeholder="TT.MM.YYYY"
          class="ds-input ds-input-small"
          @input="() => updateLuceneQuery()"
        />
      </div>
      <Button text label="Löschen" @click="handleDeleteRow(index)" />
    </div>
    <Button
      aria-label="Suchparameter hinzufügen"
      label="Suchparameter hinzufügen"
      :disabled="userInputDisabled"
      @click="handleAddRow"
    />
    &nbsp;
    <Button
      v-if="rows.length > 0"
      aria-label="Zurücksetzen"
      label="Zurücksetzen"
      :disabled="userInputDisabled"
      @click="handleResetRows"
    />
  </div>
</template>
