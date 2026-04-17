<script setup lang="ts">
import { type OutlineItem } from "../composables/outline";

const props = defineProps<{
  items: OutlineItem[];
  root?: boolean;
}>();
</script>

<template>
  <ul :class="props.root ? '' : 'pl-8'">
    <li v-for="item in props.items">
      <a
        class="block py-2 px-8 my-2 no-underline text-neutral-primary rounded hover:bg-blue-100 focus-within:bg-blue-100"
        :href="item.link"
        :title="item.title"
      >
        {{ item.title }}
      </a>
      <template v-if="item.children?.length">
        <TableOfContentsItems :items="item.children" />
      </template>
    </li>
  </ul>
</template>
