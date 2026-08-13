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
  const { data, status, error } = await useRisBackend<
    JSONLDList<LegislationExpression>
  >(`/v1/legislation/work-example/${eli}`, { immediate: true });

  const sortedVersions = computed(() => data.value?.member ?? []);
  return { data, status, error, sortedVersions };
}

export async function useValidNormVersions(eli: string) {
  const today = getCurrentDateInGermanyFormatted();
  return getNorms({
    eli: eli,
    temporalCoverageFrom: today,
    temporalCoverageTo: today,
    size: 300,
  });
}

function getNorms(params: LegislationSearchParams) {
  const immediate = params.eli !== undefined;
  return useRisBackend<JSONLDList<SearchResult<LegislationWork>>>(
    `/v1/legislation`,
    { params, immediate: immediate },
  );
}
