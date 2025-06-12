import { type AsyncDataRequestStatus, useFetch } from "#app";
import type { JSONLDList, LegislationWork, SearchResult } from "~/types";
import _ from "lodash";

import { computed } from "vue";
import { formattedDateToDateTime } from "~/utils/dateFormatting";

interface UseNormVersions {
  status: Ref<AsyncDataRequestStatus>;
  sortedVersions: ComputedRef<SearchResult<LegislationWork>[]>;
}

export type VersionStatus = "inForce" | "future" | "historical" | undefined;

export function useNormVersions(workEli: string): UseNormVersions {
  const backendURL = useBackendURL();
  const { status, data, error } = useFetch<
    JSONLDList<SearchResult<LegislationWork>>
  >(`${backendURL}/v1/legislation`, { params: { eli: workEli } });

  if (error?.value) {
    showError(error.value);
  }

  const sortedVersions = computed(() =>
    _.sortBy(
      data.value?.member ?? [],
      (member) => member.item.workExample.temporalCoverage,
    ),
  );

  return { status, sortedVersions };
}

export const getVersionDates = (
  version: SearchResult<LegislationWork> | undefined,
) => {
  const temporalCoverage = version?.item.workExample.temporalCoverage ?? "";
  return splitTemporalCoverage(temporalCoverage);
};

export const getVersionStatus = (
  version: SearchResult<LegislationWork> | undefined,
): VersionStatus => {
  const [startDate, endDate] = getVersionDates(version);
  const status = version?.item.workExample.legislationLegalForce;
  if (status === "InForce") {
    return "inForce";
  } else {
    if (startDate && formattedDateToDateTime(startDate) > new Date()) {
      return "future";
    }
    if (endDate && formattedDateToDateTime(endDate) < new Date()) {
      return "historical";
    }
  }
  return undefined;
};
