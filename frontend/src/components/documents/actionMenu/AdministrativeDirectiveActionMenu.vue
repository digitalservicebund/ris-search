<script setup lang="ts">
import ActionMenu from "~/components/documents/actionMenu/ActionMenu.vue";
import type { AdministrativeDirective } from "~/types";

const { administrativeDirective } = defineProps<{
  administrativeDirective: AdministrativeDirective | undefined;
}>();

const xmlUrl = computed(() => {
  const encoding = administrativeDirective?.encoding?.find(
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
