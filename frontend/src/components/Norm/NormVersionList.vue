<script setup lang="ts">
import _ from "lodash";
import Column from "primevue/column";
import DataTable from "primevue/datatable";
import Badge, { BadgeColor } from "~/components/Badge.vue";
import IncompleteDataMessage from "~/components/IncompleteDataMessage.vue";
import type { LegislationWork, SearchResult } from "~/types";
import { dateFormattedDDMMYYYY } from "~/utils/dateFormatting";
import {
  getValidityStatus,
  getValidityStatusLabel,
  temporalCoverageToValidityInterval,
  type ValidityStatus,
} from "~/utils/normUtils";
import IcBaselineLaunch from "~icons/ic/baseline-launch";

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
    const validityInterval = temporalCoverageToValidityInterval(
      version.item.workExample.temporalCoverage,
    );

    const id = index;
    const validityStatus = getValidityStatus(validityInterval);
    const status: Status = {
      label: getValidityStatusLabel(validityStatus) ?? "Unbekannt",
      color: getStatusColor(validityStatus),
    };
    const link = `/norms/${version.item.workExample.legislationIdentifier}`;
    const selectable =
      version.item.workExample.legislationIdentifier !==
      props.currentLegislationIdentifier;

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

function getStatusColor(validityStatus?: ValidityStatus): BadgeColor {
  switch (validityStatus) {
    case "InForce":
      return BadgeColor.GREEN;
    case "FutureInForce":
      return BadgeColor.YELLOW;
    case "Expired":
      return BadgeColor.RED;
    default:
      return BadgeColor.BLUE;
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
      <Column
        field="fromDate"
        header="Gültig von"
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
            <IcBaselineLaunch
              class="invisible text-gray-900 group-hover:visible"
            />
          </div>
        </template>
      </Column>
    </DataTable>
  </section>
</template>
