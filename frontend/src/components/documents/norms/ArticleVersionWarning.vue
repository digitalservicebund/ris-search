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

const inForceVersionLink = computed(() => {
  const inForceFassung = fassungen.find(
    (fassung) => getVersionValidityStatus(fassung) === "InForce",
  );
  if (!inForceFassung) return undefined;

  // Prefer the currently valid expression, so the user lands on the article
  // within the currently valid Gesamtausgabe if it is part of it
  const expressionElis = getExpressionElis(inForceFassung);
  if (inForceExpressionEli && expressionElis.includes(inForceExpressionEli)) {
    return getArticleRoute(inForceExpressionEli, inForceFassung.eId);
  }

  const newestExpressionEli = getNewestExpressionEli(expressionElis);
  return getArticleRoute(newestExpressionEli, inForceFassung.eId);
});

const futureVersion = computed<FutureVersionTarget | undefined>(() => {
  if (currentArticleStatus.value !== "InForce") return undefined;

  const nextFutureFassung = findNextFutureVersion(fassungen);
  if (!nextFutureFassung) return undefined;

  const expressionElis = getExpressionElis(nextFutureFassung);
  const newestExpressionEli = getNewestExpressionEli(expressionElis);
  const to = getArticleRoute(newestExpressionEli, nextFutureFassung.eId);
  if (!to) return undefined;

  return { to, validFrom: getVersionValidFrom(nextFutureFassung) };
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
