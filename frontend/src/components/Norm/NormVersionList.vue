<script setup lang="ts">
import IncompleteDataMessage from "~/components/IncompleteDataMessage.vue";
import type { LegislationWork, SearchResult } from "~/types";
import Column from "primevue/column";
import DataTable from "primevue/datatable";
import Badge, { BadgeColor } from "@/components/Badge.vue";
import type { VersionStatus } from "~/composables/useNormVersions";
import { getVersionStatus } from "~/composables/useNormVersions";

const props = defineProps<{
  status: string;
  currentLegislationIdentifier: string;
  versions: SearchResult<LegislationWork>[];
}>();

interface TableRowData {
  id: number;
  fromDate: string;
  toDate: string;
  status?: string;
  link: string;
  selectable: boolean;
}

const selectedVersion = defineModel<TableRowData>();

const tableRowData = computed<TableRowData[]>(() => {
  const versionsSorted = [...props.versions].sort((versionA, versionB) => {
    if (
      versionA.item.workExample.temporalCoverage <
      versionB.item.workExample.temporalCoverage
    )
      return 1;
    if (
      versionA.item.workExample.temporalCoverage >
      versionB.item.workExample.temporalCoverage
    )
      return -1;
    return 0;
  });

  return versionsSorted.map((version, index) => {
    const fromAndToDate = splitTemporalCoverage(
      version.item.workExample.temporalCoverage,
    );

    const id = index;
    const fromDate = fromAndToDate[0] ?? "-";
    const toDate = fromAndToDate[1] ?? "-";
    const status = translateStatus(getVersionStatus(version));
    const link = `/norms/${version.item.workExample.legislationIdentifier}`;
    const selectable =
      version.item.workExample.legislationIdentifier !==
      props.currentLegislationIdentifier;

    const rowData: TableRowData = {
      id: id,
      fromDate: fromDate,
      toDate: toDate,
      status: status,
      link: link,
      selectable: selectable,
    };

    return rowData;
  });
});

function translateStatus(status: VersionStatus): string | undefined {
  switch (status) {
    case "inForce":
      return "Aktuell gültig";
    case "future":
      return "Zukünftig in Kraft";
    case "historical":
      return "Außer Kraft";
    default:
      return undefined;
  }
}

const rowClass = (row: TableRowData) => {
  return row.selectable
    ? "cursor-pointer"
    : "pointer-event-none hover:bg-transparent";
};

async function onRowSelect() {
  if (selectedVersion.value) {
    await navigateTo(selectedVersion.value.link);
  }
}

async function handleSelectionUpdate(newSelection: TableRowData) {
  if (newSelection.selectable) {
    selectedVersion.value = newSelection;
  } else {
    selectedVersion.value = undefined;
  }
}
</script>

<template>
  <section aria-labelledby="fassungenTabPanelTitle">
    <h2 id="fassungenTabPanelTitle" class="ris-heading3-bold my-24">
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
      <Column field="fromDate" header="Gültig von" header-class="w-px"></Column>
      <Column field="toDate" header="Gültig bis" header-class="w-px"></Column>
      <Column header="Status">
        <template #body="slotProps">
          <Badge
            v-if="slotProps.data.status"
            :label="slotProps.data.status"
            :color="BadgeColor.BLUE"
          ></Badge>
        </template>
      </Column>
    </DataTable>
  </section>
</template>
