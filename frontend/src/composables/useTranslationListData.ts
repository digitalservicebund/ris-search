import type { TranslationContent } from "~/composables/useTranslationDetailData";
import type { LegislationWork, JSONLDList, SearchResult } from "~/types";
import { getCurrentDateInGermanyFormatted } from "~/utils/dateFormatting";

export async function useTranslationListData(id?: MaybeRefOrGetter<string>) {
  const translationEndpointUrl = computed(() => {
    const idVal = toValue(id);
    const baseUrl = "/v1/translatedLegislation";
    if (idVal == null) return baseUrl;
    return `${baseUrl}?id=${idVal}`;
  });

  const { data, error, status, pending, execute } = await useRisBackend<
    TranslationContent[]
  >(translationEndpointUrl.value);

  return {
    translations: data,
    translationsError: error,
    translationsIsPending: pending,
    translationsStatus: status,
    executeTranslations: execute,
  };
}

export async function useGermanOriginal(id: string) {
  const currentDateInGermanyFormatted = getCurrentDateInGermanyFormatted();
  const searchEndpoint = `/v1/legislation?searchTerm=${id}&temporalCoverageFrom=${currentDateInGermanyFormatted}&temporalCoverageTo=${currentDateInGermanyFormatted}&size=100&pageIndex=0`;

  const { data, error, refresh } =
    await useRisBackend<JSONLDList<SearchResult<LegislationWork>>>(
      searchEndpoint,
    );

  if (error.value) {
    return {
      data: null,
      isLoading: false,
      error: error.value,
      refresh,
    };
  }

  const firstResult = data.value?.member?.[0] ?? null;

  if (firstResult?.item?.abbreviation === id) {
    return {
      data: firstResult,
      isLoading: false,
      error: null,
      refresh,
    };
  }

  const mismatchError = new Error(
    `The fetched legislation (abbreviation: ${firstResult?.item?.abbreviation || "N/A"}) does not match the requested ID: ${id}`,
  );

  return {
    data: null,
    isLoading: false,
    error: mismatchError,
    refresh,
  };
}
