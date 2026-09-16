<script setup lang="ts">
import { useCopyUrlActionItem } from "~/composables/useActionMenuItem/useCopyUrlActionItem";
import { usePdfActionItem } from "~/composables/useActionMenuItem/usePdfActionItem";
import { usePrintActionItem } from "~/composables/useActionMenuItem/usePrintActionItem";
import { useXmlActionItem } from "~/composables/useActionMenuItem/useXmlActionItem";
import type { Rechtsprechung } from "~/types/api";

const { rechtsprechung } = defineProps<{
  rechtsprechung: Rechtsprechung | undefined;
}>();

const actions = computed(() => {
  const requestUrl = useRequestURL();
  requestUrl.search = "";
  const permalink = requestUrl.href;

  const xmlUrl = useBackendUrl(
    getEncodingURL(rechtsprechung?.encoding, "application/xml"),
  );

  return [
    useCopyUrlActionItem(permalink),
    usePrintActionItem(),
    usePdfActionItem(),
    useXmlActionItem(xmlUrl),
  ];
});
</script>

<template>
  <DocumentsActionMenu :actions />
</template>
