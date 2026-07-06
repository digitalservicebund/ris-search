<script setup lang="ts">
import { useCopyUrlActionItem } from "~/composables/useActionMenuItem/useCopyUrlActionItem";
import { usePdfActionItem } from "~/composables/useActionMenuItem/usePdfActionItem";
import { usePrintActionItem } from "~/composables/useActionMenuItem/usePrintActionItem";

const actions = computed(() => {
  const requestUrl = useRequestURL();
  requestUrl.search = "";
  const permalink = requestUrl.href;

  return [
    useCopyUrlActionItem(permalink, "Link to translation"),
    usePrintActionItem(),
    usePdfActionItem(),
  ];
});
</script>

<template>
  <DocumentsActionMenu :actions />
</template>
