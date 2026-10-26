import dayjs from "dayjs";
import { computed, ref } from "vue";
import { temporalCoverageToValidityInterval } from "~/utils/norm";

export function useVersionDateFilter<T extends { temporalCoverage: string }>(
  versions: Ref<T[]>,
) {
  const dateFilterValue = ref<string>();

  const filteredVersions = computed<T[]>(() => {
    return versionDateFilter(versions.value, dateFilterValue.value);
  });

  return { dateFilterValue, filteredVersions };
}

export function versionDateFilter<T extends { temporalCoverage: string }>(
  versions: T[],
  date?: string,
): T[] {
  const filterDate = date
    ? dayjs(date, "YYYY-MM-DD").tz("Europe/Berlin")
    : undefined;

  if (!filterDate) return versions;

  const matchingVersion = versions.find((version) => {
    const validityInterval = temporalCoverageToValidityInterval(
      version.temporalCoverage,
    );

    const inForceDate = validityInterval?.from;
    const outOfForceDate = validityInterval?.to;

    const isOnOrAfterInForce =
      filterDate.isSame(inForceDate, "day") ||
      filterDate.isAfter(inForceDate, "day");

    const isOnOrBeforeOutOfForce =
      filterDate.isBefore(outOfForceDate, "day") ||
      filterDate.isSame(outOfForceDate, "day") ||
      !outOfForceDate;

    return isOnOrAfterInForce && isOnOrBeforeOutOfForce;
  });

  return matchingVersion ? [matchingVersion] : [];
}
