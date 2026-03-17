<script setup lang="ts" generic="T extends TreeItem">
import type { RouteLocationRaw } from "#vue-router";

export type TreeItem = {
  key: string;
  title: string;
  subtitle?: string;
  to?: RouteLocationRaw;
  children?: TreeItem[];
};

const props = defineProps<{
  items: T[];
  heading?: string;
  subheading?: string;
  label?: string;
}>();

const expandedKeys = defineModel<string[]>("expandedKeys", {
  default: () => [],
});

const selected = defineModel<string>("selected");

defineEmits<{
  click: [item: T];
}>();

const headingId = useId();
</script>

<template>
  <div>
    <div
      v-if="heading"
      class="space-y-4 border-b border-b-gray-400 px-[1.375rem] py-16"
    >
      <h2 :id="headingId" class="ris-heading3-bold">{{ heading }}</h2>
      <p v-if="subheading" class="ris-label1-regular">{{ subheading }}</p>
    </div>

    <ul
      role="tree"
      :aria-label="label"
      :aria-labelledby="heading ? headingId : undefined"
    >
      <TreeViewItem
        v-for="item in items"
        :key="item.key"
        :item="item"
        v-model:expanded-keys="expandedKeys"
        v-model:selected="selected"
        @click="$emit('click', $event as T)"
      />
    </ul>
  </div>
</template>
