<script setup lang="ts">
import ActionMenu from "~/components/documents/actionMenu/ActionMenu.vue";
import { useCopyUrlActionItem } from "~/composables/useActionMenuItem/useCopyUrlActionItem";
import { usePdfActionItem } from "~/composables/useActionMenuItem/usePdfActionItem";
import { usePrintActionItem } from "~/composables/useActionMenuItem/usePrintActionItem";
import { useXmlActionItem } from "~/composables/useActionMenuItem/useXmlActionItem";
import type { CaseLaw } from "~/types";
import { getEncodingURL } from "~/utils/caseLaw";

const { caseLaw } = defineProps<{ caseLaw: CaseLaw | undefined }>();

const actions = computed(() => {
  const permalink = useRequestURL().href;

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
  <ActionMenu :actions />
</template>
