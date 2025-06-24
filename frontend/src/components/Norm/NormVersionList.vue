<script setup lang="ts">
import IncompleteDataMessage from "~/components/IncompleteDataMessage.vue";
import type { LegislationWork, SearchResult } from "~/types";
import Column from "primevue/column";
import DataTable from "primevue/datatable";
import Badge, { BadgeColor } from "@/components/Badge.vue";
import type { VersionStatus } from "~/composables/useNormVersions";
import { getVersionStatus } from "~/composables/useNormVersions";
import IcBaselineLaunch from "~icons/ic/baseline-launch";
import _ from "lodash";

const props = defineProps<{
  status: string;
  currentLegislationIdentifier: string;
  versions: SearchResult<LegislationWork>[];
}>();

interface TableRowData {
  id: number;
  fromDate: string;
  toDate: string;
  status?: Status;
  link: string;
  selectable: boolean;
}

interface Status {
  label: string;
  color: BadgeColor;
}

const selectedVersion = ref<TableRowData>();

const tableRowData = computed<TableRowData[]>(() => {
  const versionsSorted = _.orderBy(
    props.versions,
    [(version) => version.item.workExample.temporalCoverage],
    ["desc"],
  );

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

function translateStatus(status: VersionStatus): Status | undefined {
  switch (status) {
    case "inForce":
      return { label: "Aktuell gültig", color: BadgeColor.GREEN };
    case "future":
      return { label: "Zukünftig in Kraft", color: BadgeColor.YELLOW };
    case "historical":
      return { label: "Außer Kraft", color: BadgeColor.RED };
    default:
      return undefined;
  }
}

const rowClass = (row: TableRowData) => {
  return row.selectable
    ? "group cursor-pointer"
    : "cursor-not-allowed pointer-event-none bg-blue-100 text-gray-900";
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
          <div class="flex justify-between">
            <Badge
              v-if="slotProps.data.status"
              :label="slotProps.data.status.label"
              :color="slotProps.data.status.color"
            ></Badge>
            <IcBaselineLaunch
              class="invisible text-gray-900 group-hover:visible"
            />
          </div>
        </template>
      </Column>
    </DataTable>
  </section>
</template>
