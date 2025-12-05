<script setup lang="ts">
import ActionsMenu from "~/components/ActionMenu/ActionsMenu.vue";
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
  <ActionsMenu :permalink="permalink" :xml-url="xmlUrl" />
</template>
