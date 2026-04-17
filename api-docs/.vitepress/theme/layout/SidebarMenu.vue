<script setup lang="ts">
import type {NavItem} from "../types";
import MenuItem from "../components/MenuItem.vue";

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
    :item="item"
    :key="item.link"
    root
    @click="() => onClick(item)"
  />
</template>
