<script setup lang="ts">
import ActionMenu from "~/components/documents/actionMenu/ActionMenu.vue";
import type { Literature } from "~/types";

const { literature } = defineProps<{ literature: Literature | undefined }>();

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
  <ActionMenu :permalink="permalink" :xml-url="xmlUrl" />
</template>
