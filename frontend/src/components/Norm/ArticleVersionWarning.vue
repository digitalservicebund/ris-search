<script setup lang="ts">
import VersionWarningMessage from "~/components/Norm/VersionWarningMessage.vue";
import type { Article } from "~/types";
import { getStatusLabel } from "~/composables/useNormVersions";

const props = defineProps<{
  inForceVersionLink: string;
  currentArticle: Article;
}>();

const currentArticleStatus = computed(() =>
  getStatusLabel(
    formattedDate(props.currentArticle.entryIntoForceDate),
    formattedDate(props.currentArticle.expiryDate),
  ),
);
</script>

<template>
  <VersionWarningMessage
    :current-version-status="currentArticleStatus"
    :in-force-version-link="inForceVersionLink"
    historical-warning-message="Paragraf einer historischen Fassung."
    future-warning-message="Paragraf einer zukünftigen Fassung."
  />
</template>
