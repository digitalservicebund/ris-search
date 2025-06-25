<script setup lang="ts">
import WarningMessage from "~/components/Norm/WarningMessage.vue";
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
  <WarningMessage
    :current-version-status="currentArticleStatus"
    :in-force-version-link="inForceVersionLink"
    historical-warning-message="Paragraf einer historischen Fassung."
    future-warning-message="Paragraph einer zukünftigen Fassung."
  />
</template>
