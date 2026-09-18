<script setup lang="ts">
import { NuxtLink } from "#components";
import type { BadgeColor } from "~/components/ui/Badge.vue";
import type {
  DataTableColumn,
  DataTableRow,
} from "~/components/ui/DataTable.vue";
import type { LegislationExpression } from "~/types/api";

const props = defineProps<{
  currentLegislationIdentifier: string;
  versions: LegislationExpression[];
}>();

type VersionRow = DataTableRow & {
  fromDate: string;
  toDate: string;
  status: { label: string; color: BadgeColor };
};

const route = useRoute();

const columns: DataTableColumn<VersionRow>[] = [
  { key: "fromDate", label: "Gültig ab" },
  { key: "toDate", label: "Gültig bis" },
  { key: "status", label: "Status" },
];

const rows = computed<VersionRow[]>(() => {
  // Newest Fassung first
  const versionsSorted = props.versions.toSorted((a, b) =>
    b.temporalCoverage.localeCompare(a.temporalCoverage),
  );

  return versionsSorted.map((version) => {
    const validityInterval = temporalCoverageToValidityInterval(
      version.temporalCoverage,
    );

    const current =
      version.legislationIdentifier === props.currentLegislationIdentifier;

    const to = {
      path: `/gesetze/${version.legislationIdentifier}`,
      query: { from: route.query.from },
    };

    const status = formatNormValidity(version.temporalCoverage) ?? {
      label: "Unbekannt",
      color: "blue",
    };

    return {
      key: version.legislationIdentifier ?? "",
      attrs: { to },
      current,
      fromDate: dateFormattedDDMMYYYY(validityInterval?.from) ?? "–",
      toDate: dateFormattedDDMMYYYY(validityInterval?.to) ?? "–",
      status,
    };
  });
});

// Filtering swaps the rows out in place, which assistive technology does not
// announce. Report the new count instead. Opening the Fassungen tab mounts this
// list, so the watcher skips the initial value and stays quiet.
const announcement = ref("");

watch(
  () => rows.value.length,
  (count) => {
    if (count === 0) {
      announcement.value = "Keine Ergebnisse gefunden";
    } else if (count === 1) {
      announcement.value = "1 Fassung";
    } else {
      announcement.value = `${count} Fassungen`;
    }
  },
);
</script>

<template>
  <output aria-atomic="true" aria-live="polite" class="sr-only">
    {{ announcement }}
  </output>

  <UiDataTable
    :columns="columns"
    :row-as="NuxtLink"
    :rows="rows"
    aria-label="Fassungen"
    class="-mx-16 md:mx-0"
  >
    <template #cell-status="{ row }">
      <UiBadge
        :color="row.status.color"
        :label="row.status.label"
        class="font-bold!"
        variant="small"
      />
    </template>

    <template #empty>Keine Ergebnisse gefunden</template>
  </UiDataTable>
</template>
