import type { AsyncDataRequestStatus } from "#app";
import { computed, type ComputedRef } from "vue";
import type {
  JSONLDList,
  LegislationExpression,
  LegislationSearchParams,
  LegislationWork,
  SearchResult,
} from "~/types/api";
import { getCurrentDateInGermanyFormatted } from "~/utils/dateFormatting";

interface UseNormVersions {
  status: Ref<AsyncDataRequestStatus>;
  sortedVersions: ComputedRef<LegislationExpression[]>;
}

export function useNormVersions(workEli?: string): UseNormVersions {
  const { data, status } = getNormVersions(workEli);
  const sortedVersions = computed(() => data.value?.member ?? []);
  return { status, sortedVersions };
}

function getNormVersions(workEli?: string) {
  const immediate = true;
  const { status, data, error } = useRisBackend<
    JSONLDList<LegislationExpression>
  >(`/v1/legislation/work-example/${workEli}`, {
    immediate: immediate,
  });

  if (error?.value) {
    showError(error.value);
  }

  return { status, data };
}

function getNorms(
  params: Omit<LegislationSearchParams, "size"> & { size: string },
) {
  const immediate = params.eli !== undefined;
  const { status, data, error } = useRisBackend<
    JSONLDList<SearchResult<LegislationWork>>
  >(`/v1/legislation`, {
    params,
    immediate: immediate,
  });

  if (error?.value) {
    showError(error.value);
  }

  return { status, data };
}

export function useValidNormVersions(workEli?: string) {
  const today = getCurrentDateInGermanyFormatted();
  return getNorms({
    eli: workEli,
    temporalCoverageFrom: today,
    temporalCoverageTo: today,
    size: "300",
  });
}
