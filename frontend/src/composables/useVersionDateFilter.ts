import dayjs from "dayjs";
import { computed, ref } from "vue";
import { temporalCoverageToValidityInterval } from "~/utils/norm";

export function useVersionDateFilter<T extends { temporalCoverage: string }>(
  versions: Ref<T[]>,
) {
  const dateFilterValue = ref<string>();

  const filteredVersions = computed<T[]>(() => {
    const filterDate = dateFilterValue.value
      ? dayjs(dateFilterValue.value, "YYYY-MM-DD").tz("Europe/Berlin")
      : undefined;

    if (!filterDate) return versions.value;

    const matchingVersion = versions.value.find((version) => {
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
  });

  return { dateFilterValue, filteredVersions };
}
