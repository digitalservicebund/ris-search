import type {
  JSONLDList,
  LegislationExpression,
  SearchResult,
} from "~/types/api";

export default defineNuxtRouteMiddleware(async (from) => {
  const { data, error } = await useRisBackend<
    JSONLDList<SearchResult<LegislationExpression>>
  >("/v1/legislation", {
    query: {
      risAbbreviation: from.params.risAbbreviation,
      mostRelevantOn: getCurrentDateInGermanyFormatted(),
    },
  });

  const expressionEli = data.value?.member?.[0]?.item?.legislationIdentifier;
  if (expressionEli && data.value?.member?.length === 1) {
    return `/gesetze/${expressionEli}`;
  }

  throw createError(error.value ?? { status: 404 });
});
