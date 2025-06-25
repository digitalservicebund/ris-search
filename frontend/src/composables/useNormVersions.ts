import { type AsyncDataRequestStatus, useFetch } from "#app";
import type {
  JSONLDList,
  LegalForceStatus,
  LegislationWork,
  SearchResult,
} from "~/types";
import _ from "lodash";

import { computed } from "vue";
import { formattedDateToDateTime } from "~/utils/dateFormatting";
import { temporalCoverageToValidityInterval } from "~/utils/normUtils";

interface UseNormVersions {
  status: Ref<AsyncDataRequestStatus>;
  sortedVersions: ComputedRef<SearchResult<LegislationWork>[]>;
}

export type VersionStatus = "inForce" | "future" | "historical" | undefined;

export function useNormVersions(workEli?: string): UseNormVersions {
  const backendURL = useBackendURL();
  const immediate = workEli !== undefined;
  const { status, data, error } = useFetch<
    JSONLDList<SearchResult<LegislationWork>>
  >(`${backendURL}/v1/legislation`, {
    params: { eli: workEli },
    immediate: immediate,
  });

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

export const getVersionStatus = (
  version: SearchResult<LegislationWork> | undefined,
): VersionStatus => {
  const { from: startDate, to: endDate } =
    temporalCoverageToValidityInterval(
      version?.item.workExample.temporalCoverage,
    ) || {};
  const status = version?.item.workExample.legislationLegalForce;
  return getStatusLabel(startDate, endDate, status);
};

export function getStatusLabel(
  startDate: string | undefined,
  endDate: string | undefined,
  status?: LegalForceStatus | undefined,
): VersionStatus {
  if (status === "InForce") {
    return "inForce";
  }
  const dateFrom = startDate ? formattedDateToDateTime(startDate) : undefined;
  const dateTo = endDate ? formattedDateToDateTime(endDate) : undefined;
  const now = new Date();
  console.log(startDate, endDate, dateFrom, dateTo, now);
  if (dateFrom && dateFrom > now) {
    return "future";
  }
  if (dateTo && dateTo < now) {
    return "historical";
  }
  if (dateFrom && dateFrom <= now && (!dateTo || dateTo >= now)) {
    return "inForce";
  }
  return undefined;
}
