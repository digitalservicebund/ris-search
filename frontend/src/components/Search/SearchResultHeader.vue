<script setup lang="ts">
export interface SearchResultHeaderItem {
  isMarkup?: boolean;
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
      <template v-if="item.isMarkup">
        <span v-html="item.value" />
      </template>
      <template v-else>
        <span>
          {{ item.value }}
        </span>
      </template>
    </template>
    <slot name="trailing" />
  </p>
</template>
