<script setup lang="ts">
import Button from "primevue/button";
import Menu, { type MenuMethods } from "primevue/menu";
import type { ActionMenuItem } from "~/components/ActionMenu/ActionsMenu.vue";
import MdiDotsVertical from "~icons/mdi/dots-vertical";

const { actions } = defineProps<{ actions: ActionMenuItem[] }>();

defineExpose({ toggle });
</script>

<template></template>
