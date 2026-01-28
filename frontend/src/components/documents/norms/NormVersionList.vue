<script setup lang="ts">
import _ from "lodash";
import Column from "primevue/column";
import DataTable from "primevue/datatable";
import Badge from "~/components/Badge.vue";
import IncompleteDataMessage from "~/components/documents/IncompleteDataMessage.vue";
import type { LegislationExpression } from "~/types";
import { dateFormattedDDMMYYYY } from "~/utils/dateFormatting";
import { formatNormValidity } from "~/utils/displayValues";
import { temporalCoverageToValidityInterval } from "~/utils/norm";

const props = defineProps<{
  status: string;
  currentLegislationIdentifier: string;
  versions: LegislationExpression[];
}>();

interface TableRowData {
  id: number;
  fromDate: string;
  toDate: string;
  status?: ReturnType<typeof formatNormValidity>;
  link: string;
  selectable: boolean;
}

const selectedVersion = ref<TableRowData>();

const tableRowData = computed<TableRowData[]>(() => {
  const versionsSorted = _.orderBy(
    props.versions,
    [(version) => version.temporalCoverage],
    ["desc"],
  );

  return versionsSorted.map((version, index) => {
    const validityInterval = temporalCoverageToValidityInterval(
      version.temporalCoverage,
    );

    const status = formatNormValidity(version.temporalCoverage);

    const id = index;

    const link = `/norms/${version.legislationIdentifier}`;

    const selectable =
      version.legislationIdentifier !== props.currentLegislationIdentifier;

    const rowData: TableRowData = {
      id: id,
      fromDate: dateFormattedDDMMYYYY(validityInterval?.from) ?? "-",
      toDate: dateFormattedDDMMYYYY(validityInterval?.to) ?? "-",
      status: status,
      link: link,
      selectable: selectable,
    };

    return rowData;
  });
});

const rowClass = (row: TableRowData) => {
  return row.selectable
    ? "group cursor-pointer"
    : "cursor-not-allowed pointer-event-none bg-blue-100 text-gray-1000";
};

async function onRowSelect() {
  if (selectedVersion.value) {
    await navigateTo(selectedVersion.value.link);
  }
}

async function handleSelectionUpdate(newSelection: TableRowData) {
  // Necessary so the unselectable row is never highlighted as selected
  if (newSelection.selectable) {
    selectedVersion.value = newSelection;
  } else {
    selectedVersion.value = undefined;
  }
}

const fassungenTabPanelTitleId = useId();
</script>

<template>
  <section :aria-labelledby="fassungenTabPanelTitleId">
    <h2 :id="fassungenTabPanelTitleId" class="ris-heading3-bold my-24">
      Fassungen
    </h2>
    <IncompleteDataMessage class="my-24" />
    <DataTable
      v-model:selection="selectedVersion"
      selection-mode="single"
      data-key="id"
      :value="tableRowData"
      :loading="status === 'pending'"
      :row-class="rowClass"
      @row-select="onRowSelect"
      @update:selection="handleSelectionUpdate"
    >
      <Column
        field="fromDate"
        header="Gültig ab"
        header-class="whitespace-nowrap w-1"
      ></Column>
      <Column
        field="toDate"
        header="Gültig bis"
        header-class="whitespace-nowrap w-1"
      ></Column>
      <Column header="Status">
        <template #body="slotProps">
          <div class="flex justify-between">
            <Badge
              v-if="slotProps.data.status"
              :label="slotProps.data.status.label"
              :color="slotProps.data.status.color"
            ></Badge>
          </div>
        </template>
      </Column>
    </DataTable>
  </section>
</template>
