<script setup lang="ts">
import ActionMenu from "~/components/documents/actionMenu/ActionMenu.vue";
import { useCopyUrlActionItem } from "~/composables/useActionMenuItem/useCopyUrlActionItem";
import { usePdfActionItem } from "~/composables/useActionMenuItem/usePdfActionItem";
import { usePrintActionItem } from "~/composables/useActionMenuItem/usePrintActionItem";
import { useXmlActionItem } from "~/composables/useActionMenuItem/useXmlActionItem";
import type { AdministrativeDirective } from "~/types";

const { administrativeDirective } = defineProps<{
  administrativeDirective: AdministrativeDirective | undefined;
}>();

const actions = computed(() => {
  const permalink = useRequestURL().href;

  const xmlUrl = useBackendUrl(
    administrativeDirective?.encoding?.find(
      (e) => e.encodingFormat === "application/xml",
    )?.contentUrl ?? undefined,
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
  <ActionMenu :actions />
</template>
