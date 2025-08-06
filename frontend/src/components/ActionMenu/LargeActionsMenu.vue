<script setup lang="ts">
import Button from "primevue/button";
import type { ActionMenuItem } from "~/components/ActionMenu/ActionsMenu.vue";

const { actions } = defineProps<{ actions: ActionMenuItem[] }>();
</script>

<template></template>
