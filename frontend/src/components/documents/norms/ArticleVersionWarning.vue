<script setup lang="ts">
import VersionWarningMessage from "~/components/documents/norms/VersionWarningMessage.vue";
import type { Article } from "~/types/api";
import { getValidityStatus } from "~/utils/norm";

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
  <VersionWarningMessage
    :current-version-validity-status="currentArticleStatus"
    :in-force-version-link="inForceVersionLink"
    historical-warning-message="Sie lesen einen Paragrafen einer historischen Fassung."
    future-warning-message="Sie lesen einen Paragrafen einer zukünftigen Fassung."
  />
</template>
