<script setup lang="ts">
import ActionsMenu from "~/components/ActionMenu/ActionsMenu.vue";
import type { CaseLaw } from "~/types";
import { getEncodingURL } from "~/utils/caseLawUtils";

const { caseLaw } = defineProps<{ caseLaw: CaseLaw | null }>();

const backendURL = useBackendURL();
const xmlUrl = computed(() =>
  getEncodingURL(caseLaw, backendURL, "application/xml"),
);

const permalink = {
  label: "Link kopieren",
  url: "",
  disabled: true,
};
</script>

<template>
  <ActionsMenu :permalink="permalink" :xml-url="xmlUrl" />
</template>
