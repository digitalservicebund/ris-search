<script setup lang="ts">
export interface SearchResultHeaderItem {
  isMarkup?: boolean;
  id?: string;
  value: string;
}

const { items, secondaryItem } = defineProps<{
  documentType?: Omit<SearchResultHeaderItem, "isMarkup">;
  items: SearchResultHeaderItem[];
  secondaryItem?: SearchResultHeaderItem;
}>();

const itemsWithContent = computed(() => items.filter((i) => !!i.value));
</script>

<template>
  <div class="flex items-center gap-8">
    <div>
      <p class="typo-label2-regular content-center space-x-12 hyphens-auto">
        <span v-if="documentType" class="font-bold!" :id="documentType.id">{{
          documentType.value
        }}</span>
        <template v-for="item in itemsWithContent" :key="item.value">
          <span v-if="item.isMarkup" :id="item.id" v-html="item.value" />
          <span v-else :id="item.id">{{ item.value }}</span>
        </template>
      </p>
      <p
        v-if="secondaryItem?.value"
        class="typo-label1-regular mt-8 hyphens-auto"
      >
        <span v-if="secondaryItem.isMarkup" v-html="secondaryItem.value" />
        <span v-else>{{ secondaryItem.value }}</span>
      </p>
    </div>

    <slot name="trailing" />
  </div>
</template>
