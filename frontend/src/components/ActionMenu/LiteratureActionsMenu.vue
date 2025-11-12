<script setup lang="ts">
import ActionsMenu from "~/components/ActionMenu/ActionsMenu.vue";
import type { Literature } from "~/types";

const { literature } = defineProps<{ literature: Literature | null }>();

const xmlUrl = computed(() => {
  const encoding = literature?.encoding?.find(
    (e) => e.encodingFormat === "application/xml",
  );
  return encoding?.contentUrl ?? undefined;
});

const permalink = {
  label: "Link kopieren",
  url: globalThis?.location?.href,
};
</script>

<template>
  <ActionsMenu :permalink="permalink" :xml-url="xmlUrl" />
</template>
