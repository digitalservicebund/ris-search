import { computed } from "vue";
import { type AsyncDataRequestStatus, useFetch } from "#app";
import useBackendUrl from "~/composables/useBackendUrl";
import type { JSONLDList, LegislationWork, SearchResult } from "~/types";
import { getCurrentDateInGermanyFormatted } from "~/utils/dateFormatting";

interface UseNormVersions {
  status: Ref<AsyncDataRequestStatus>;
  sortedVersions: ComputedRef<SearchResult<LegislationWork>[]>;
}

export function useNormVersions(workEli?: string): UseNormVersions {
  const { data, status } = getNorms({
    eli: workEli,
    sort: "-temporalCoverageFrom",
  });
  const sortedVersions = computed(() => data.value?.member ?? []);
  return { status, sortedVersions };
}

function getNorms(params: {
  eli?: string;
  temporalCoverageFrom?: string;
  temporalCoverageTo?: string;
  sort?: string;
}) {
  const immediate = params.eli !== undefined;
  const { status, data, error } = useFetch<
    JSONLDList<SearchResult<LegislationWork>>
  >(useBackendUrl(`/v1/legislation`), {
    params: params,
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
  });
}
