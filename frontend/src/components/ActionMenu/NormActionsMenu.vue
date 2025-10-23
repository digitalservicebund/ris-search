<script setup lang="ts">
import ActionsMenu from "~/components/ActionMenu/ActionsMenu.vue";
import { useBackendURL } from "~/composables/useBackendURL";
import type { LegislationWork } from "~/types";
import { getManifestationUrl } from "~/utils/norm";

const { metadata } = defineProps<{ metadata: LegislationWork | undefined }>();

const backendURL = useBackendURL();
const xmlUrl = computed(() =>
  getManifestationUrl(metadata, backendURL, "application/xml"),
);

const workUrl = computed(() => {
  if (!import.meta.client || !metadata) return undefined;
  const href = globalThis.location.href;
  const workEli = metadata.legislationIdentifier;
  return href.replace(/eli.+$/, workEli);
});

const link = computed(() => {
  if (workUrl.value) {
    return {
      url: workUrl.value,
      label: "Link zur jeweils gültigen Fassung",
    };
  }
  return undefined;
});

const permalink = {
  url: globalThis?.location.href,
  label: "Permalink zu dieser Fassung",
};
</script>

<template>
  <ActionsMenu :link="link" :permalink="permalink" :xml-url="xmlUrl" />
</template>
