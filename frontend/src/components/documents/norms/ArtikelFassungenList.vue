<script setup lang="ts">
import { computed } from "vue";
import IcChevronRightIcon from "~icons/ic/outline-chevron-right";
import GesamtausgabenList from "~/components/documents/norms/GesamtausgabenList.vue";
import { useArtikelFassungen } from "~/composables/useArtikelFassungen.ts";
import type { ArtikelFassung, LegislationExpression } from "~/types/api.ts";
import { dateFormattedDDMMYYYY } from "~/utils/dateFormatting.ts";

const {
  currentLegislationIdentifier,
  currentFassungId,
  fassungen,
  dateFilter,
} = defineProps<{
  currentLegislationIdentifier: string;
  currentFassungId: string;
  fassungen: ArtikelFassung[];
  dateFilter?: string;
}>();

type FassungRow = {
  key: string;
  fromDate: string;
  toDate: string;
  contentUrl?: string;
  revision?: string;
  current: boolean;
};

type FassungColumn = {
  key: Extract<keyof FassungRow, string>;
  label: string;
};

type RowContent = {
  gesamtausgabeLink?: { label: string; to: string };
  gesamtausgaben: LegislationExpression[];
  html?: string;
  error: boolean;
};

const missingDate = "—";

const columns: FassungColumn[] = [
  { key: "fromDate", label: "Gültig ab" },
  { key: "toDate", label: "Gültig bis" },
];

function hasBothDates(row: FassungRow) {
  return row.fromDate !== missingDate && row.toDate !== missingDate;
}

const rows = computed<FassungRow[]>(() => {
  // newest single norm first
  const fassungenSorted = fassungen.toSorted((a, b) =>
    b.temporalCoverage.localeCompare(a.temporalCoverage),
  );

  return fassungenSorted.map((fassung) => {
    const validityInterval = temporalCoverageToValidityInterval(
      fassung.temporalCoverage,
    );

    return {
      key: fassung["@id"],
      fromDate: dateFormattedDDMMYYYY(validityInterval?.from) ?? missingDate,
      toDate: dateFormattedDDMMYYYY(validityInterval?.to) ?? missingDate,
      contentUrl: getEncodingURL(fassung.encoding, "text/html"),
      revision: fassung.revision,
      current: fassung.revision === currentFassungId,
    };
  });
});

const expandedRowKey = ref<string | undefined>();
const { fassungenCache, updateCache } = useArtikelFassungen();

function expandRow(row?: FassungRow) {
  expandedRowKey.value = row?.key;
  if (row)
    void updateCache({
      key: row.key,
      contentUrl: row.contentUrl,
      revision: row.revision,
    });
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

const expandedRowContent = computed<RowContent | undefined>(() => {
  if (!expandedRowKey.value) return undefined;
  const cachedData = fassungenCache.value.get(expandedRowKey.value);
  if (!cachedData) return undefined;

  const gesamtausgaben = versionDateFilter(
    cachedData.gesamtausgaben,
    dateFilter,
  );

  if (gesamtausgaben.length === 1) {
    const gesamtausgabe = gesamtausgaben[0]!;
    const formattedTemporalCoverage = formatTemporalCoverage(
      gesamtausgabe.temporalCoverage,
    );
    const label = `Gesamtausgabe ${formattedTemporalCoverage}öffnen`;
    const to = `/gesetze/${gesamtausgabe.legislationIdentifier}`;

    return {
      ...cachedData,
      gesamtausgabeLink: {
        label,
        to,
      },
    };
  }

  return {
    ...cachedData,
  };
});

function formatTemporalCoverage(temporalCoverage?: string) {
  const coverage = temporalCoverageToValidityInterval(temporalCoverage);
  const from = dateFormattedDDMMYYYY(coverage?.from);
  const to = dateFormattedDDMMYYYY(coverage?.to);

  if (from && to) {
    return `${from} - ${to} `;
  } else if (from) {
    return `gültig ab ${from} `;
  } else if (to) {
    return `gültig bis ${to} `;
  } else {
    return "";
  }
}

watch(rows, (newRows) => {
  const onlyRow = newRows.length === 1 ? newRows[0] : undefined;
  expandRow(onlyRow);
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
        :aria-current="row.current || undefined"
        :class="{ 'bg-gray-100': row.current }"
        class="col-span-full grid grid-cols-subgrid border-b border-gray-400"
      >
        <details
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
              <template v-for="column in columns" :key="column.key">
                <span
                  class="typo-label1-bold flex min-h-32 items-center md:sr-only"
                  >{{ column.label }}:</span
                >
                {{ " " }}
                <span
                  class="typo-label1-regular flex min-h-32 items-center md:relative md:min-h-48 md:px-16 md:py-10"
                >
                  <span
                    :class="{ 'md:hidden': row[column.key] === missingDate }"
                    >{{ row[column.key] }}</span
                  ><span
                    v-if="column.key === 'fromDate' && hasBothDates(row)"
                    aria-hidden="true"
                    class="absolute right-0 hidden translate-x-1/2 md:block"
                    >–</span
                  >
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
              <template v-if="expandedRowContent?.error === false">
                <div
                  class="mx-16 mt-8 border border-blue-400 bg-white p-16"
                  :class="{ 'mb-10': row.current }"
                >
                  <NuxtLink
                    v-if="expandedRowContent.gesamtausgabeLink"
                    class="typo-link1-bold link-hover"
                    :to="expandedRowContent.gesamtausgabeLink.to"
                    >{{ expandedRowContent.gesamtausgabeLink.label }}</NuxtLink
                  >
                  <UiAccordion
                    v-else
                    header-collapsed="Gesamtausgabe auswählen"
                    header-expanded="Gesamtausgabe auswählen"
                    :model-value="row.current"
                  >
                    <GesamtausgabenList
                      :current-legislation-identifier="
                        currentLegislationIdentifier
                      "
                      :gesamtausgaben="expandedRowContent.gesamtausgaben"
                    />
                  </UiAccordion>
                </div>
                <DocumentsNormsLegislationContent v-if="!row.current">
                  <div class="akn-act px-16" v-html="expandedRowContent.html" />
                </DocumentsNormsLegislationContent>
              </template>
              <UiMessage
                v-else-if="expandedRowContent?.error"
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
