import type { Dayjs } from "dayjs";
import { sortBy } from "lodash-es";
import type { ComputedRef, Ref } from "vue";
import type { Page } from "~/components/Pagination.vue";
import type { AnyDocument, SearchResult } from "~/types/api";
import { DocumentKind } from "~/types/api";
import { isLegislation } from "~/utils/anyDocument";
import { getCurrentDateInGermany } from "~/utils/dateFormatting";
import { temporalCoverageToValidityInterval } from "~/utils/norm";
import {
  buildLuceneQuery,
  getLuceneSearchPath,
} from "~/utils/search/luceneSearch";

export const RECENT_UPDATES_DOCUMENT_KINDS = [
  DocumentKind.Norm,
  DocumentKind.CaseLaw,
  DocumentKind.AdministrativeDirective,
  DocumentKind.Literature,
] as const;

/** A document kind shown in the "Aktuelles" section. */
export type RecentUpdatesDocumentKind =
  (typeof RECENT_UPDATES_DOCUMENT_KINDS)[number];

/** Number of results shown per document kind. */
const RESULTS_PER_KIND = 5;

/** The results shown in one tab of the "Aktuelles" section. */
type RecentUpdatesSearch = {
  documentKind: RecentUpdatesDocumentKind;
  searchResults: ComputedRef<SearchResult<AnyDocument>[]>;
  searchError: Readonly<Ref<Error | null | undefined>>;
};

function fetchResults(
  documentKind: RecentUpdatesDocumentKind,
  options: { key: string; query: string; sort: string },
) {
  const query = {
    query: options.query,
    size: RESULTS_PER_KIND,
    sort: options.sort,
    pageIndex: 0,
  };

  return useRisBackend<Page>(getLuceneSearchPath(documentKind), {
    key: options.key,
    query,
  });
}

function getNormDate(
  searchResult: SearchResult<AnyDocument>,
): Dayjs | undefined {
  if (!isLegislation(searchResult.item)) return undefined;
  const coverage = temporalCoverageToValidityInterval(
    searchResult.item.temporalCoverage,
  );
  return coverage?.from;
}

/**
 * Picks the norms to show from a list of past and future norms. Tries to evenly
 * distribute between past and future by getting the results closest to today
 * from each. If not enough results are found, fills up with norms from the
 * other list if possible, or returns fewer results.
 *
 * @param past Norms that entered into force on or before today
 * @param future Norms entering into force after today
 * @returns List of recently updated norms
 */
function pickNorms(
  past: SearchResult<AnyDocument>[],
  future: SearchResult<AnyDocument>[],
) {
  const reservedPerHalf = 2;

  const today = getCurrentDateInGermany().startOf("day");

  const distanceToToday = (searchResult: SearchResult<AnyDocument>) => {
    const date = getNormDate(searchResult);
    return date ? Math.abs(date.diff(today, "day")) : Infinity;
  };

  const descendingByDate = (searchResult: SearchResult<AnyDocument>) =>
    -(getNormDate(searchResult)?.valueOf() ?? -Infinity);

  const nearestPast = sortBy(past, distanceToToday);
  const nearestFuture = sortBy(future, distanceToToday);

  const picked = [
    ...nearestPast.slice(0, reservedPerHalf),
    ...nearestFuture.slice(0, reservedPerHalf),
    ...sortBy(
      [
        ...nearestPast.slice(reservedPerHalf),
        ...nearestFuture.slice(reservedPerHalf),
      ],
      distanceToToday,
    ),
  ].slice(0, RESULTS_PER_KIND);

  return sortBy(picked, descendingByDate);
}

const normWindowQuery = (range: string) =>
  buildLuceneQuery(range, { type: "allTime" }, DocumentKind.Norm);

function searchNorms() {
  const normWindowDays = 10;

  const past = fetchResults(DocumentKind.Norm, {
    key: "recent-updates-norms-past",
    query: normWindowQuery(`DATUM:[now-${normWindowDays}d/d TO now/d]`),
    sort: "-date",
  });

  const future = fetchResults(DocumentKind.Norm, {
    key: "recent-updates-norms-future",
    query: normWindowQuery(`DATUM:[now+1d/d TO now+${normWindowDays}d/d]`),
    sort: "date",
  });

  const search: RecentUpdatesSearch = {
    documentKind: DocumentKind.Norm,
    searchResults: computed(() =>
      pickNorms(past.data.value?.member ?? [], future.data.value?.member ?? []),
    ),
    searchError: computed(() => past.error.value ?? future.error.value),
  };

  return { requests: [past, future], search };
}

function searchOtherDocuments(
  documentKind: Exclude<RecentUpdatesDocumentKind, DocumentKind.Norm>,
) {
  const request = fetchResults(documentKind, {
    key: `recent-updates-${documentKind}`,
    query: buildLuceneQuery(
      "",
      { type: getDefaultDateFilterType(documentKind) },
      documentKind,
    ),
    sort: "-date",
  });

  const search: RecentUpdatesSearch = {
    documentKind,
    searchResults: computed(() => request.data.value?.member ?? []),
    searchError: request.error,
  };

  return { requests: [request], search };
}

/**
 * Loads the results shown in the "Aktuelles" section of the landing page.
 *
 * @returns Search results
 */
export async function useRecentUpdates() {
  const searches = RECENT_UPDATES_DOCUMENT_KINDS.map((documentKind) =>
    documentKind === DocumentKind.Norm
      ? searchNorms()
      : searchOtherDocuments(documentKind),
  );

  await Promise.all(searches.flatMap(({ requests }) => requests));

  return searches.map(({ search: result }) => result);
}
