<script setup lang="ts">
import ActionMenu from "~/components/documents/actionMenu/ActionMenu.vue";
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
  <ActionMenu :permalink="permalink" :xml-url="xmlUrl" />
</template>
