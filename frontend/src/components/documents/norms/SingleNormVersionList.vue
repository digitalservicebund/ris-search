<script setup lang="ts">
import { computed } from "vue";
import IcChevronRightIcon from "~icons/ic/outline-chevron-right";
import type {
  DataTableColumn,
  DataTableRow,
} from "~/components/ui/DataTable.vue";

const props = defineProps<{
  currentSingleNormIdentifier: string;
  versions: SingleNorm[];
}>();

type VersionRow = DataTableRow & {
  fromDate: string;
  toDate: string;
};

const columns: DataTableColumn<VersionRow>[] = [
  { key: "fromDate", label: "Gültig ab" },
  { key: "toDate", label: "Gültig bis" },
];

const rows = computed<VersionRow[]>(() => {
  // newest single norm first
  const versionsSorted = props.versions.toSorted((a, b) =>
    b.temporalCoverage.localeCompare(a.temporalCoverage),
  );

  return versionsSorted.map((version) => {
    const validityInterval = temporalCoverageToValidityInterval(
      version.temporalCoverage,
    );

    const current = version["@id"] === props.currentSingleNormIdentifier;

    return {
      key: version["@id"],
      current,
      fromDate: dateFormattedDDMMYYYY(validityInterval?.from) ?? "–",
      toDate: dateFormattedDDMMYYYY(validityInterval?.to) ?? "–",
    };
  });
});

const currentlyExpandedRowKey = defineModel<string | undefined>();

function isExpanded(row: VersionRow) {
  return row.key === currentlyExpandedRowKey.value;
}

function onRowClick(row: VersionRow) {
  if (isExpanded(row)) {
    currentlyExpandedRowKey.value = undefined;
  } else {
    currentlyExpandedRowKey.value = row.key;
  }
}

// Filtering swaps the rows out in place, which assistive technology does not
// announce. Report the new count instead. Opening the Geltungszeiten tab mounts this
// list, so the watcher skips the initial value and stays quiet.
const announcement = ref("");

watch(
  () => rows.value.length,
  (count) => {
    if (count === 0) {
      announcement.value = "Keine Ergebnisse gefunden";
    } else if (count === 1) {
      announcement.value = "1 Ergebnis";
    } else {
      announcement.value = `${count} Ergebnisse`;
    }
  },
);
</script>

<template>
  <output aria-atomic="true" aria-live="polite" class="sr-only">
    {{ announcement }}
  </output>

  <ul
    class="-mx-16 grid grid-cols-[max-content_minmax(0,1fr)_max-content] border-t border-gray-400 md:mx-0 md:border-t-0"
  >
    <!-- Decorative: every row repeats the column labels for assistive
         technology, so exposing them here as well would only add noise. -->
    <li
      v-if="columns.length"
      class="hidden border-b border-gray-400 md:col-span-full md:grid md:grid-cols-subgrid"
      aria-hidden="true"
    >
      <span
        v-for="column in columns"
        :key="column.key"
        class="typo-label1-bold flex min-h-48 items-center px-16 py-10"
      >
        {{ column.label }}
      </span>
    </li>

    <template v-if="rows.length">
      <li
        v-for="row in rows"
        :key="row.key"
        :class="{ 'bg-gray-100': row.current }"
        class="col-span-full grid grid-cols-subgrid border-b border-gray-400"
      >
        <button
          :aria-expanded="isExpanded(row)"
          class="col-span-full grid cursor-pointer list-none grid-cols-subgrid items-start gap-8 p-16 text-left hover:bg-gray-100 focus-visible:outline-4 focus-visible:-outline-offset-4 focus-visible:outline-blue-800 md:items-center md:gap-0 md:p-0"
          role="button"
          @click.prevent="onRowClick(row)"
        >
          <div
            class="col-span-2 grid grid-cols-[max-content_minmax(0,1fr)] gap-8 md:grid-cols-subgrid md:gap-0"
          >
            <template v-for="column in columns" :key="column.key">
              <span
                class="typo-label1-bold flex min-h-32 items-center md:sr-only"
                >{{ column.label }}:</span
              >
              {{ " " }}
              <span
                class="typo-label1-regular flex min-h-32 items-center md:min-h-48 md:px-16 md:py-10"
              >
                <slot :name="`cell-${column.key}`" :row="row" :column="column">
                  {{ row[column.key] }}
                </slot>
              </span>
              {{ " " }}
            </template>
          </div>
          <IcChevronRightIcon
            class="mx-16 size-24 self-center text-blue-800"
            :class="isExpanded(row) ? '-rotate-90' : 'rotate-90'"
          />
        </button>

        <section
          v-show="isExpanded(row)"
          :aria-labelledby="`version-heading-${row.key}`"
          class="col-span-full"
        >
          Html content coming soon...
        </section>
      </li>
    </template>

    <template v-else>
      <li
        class="border-b border-b-gray-400 px-16 py-12 text-left text-gray-900 md:col-span-full"
      >
        Keine Ergebnisse gefunden
      </li>
    </template>
  </ul>
</template>
