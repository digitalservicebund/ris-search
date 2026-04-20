<script setup lang="ts">
import { onContentUpdated } from "vitepress";
import { shallowRef } from "vue";
import { useData } from "../composables/data";
import { getHeadings, type OutlineItem } from "../composables/outline";
import TableOfContentsItems from "./TableOfContentsItems.vue";

const { frontmatter, theme } = useData();
const items = shallowRef<OutlineItem[]>([]);

onContentUpdated(() => {
  items.value = getHeadings(frontmatter.value.outline ?? theme.value.outline);
});
</script>

<template>
  <nav
    class="border border-neutral-tertiary  text-neutral-secondary rounded py-16 px-8 flex-grow text-sm text-gray-800 lg:max-w-xs"
    v-if="items.length > 0"
  >
    <div class="px-8 font-medium">On this page</div>
    <TableOfContentsItems :items="items" root />
  </nav>
</template>
