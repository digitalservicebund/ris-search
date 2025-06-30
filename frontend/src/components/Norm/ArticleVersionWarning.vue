<script setup lang="ts">
import VersionWarningMessage from "~/components/Norm/VersionWarningMessage.vue";
import type { Article } from "~/types";
import { getValidityStatus } from "~/utils/normUtils";

const props = defineProps<{
  inForceVersionLink: string;
  currentArticle: Article;
}>();

const currentArticleStatus = computed(() =>
  getValidityStatus(
    parseDateGermanLocalTime(props.currentArticle.entryIntoForceDate),
    parseDateGermanLocalTime(props.currentArticle.expiryDate),
  ),
);
</script>

<template>
  <VersionWarningMessage
    :current-version-validity-status="currentArticleStatus"
    :in-force-version-link="inForceVersionLink"
    historical-warning-message="Paragraf einer historischen Fassung."
    future-warning-message="Paragraf einer zukünftigen Fassung."
  />
</template>
