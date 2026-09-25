<script setup lang="ts">
import { computed } from "vue";
import IcChevronRightIcon from "~icons/ic/outline-chevron-right";
import type { ArticleVersion } from "~/types/api.ts";

const { currentExpressionId, versions } = defineProps<{
  currentExpressionId: string;
  versions: ArticleVersion[];
}>();

type VersionRow = {
  key: string;
  fromDate: string;
  toDate: string;
  contentUrl?: string;
  disabled: boolean;
};

type VersionColumn = {
  key: Extract<keyof VersionRow, string>;
  label: string;
};

const columns: VersionColumn[] = [
  { key: "fromDate", label: "Gültig ab" },
  { key: "toDate", label: "Gültig bis" },
];

const rows = computed<VersionRow[]>(() => {
  // newest single norm first
  const versionsSorted = versions.toSorted((a, b) =>
    b.temporalCoverage.localeCompare(a.temporalCoverage),
  );

  return versionsSorted.map((version) => {
    const validityInterval = temporalCoverageToValidityInterval(
      version.temporalCoverage,
    );

    const disabled = (version.isPartOf ?? [])
      .map((expression) => expression["@id"])
      .includes(currentExpressionId);
    const encodingUrl = getEncodingURL(version.encoding, "text/html");

    return {
      key: version["@id"],
      fromDate: dateFormattedDDMMYYYY(validityInterval?.from) ?? "–",
      toDate: dateFormattedDDMMYYYY(validityInterval?.to) ?? "–",
      contentUrl: encodingUrl,
      disabled,
    };
  });
});

const expandedRowKey = ref<string | undefined>();
const { rowsHtml, updateRowsHtml } = useSingleNormVersionsHtml();

// <details name="..."> makes the rows a mutually exclusive accordion natively,
// even without JS. This handler only keeps the ref in sync with that
// native state; it never drives the expand/collapse behavior itself.
async function onToggle(row: VersionRow, event: Event) {
  const details = event.currentTarget as HTMLDetailsElement;
  // When one row is open and another row is clicked it will send two events.
  // First one with open == true for the row that was clicked
  // and then one with open == false for the other row that implicitly
  // gets closed.
  if (details.open) {
    expandedRowKey.value = row.key;
    await updateRowsHtml(row.key, row.contentUrl);
  } else if (expandedRowKey.value === row.key) {
    expandedRowKey.value = undefined;
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

const currentRowId = useId();
</script>

<template>
  <output aria-atomic="true" aria-live="polite" class="sr-only">
    {{ announcement }}
  </output>

  <ul
    aria-label="Geltungszeiträume"
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
        <!-- The current version is already the one being displayed on this
             page, so it isn't made expandable, and can't use <details>, which
             has no way to disable toggling. -->
        <div
          v-if="row.disabled"
          :aria-labelledby="currentRowId"
          aria-current="true"
          role="group"
          class="col-span-full grid grid-cols-subgrid items-start gap-8 p-16 md:items-center md:gap-0 md:p-0"
        >
          <span
            aria-hidden="true"
            class="col-span-2 grid grid-cols-[max-content_minmax(0,1fr)] gap-8 md:grid-cols-subgrid md:gap-0"
            :id="currentRowId"
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
          </span>
        </div>

        <details
          v-else
          name="single-norm-versions"
          class="group contents details-content:col-span-full"
          @toggle="onToggle(row, $event)"
        >
          <summary
            class="col-span-full grid cursor-pointer list-none grid-cols-subgrid items-start gap-8 p-16 hover:bg-gray-100 focus-visible:outline-4 focus-visible:-outline-offset-4 focus-visible:outline-blue-800 md:items-center md:gap-0 md:p-0 [&::-webkit-details-marker]:hidden"
          >
            <span
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
                  <slot
                    :name="`cell-${column.key}`"
                    :row="row"
                    :column="column"
                  >
                    {{ row[column.key] }}
                  </slot>
                </span>
                {{ " " }}
              </template>
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
                single-article
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
