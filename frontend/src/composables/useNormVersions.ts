import { computed } from "vue";
import type {
  JSONLDList,
  LegislationExpression,
  LegislationSearchParams,
  LegislationWork,
  SearchResult,
} from "~/types/api";
import { getCurrentDateInGermanyFormatted } from "~/utils/dateFormatting";

export async function useNormVersions(eli: string) {
  const { data, error } = await useRisBackend<
    JSONLDList<LegislationExpression>
  >(`/v1/legislation/work-example/${eli}`);

  const sortedVersions = computed(() => data.value?.member ?? []);
  return { error, sortedVersions };
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
