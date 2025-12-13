<script setup lang="ts">
import type { NavItem } from "./MenuItem.vue";
import MenuItem from "./MenuItem.vue";

defineProps<{
  model: NavItem[];
}>();

const emit = defineEmits(["selectLeaf"]);

const onClick = (item: NavItem) => {
  if (!item.items?.length) {
    emit("selectLeaf");
  }
};
</script>

<template>
  <MenuItem
    v-for="item in model"
    :key="item.link"
    :item="item"
    root
    @click="() => onClick(item)"
  />
</template>
