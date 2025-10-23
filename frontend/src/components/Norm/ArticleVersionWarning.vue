<script setup lang="ts">
import VersionWarningMessage from "~/components/Norm/VersionWarningMessage.vue";
import type { Article } from "~/types";
import { parseDateGermanLocalTime } from "~/utils/dateFormatting";
import { getValidityStatus } from "~/utils/norm";

const props = defineProps<{
  inForceVersionLink: string;
  currentArticle: Article;
}>();

const currentArticleStatus = computed(() =>
  getValidityStatus({
    from: parseDateGermanLocalTime(props.currentArticle.entryIntoForceDate),
    to: parseDateGermanLocalTime(props.currentArticle.expiryDate),
  }),
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
