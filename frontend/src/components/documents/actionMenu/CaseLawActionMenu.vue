<script setup lang="ts">
import ActionMenuWrapper from "~/components/documents/actionMenu/ActionMenuWrapper.vue";
import type { CaseLaw } from "~/types";
import { getEncodingURL } from "~/utils/caseLaw";

const { caseLaw } = defineProps<{ caseLaw: CaseLaw | undefined }>();

const xmlUrl = computed(() => getEncodingURL(caseLaw, "application/xml"));

const permalink = {
  label: "Link kopieren",
  url: "",
  disabled: true,
};
</script>

<template>
  <ActionMenuWrapper :permalink="permalink" :xml-url="xmlUrl" />
</template>
