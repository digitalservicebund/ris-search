<script setup lang="ts">
import { orderBy } from "lodash-es";
import { Column, DataTable } from "primevue";
import type { RouteLocationRaw } from "#vue-router";
import type { LegislationExpression } from "~/types/api";

const props = defineProps<{
  status: string;
  currentLegislationIdentifier: string;
  versions: LegislationExpression[];
}>();

type TableRowData = {
  id: number;
  fromDate: string;
  toDate: string;
  status?: ReturnType<typeof formatNormValidity>;
  link: RouteLocationRaw;
  selectable: boolean;
};

const route = useRoute();

const selectedVersion = ref<TableRowData>();

const tableRowData = computed<TableRowData[]>(() => {
  const versionsSorted = orderBy(
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

    const link: RouteLocationRaw = {
      path: `/norms/${version.legislationIdentifier}`,
      query: { from: route.query.from },
    };

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
  if (selectedVersion.value) await navigateTo(selectedVersion.value.link);
}

async function handleSelectionUpdate(newSelection: TableRowData) {
  // Necessary so the unselectable row is never highlighted as selected
  if (newSelection.selectable) selectedVersion.value = newSelection;
  else selectedVersion.value = undefined;
}
</script>

<template>
  <DataTable
    v-model:selection="selectedVersion"
    selection-mode="single"
    data-key="id"
    :value="tableRowData"
    :loading="status === 'pending'"
    :row-class="rowClass"
    @row-select="onRowSelect"
    @update:selection="handleSelectionUpdate"
    :pt="{ emptyMessageCell: { class: 'ps-16 py-12 text-left text-gray-900' } }"
  >
    <template #empty>Keine Ergebnisse gefunden</template>
    <Column
      field="fromDate"
      header="Gültig ab"
      header-class="whitespace-nowrap w-1"
    />
    <Column
      field="toDate"
      header="Gültig bis"
      header-class="whitespace-nowrap w-1"
    />
    <Column header="Status">
      <template #body="{ data: { status } }">
        <div class="flex justify-between">
          <Badge v-if="status" :label="status.label" :color="status.color" />
        </div>
      </template>
    </Column>
  </DataTable>
</template>
