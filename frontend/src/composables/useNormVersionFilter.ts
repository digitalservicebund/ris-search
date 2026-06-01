import { computed, ref } from "vue";
import type { LegislationExpression } from "~/types/api";
import { parseDateGermanLocalTime } from "~/utils/dateFormatting";
import { temporalCoverageToValidityInterval } from "~/utils/norm";

export function useNormVersionFilter(
  normVersions: Ref<LegislationExpression[]>,
) {
  const dateFilterValue = ref<string>();

  const filteredNormVersions = computed<LegislationExpression[]>(() => {
    const filterDate = parseDateGermanLocalTime(dateFilterValue.value);

    if (!filterDate) return normVersions.value;

    const matchingExpression = normVersions.value.find((expression) => {
      const validityInterval = temporalCoverageToValidityInterval(
        expression.temporalCoverage,
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

    return matchingExpression ? [matchingExpression] : [];
  });

  return { dateFilterValue, filteredNormVersions };
}
