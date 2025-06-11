import { type AsyncDataRequestStatus, useFetch } from "#app";
import type { JSONLDList, LegislationWork, SearchResult } from "~/types";
import _ from "lodash";

import { computed } from "vue";

interface UseNormVersions {
  status: Ref<AsyncDataRequestStatus>;
  sortedVersions: ComputedRef<SearchResult<LegislationWork>[]>;
}

export function useNormVersions(workEli: string): UseNormVersions {
  const backendURL = useBackendURL();
  const { status, data, error } = useFetch<
    JSONLDList<SearchResult<LegislationWork>>
  >(`${backendURL}/v1/legislation`, { params: { eli: workEli } });

  const sortedVersions = computed(() =>
    error?.value
      ? []
      : _.sortBy(
          data.value?.member ?? [],
          (member) => member.item.workExample.temporalCoverage,
        ),
  );

  return { status, sortedVersions };
}
