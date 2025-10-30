<script setup lang="ts">
import ActionsMenu from "~/components/ActionMenu/ActionsMenu.vue";
import { useBackendURL } from "~/composables/useBackendURL";
import type { Literature } from "~/types";

const { literature } = defineProps<{ literature: Literature | null }>();

const backendURL = useBackendURL();
const xmlUrl = computed(() => {
  const encoding = literature?.encoding?.find(
    (e) => e.encodingFormat === "application/xml",
  );
  return encoding?.contentUrl ? backendURL + encoding.contentUrl : undefined;
});

const permalink = {
  label: "Link kopieren",
  url: globalThis?.location?.href,
};
</script>

<template>
  <ActionsMenu :permalink="permalink" :xml-url="xmlUrl" />
</template>
