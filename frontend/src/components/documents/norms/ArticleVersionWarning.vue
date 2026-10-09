<script setup lang="ts">
import type { RouteLocationRaw } from "#vue-router";
import type { Article, ArtikelFassung } from "~/types/api";
import type { FutureVersionTarget } from "./VersionWarningMessage.vue";

const { currentArticle, fassungen, inForceExpressionEli } = defineProps<{
  currentArticle: Article;
  /** All Fassungen of the current Einzelnorm across all Gesamtausgaben */
  fassungen: ArtikelFassung[];
  /** ELI of the currently valid Gesamtausgabe, if one exists */
  inForceExpressionEli?: string;
}>();

const route = useRoute();

function getArticleRoute(
  expressionEli: string | undefined,
  eId: string,
): RouteLocationRaw | undefined {
  if (!expressionEli) return undefined;
  return {
    path: `/gesetze/${expressionEli}/${eId}`,
    query: { from: route.query.from },
  };
}

const currentArticleStatus = computed(() =>
  getVersionValidityStatus(currentArticle),
);

const inForceFassung = computed(() =>
  currentArticleStatus.value === "InForce"
    ? undefined
    : fassungen.find(
        (fassung) => getVersionValidityStatus(fassung) === "InForce",
      ),
);

const nextFutureFassung = computed(() =>
  currentArticleStatus.value === "InForce"
    ? findNextFutureVersion(fassungen)
    : undefined,
);

const [{ data: inForceExpressions }, { data: futureExpressions }] =
  await Promise.all([
    useFassungsGesamtausgaben(() => inForceFassung.value?.revision),
    useFassungsGesamtausgaben(() => nextFutureFassung.value?.revision),
  ]);

const inForceVersionLink = computed(() => {
  if (!inForceFassung.value) return undefined;

  // Prefer the currently valid expression, so the user lands on the article
  // within the currently valid Gesamtausgabe if it is part of it
  const expressionElis = inForceExpressions.value.map(
    (expression) => expression.legislationIdentifier,
  );
  if (inForceExpressionEli && expressionElis.includes(inForceExpressionEli)) {
    return getArticleRoute(inForceExpressionEli, inForceFassung.value.eId);
  }

  const newestExpression = getNewestExpression(inForceExpressions.value);
  return getArticleRoute(
    newestExpression?.legislationIdentifier,
    inForceFassung.value.eId,
  );
});

const futureVersion = computed<FutureVersionTarget | undefined>(() => {
  if (!nextFutureFassung.value) return undefined;

  const newestExpression = getNewestExpression(futureExpressions.value);
  const to = getArticleRoute(
    newestExpression?.legislationIdentifier,
    nextFutureFassung.value.eId,
  );
  if (!to) return undefined;

  return { to, validFrom: getVersionValidFrom(nextFutureFassung.value) };
});
</script>

<template>
  <DocumentsNormsVersionWarningMessage
    :current-version-validity-status="currentArticleStatus"
    :in-force-version-link="inForceVersionLink"
    :future-version="futureVersion"
    document-term="Fassung"
  />
</template>
