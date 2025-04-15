<script setup lang="ts">
import type { NavItem } from "./AppNavItem.vue";

const props = defineProps<{ item: NavItem }>();
const item = props.item;
</script>

<template>
  <li>
    <AppNavItem :item="item" />
  </li>
</template>
