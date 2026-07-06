<script setup lang="ts">
import { useCopyUrlActionItem } from "~/composables/useActionMenuItem/useCopyUrlActionItem";
import { usePdfActionItem } from "~/composables/useActionMenuItem/usePdfActionItem";
import { usePrintActionItem } from "~/composables/useActionMenuItem/usePrintActionItem";
import { useXmlActionItem } from "~/composables/useActionMenuItem/useXmlActionItem";
import type { CaseLaw } from "~/types/api";

const { caseLaw } = defineProps<{ caseLaw: CaseLaw | undefined }>();

const actions = computed(() => {
  const requestUrl = useRequestURL();
  requestUrl.search = "";
  const permalink = requestUrl.href;

  const xmlUrl = useBackendUrl(getEncodingURL(caseLaw, "application/xml"));

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
