<script setup lang="ts">
export interface SearchResultHeaderItem {
  isMarkup?: boolean;
  id?: string;
  value: string;
}

const { icon, items } = defineProps<{
  icon: Component;
  items: SearchResultHeaderItem[];
}>();
</script>

<template>
  <p class="ris-label2-regular flex flex-row flex-wrap items-center gap-8">
    <component :is="icon" class="h-16 w-16 text-gray-900" />

    <template v-for="item in items" :key="item.value">
      <span v-if="item.isMarkup" :id="item.id" v-html="item.value" />
      <span v-else :id="item.id">{{ item.value }}</span>
    </template>

    <slot name="trailing" />
  </p>
</template>
