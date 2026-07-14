<script setup lang="ts">
export interface SearchResultHeaderItem {
  isMarkup?: boolean;
  id?: string;
  value: string;
}

const { icon, items, secondaryItem } = defineProps<{
  icon?: Component;
  items: SearchResultHeaderItem[];
  secondaryItem?: string;
}>();

const itemsWithContent = computed(() => items.filter((i) => !!i.value));
</script>

<template>
  <div class="flex items-center gap-8">
    <div>
      <p class="typo-label2-regular content-center space-x-8 hyphens-auto">
        <span
          v-if="icon"
          class="inline-flex h-lh items-center align-text-bottom"
        >
          <component :is="icon" class="inline-block h-16 w-16 text-gray-900" />
        </span>

        <template v-for="item in itemsWithContent" :key="item.value">
          <span v-if="item.isMarkup" :id="item.id" v-html="item.value" />
          <span v-else :id="item.id">{{ item.value }}</span>
        </template>
      </p>
      <p v-if="secondaryItem" class="typo-label1-regular mt-8 hyphens-auto">
        {{ secondaryItem }}
      </p>
    </div>

    <slot name="trailing" />
  </div>
</template>
