<script setup lang="ts">
import type { RouteLocationRaw } from "#vue-router";
import type { Article, ArtikelFassung } from "~/types/api";
import type { FutureVersionTarget } from "./VersionWarningMessage.vue";

const { currentArticle, versions, inForceExpressionEli } = defineProps<{
  currentArticle: Article;
  /** All versions of the current article across the work */
  versions: ArtikelFassung[];
  /** ELI of the currently valid expression of the norm, if one exists */
  inForceExpressionEli?: string;
}>();

const route = useRoute();

function getArticleRoute(
  expressionEli: string | undefined,
  version: ArtikelFassung,
): RouteLocationRaw | undefined {
  if (!expressionEli) return undefined;
  return {
    path: `/gesetze/${expressionEli}/${version.eId}`,
    query: { from: route.query.from },
  };
}

const currentArticleStatus = computed(() =>
  getVersionValidityStatus(currentArticle),
);

const inForceVersionLink = computed(() => {
  const inForceVersion = versions.find(
    (version) => getVersionValidityStatus(version) === "InForce",
  );
  if (!inForceVersion) return undefined;

  // Prefer the currently valid expression, so the user lands on the article
  // within the currently valid Gesamtausgabe if it is part of it
  const expressionElis = getExpressionElis(inForceVersion);
  if (inForceExpressionEli && expressionElis.includes(inForceExpressionEli)) {
    return getArticleRoute(inForceExpressionEli, inForceVersion);
  }

  const newestExpressionEli = getNewestExpressionEli(expressionElis);
  return getArticleRoute(newestExpressionEli, inForceVersion);
});

const futureVersion = computed<FutureVersionTarget | undefined>(() => {
  if (currentArticleStatus.value !== "InForce") return undefined;

  const nextFutureVersion = findNextFutureVersion(versions);
  if (!nextFutureVersion) return undefined;

  const expressionElis = getExpressionElis(nextFutureVersion);
  const newestExpressionEli = getNewestExpressionEli(expressionElis);
  const to = getArticleRoute(newestExpressionEli, nextFutureVersion);
  if (!to) return undefined;

  return { to, validFrom: getVersionValidFrom(nextFutureVersion) };
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
