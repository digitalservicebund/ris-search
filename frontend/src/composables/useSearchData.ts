import type { LegislationWork, JSONLDList, SearchResult } from "~/types";
import { getCurrentDateInGermanyFormatted } from "~/utils/dateFormatting";

export async function useLegislationSearchForAbbreviation(id: string) {
  const currentDateInGermanyFormatted = getCurrentDateInGermanyFormatted();
  const searchEndpoint = `/v1/legislation?searchTerm=${id}&temporalCoverageFrom=${currentDateInGermanyFormatted}&temporalCoverageTo=${currentDateInGermanyFormatted}&size=100&pageIndex=0`;

  const legislation = ref<SearchResult<LegislationWork> | null>(null);
  const legislationSearchError = ref<Error | null>(null);

  const { data, error, status, pending, execute } =
    await useRisBackend<JSONLDList<SearchResult<LegislationWork>>>(
      searchEndpoint,
    );

  if (error.value) {
    legislationSearchError.value = error.value;
  } else {
    const firstResult = data.value?.member?.[0] ?? null;
    if (firstResult?.item?.abbreviation === id) {
      legislation.value = firstResult;
    } else {
      legislationSearchError.value = new Error(
        `The fetched legislation does not match the requested ID: ${id}`,
      );
    }
  }

  return {
    legislation,
    legislationSearchError,
    legislationSearchIsPending: pending,
    legislationSearchStatus: status,
    executeLegislationSearch: execute,
  };
}
