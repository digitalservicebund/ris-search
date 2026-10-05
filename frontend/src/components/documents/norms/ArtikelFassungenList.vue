<script setup lang="ts">
import { computed } from "vue";
import IcChevronRightIcon from "~icons/ic/outline-chevron-right";
import type { DataTableColumn } from "~/components/ui/DataTableCells.vue";
import type { ArtikelFassung } from "~/types/api.ts";

const { currentExpressionId, fassungen } = defineProps<{
  currentExpressionId: string;
  fassungen: ArtikelFassung[];
}>();

type FassungRow = {
  key: string;
  fromDate: string;
  toDate: string;
  contentUrl?: string;
  disabled: boolean;
};

const columns: DataTableColumn<FassungRow>[] = [
  { key: "fromDate", label: "Gültig ab" },
  { key: "toDate", label: "Gültig bis" },
];

const rows = computed<FassungRow[]>(() => {
  // newest single norm first
  const fassungenSorted = fassungen.toSorted((a, b) =>
    b.temporalCoverage.localeCompare(a.temporalCoverage),
  );

  return fassungenSorted.map((fassung) => {
    const validityInterval = temporalCoverageToValidityInterval(
      fassung.temporalCoverage,
    );

    const disabled = (fassung.isPartOf ?? [])
      .map((expression) => expression["@id"])
      .includes(currentExpressionId);
    const encodingUrl = getEncodingURL(fassung.encoding, "text/html");

    return {
      key: fassung["@id"],
      fromDate: dateFormattedDDMMYYYY(validityInterval?.from) ?? "–",
      toDate: dateFormattedDDMMYYYY(validityInterval?.to) ?? "–",
      contentUrl: encodingUrl,
      disabled,
    };
  });
});

const expandedRowKey = ref<string | undefined>();
const { rowsHtml, updateRowsHtml } = useArtikelFassungenHtml();

function expandRow(row?: FassungRow) {
  expandedRowKey.value = row?.key;
  if (row) void updateRowsHtml(row.key, row.contentUrl);
}

async function onRowClick(row: FassungRow, event: MouseEvent) {
  const summary = event.currentTarget as HTMLElement;
  const isExpanding = expandedRowKey.value !== row.key;
  expandRow(isExpanding ? row : undefined);
  if (!isExpanding) return;

  // Closing a longer row above this one shifts it out of the viewport
  await nextTick();
  summary.scrollIntoView({ block: "nearest" });
}

watch(rows, (newRows) => {
  const onlyRow = newRows.length === 1 ? newRows[0] : undefined;
  expandRow(onlyRow?.disabled ? undefined : onlyRow);
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

const currentRowId = useId();
</script>

<template>
  <output aria-atomic="true" aria-live="polite" class="sr-only">
    {{ announcement }}
  </output>

  <ul
    aria-label="Fassungen"
    class="-mx-16 grid grid-cols-[auto_minmax(0,1fr)_max-content] border-t border-gray-400 md:mx-0 md:border-t-0"
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
        :class="{ 'bg-gray-100': row.disabled }"
        class="col-span-full grid grid-cols-subgrid border-b border-gray-400"
      >
        <!-- The current fassung is already the one being displayed on this
             page, so it isn't made expandable, and can't use <details>, which
             has no way to disable toggling. -->
        <div
          v-if="row.disabled"
          :aria-labelledby="currentRowId"
          aria-current="true"
          role="group"
          class="col-span-full grid grid-cols-subgrid items-start p-16 md:items-center md:p-0"
        >
          <span
            aria-hidden="true"
            class="col-span-2 grid grid-cols-[max-content_minmax(0,1fr)] gap-x-16 gap-y-4 md:grid-cols-subgrid md:gap-0"
            :id="currentRowId"
          >
            <UiDataTableCells :columns="columns" :row="row" />
          </span>
        </div>

        <details
          v-else
          :open="expandedRowKey === row.key"
          name="fassungen"
          class="group contents details-content:col-span-full"
        >
          <summary
            @click.prevent="onRowClick(row, $event)"
            class="col-span-full grid cursor-pointer scroll-mt-16 list-none grid-cols-subgrid items-start p-16 hover:bg-gray-100 focus-visible:outline-4 focus-visible:-outline-offset-4 focus-visible:outline-blue-800 md:items-center md:p-0 [&::-webkit-details-marker]:hidden"
          >
            <span
              class="col-span-2 grid grid-cols-[max-content_minmax(0,1fr)] gap-x-16 gap-y-4 md:grid-cols-subgrid md:gap-0"
            >
              <UiDataTableCells :columns="columns" :row="row" />
            </span>
            <IcChevronRightIcon
              aria-hidden="true"
              class="mx-16 size-24 rotate-90 self-center text-blue-800 group-open:-rotate-90"
            />
          </summary>

          <section class="col-span-full">
            <template v-if="expandedRowKey === row.key">
              <DocumentsNormsLegislationContent
                v-if="rowsHtml.get(expandedRowKey)?.html"
              >
                <div
                  class="akn-act -mt-16 px-16"
                  v-html="rowsHtml.get(expandedRowKey)?.html"
                />
              </DocumentsNormsLegislationContent>
              <UiMessage
                v-else-if="rowsHtml.get(expandedRowKey)?.error"
                severity="error"
                class="m-16"
                role="alert"
              >
                Es ist ein Fehler aufgetreten.
              </UiMessage>
              <div v-else class="flex justify-center p-16">
                <UiProgressSpinner />
              </div>
            </template>
          </section>
        </details>
      </li>
    </template>

    <template v-else>
      <li
        class="border-b border-gray-400 px-16 py-12 text-left text-gray-900 md:col-span-full"
      >
        Keine Ergebnisse gefunden
      </li>
    </template>
  </ul>
</template>

<style scoped>
@reference "~/assets/main.css";

:deep(h2.einzelvorschrift) {
  @apply typo-body-bold mt-0 mb-16;
}
</style>
