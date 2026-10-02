import { computed } from "vue";
import type {
  JSONLDList,
  LegislationExpression,
  LegislationSearchParams,
  LegislationWork,
  SearchResult,
} from "~/types/api";
import { getCurrentDateInGermanyFormatted } from "~/utils/dateFormatting";

export async function useNormGesamtausgaben(eli: string) {
  const { data, error } = await useRisBackend<
    JSONLDList<LegislationExpression>
  >(`/v1/legislation/work-example/${eli}`);

  const sortedGesamtausgaben = computed(() => data.value?.member ?? []);
  return { error, sortedGesamtausgaben };
}

export async function useValidNormVersions(eli: string) {
  const today = getCurrentDateInGermanyFormatted();

  const query: LegislationSearchParams = {
    eli,
    temporalCoverageFrom: today,
    temporalCoverageTo: today,
    size: 300,
  };

  return useRisBackend<JSONLDList<SearchResult<LegislationWork>>>(
    "/v1/legislation",
    { query },
  );
}
