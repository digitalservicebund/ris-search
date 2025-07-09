import _ from "lodash";
import { computed } from "vue";
import { type AsyncDataRequestStatus, useFetch } from "#app";
import type { JSONLDList, LegislationWork, SearchResult } from "~/types";

interface UseNormVersions {
  status: Ref<AsyncDataRequestStatus>;
  sortedVersions: ComputedRef<SearchResult<LegislationWork>[]>;
}

export function useNormVersions(workEli?: string): UseNormVersions {
  const { data, status } = getNorms({ eli: workEli });
  const sortedVersions = computed(() =>
    _.sortBy(
      data.value?.member ?? [],
      (member) => member.item.workExample.temporalCoverage,
    ),
  );
  return { status, sortedVersions };
}

function getNorms(params: {
  eli?: string;
  temporalCoverageFrom?: string;
  temporalCoverageTo?: string;
}) {
  const backendURL = useBackendURL();
  const immediate = params.eli !== undefined;
  const { status, data, error } = useFetch<
    JSONLDList<SearchResult<LegislationWork>>
  >(`${backendURL}/v1/legislation`, {
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
