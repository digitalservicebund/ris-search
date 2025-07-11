<script setup lang="ts">
import { toString } from "@hyperdx/lucene";
import Button from "primevue/button";
import InputText from "primevue/inputtext";
import Select from "primevue/select";
import { computed, reactive, ref } from "vue";
import {
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
  <div>
    <div
      v-for="(row, index) in rows"
      :key="index"
      style="margin-bottom: 8px"
      class="flex"
      data-testid="builder-row"
    >
      <!-- row logic operator, only AND for now -->
      <Select
        v-if="index > 0"
        v-model="row.rowLogicOp"
        style="margin-right: 10px"
        class="basis-1/5"
        data-testid="row-logic-operator"
        :options="rowLogicOperators"
        option-label="label"
        @change="() => updateLuceneQuery()"
      />

      <Select
        v-model="row.selectedField"
        style="margin-right: 10px"
        class="basis-1/4"
        data-testid="field"
        placeholder="Rubrik auswählen"
        :options="filteredFields"
        option-label="label"
        @change="() => updateLuceneQuery()"
      />
      <!-- logic operator -->
      <Select
        v-if="row.selectedField"
        v-model="row.selectedLogic"
        style="margin-right: 10px"
        class="basis-1/4"
        data-testid="operator"
        placeholder="Operator auswählen"
        :options="fieldTypeToLogicOptions(row.selectedField.type)"
        option-label="label"
        @change="() => updateLuceneQuery()"
      />
      <!-- search value -->
      <div v-if="row.selectedField && row.selectedLogic" class="grow">
        <InputText
          v-if="row.selectedLogic.inputElement === INPUT_ELEMENT.CHIPS"
          v-model="row.searchValue"
          type="text"
          placeholder="Chips input (TODO)"
          @input="() => updateLuceneQuery()"
        />
        <InputText
          v-if="row.selectedLogic.inputElement === INPUT_ELEMENT.TEXT"
          v-model="row.searchValue"
          type="text"
          placeholder="Text input"
          @input="() => updateLuceneQuery()"
        />

        <InputText
          v-if="row.selectedLogic.inputElement === INPUT_ELEMENT.DATE"
          v-model="row.searchValue"
          type="text"
          placeholder="TT.MM.YYYY"
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
