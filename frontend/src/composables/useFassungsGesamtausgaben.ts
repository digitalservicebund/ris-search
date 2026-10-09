import type { JSONLDList, LegislationExpression } from "~/types/api";

/**
 * Fetches all Gesamtausgaben the Fassung with the given revision is part of.
 * Skips the request and returns an empty list while there is no revision.
 */
export function useFassungsGesamtausgaben(
  revision: MaybeRefOrGetter<string | undefined>,
) {
  const { $risBackend } = useNuxtApp();

  return useAsyncData(
    () => `gesamtausgaben for fassung ${toValue(revision)}`,
    async () => {
      const id = toValue(revision);
      if (!id) return [];

      const response = await $risBackend<JSONLDList<LegislationExpression>>(
        `/v1/article/${id}/legislations`,
      );
      return response.member ?? [];
    },
    { default: () => [] },
  );
}
