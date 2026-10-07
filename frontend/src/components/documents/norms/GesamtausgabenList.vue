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
  gesamtausgaben: LegislationExpression[];
}>();

type GesamtausgabeRow = DataTableRow & {
  fromDate: string;
  toDate: string;
  status: { label: string; color: BadgeColor };
};

const route = useRoute();

const missingDate = "—";

const columns: DataTableColumn<GesamtausgabeRow>[] = [
  {
    key: "fromDate",
    label: "Gültig ab",
    separatorAfter: "–",
  },
  { key: "toDate", label: "Gültig bis" },
  { key: "status", label: "Status" },
];

const rows = computed<GesamtausgabeRow[]>(() => {
  // newest Gesamtausgabe first
  const gesamtausgabenSorted = props.gesamtausgaben.toSorted((a, b) =>
    b.temporalCoverage.localeCompare(a.temporalCoverage),
  );

  return gesamtausgabenSorted.map((gesamtausgabe) => {
    const validityInterval = temporalCoverageToValidityInterval(
      gesamtausgabe.temporalCoverage,
    );

    const current =
      gesamtausgabe.legislationIdentifier ===
      props.currentLegislationIdentifier;

    const to = {
      path: `/gesetze/${gesamtausgabe.legislationIdentifier}`,
      query: { from: route.query.from },
    };

    const status = formatNormValidity(gesamtausgabe.temporalCoverage) ?? {
      label: "Unbekannt",
      color: "blue",
    };

    return {
      key: gesamtausgabe.legislationIdentifier ?? "",
      attrs: { to },
      current,
      fromDate: dateFormattedDDMMYYYY(validityInterval?.from) ?? missingDate,
      toDate: dateFormattedDDMMYYYY(validityInterval?.to) ?? missingDate,
      status,
    };
  });
});

// Filtering swaps the rows out in place, which assistive technology does not
// announce. Report the new count instead. Opening the Gesamtausgaben tab mounts this
// list, so the watcher skips the initial value and stays quiet.
const announcement = ref("");

watch(
  () => rows.value.length,
  (count) => {
    if (count === 0) {
      announcement.value = "Keine Ergebnisse gefunden";
    } else if (count === 1) {
      announcement.value = "1 Gesamtausgabe";
    } else {
      announcement.value = `${count} Gesamtausgaben`;
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
    aria-label="Gesamtausgaben"
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
