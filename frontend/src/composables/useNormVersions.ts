import { type AsyncDataRequestStatus, useFetch } from "#app";
import type {
  JSONLDList,
  LegalForceStatus,
  LegislationWork,
  SearchResult,
} from "~/types";
import _ from "lodash";

import { computed } from "vue";
import {
  formattedDateToDateTime,
  getCurrentDateInGermany,
} from "~/utils/dateFormatting";

interface UseNormVersions {
  status: Ref<AsyncDataRequestStatus>;
  sortedVersions: ComputedRef<SearchResult<LegislationWork>[]>;
}

export type VersionStatus = "inForce" | "future" | "historical" | undefined;

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
  const today = getCurrentDateInGermany();
  return getNorms({
    eli: workEli,
    temporalCoverageFrom: today,
    temporalCoverageTo: today,
  });
}

export const getVersionDates = (version: LegislationWork | undefined) => {
  const temporalCoverage = version?.workExample.temporalCoverage ?? "";
  return splitTemporalCoverage(temporalCoverage);
};

export const getVersionStatus = (
  version: LegislationWork | undefined,
): VersionStatus => {
  const [startDate, endDate] = getVersionDates(version);
  const status = version?.workExample.legislationLegalForce;
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
