<script setup lang="ts">
import { useToast } from "primevue/usetoast";
import ActionMenu from "~/components/documents/actionMenu/ActionMenu.vue";
import { createActionMenuItems } from "~/utils/actionMenu";

// NOTE: This component is just to a allow for an easier transition phase
// while refactoring the ActionMenu. This will not be needed anymore after the refactoring.

export type ActionMenuProps = {
  link?: {
    url: string;
    label: string;
  };
  permalink: {
    url: string;
    label: string;
    disabled?: boolean;
  };
  xmlUrl?: string;
  translationUrl?: string;
};

const props = defineProps<ActionMenuProps>();
const toastService = useToast();

async function copyUrlCommand(url: string) {
  await navigator.clipboard.writeText(url);
  toastService.add({
    severity: "success",
    summary: "Kopiert!",
    life: 3000,
    closable: false,
  });
}

async function navigationToXml(xmlUrl: string) {
  await navigateTo(xmlUrl, { external: true });
}

const actions = createActionMenuItems(props, copyUrlCommand, navigationToXml);
</script>

<template>
  <ActionMenu :actions="actions"></ActionMenu>
</template>
