<script setup lang="ts">
import type { Article } from "~/types/api";

const props = defineProps<{
  inForceVersionLink: string;
  currentArticle: Article;
}>();

const currentArticleStatus = computed(() => {
  const interval = temporalCoverageToValidityInterval(
    props.currentArticle.temporalCoverage,
  );
  return getValidityStatus({
    from: interval?.from,
    to: interval?.to,
  });
});
</script>

<template>
  <DocumentsNormsVersionWarningMessage
    :current-version-validity-status="currentArticleStatus"
    :in-force-version-link="inForceVersionLink"
    historical-warning-message="Sie lesen einen Paragrafen einer historischen Fassung."
    future-warning-message="Sie lesen einen Paragrafen einer zukünftigen Fassung."
  />
</template>
