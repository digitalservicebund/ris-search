<script setup lang="ts">
import type { BadgeColor } from "~/components/ui/Badge.vue";

export interface TextHeaderItem {
  type: "text";
  value: string;
  id?: string;
  isMarkup?: boolean;
}

export interface BadgeHeaderItem {
  type: "badge";
  value: string;
  color: BadgeColor;
  isMarkup?: boolean;
  id?: string;
  class?: string;
}

export type SearchResultHeaderItem = TextHeaderItem | BadgeHeaderItem;

const { items, secondaryItem } = defineProps<{
  documentType?: Omit<TextHeaderItem, "isMarkup">;
  items: SearchResultHeaderItem[];
  secondaryItem?: Omit<TextHeaderItem, "id" | "isMarkup">;
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
          <span v-if="item.type === 'text' && !item.isMarkup" :id="item.id">{{
            item.value
          }}</span>
          <span
            v-if="item.type === 'text' && item.isMarkup"
            :id="item.id"
            v-html="item.value"
          />
          <UiBadge
            v-if="item.type === 'badge'"
            :is-markup="item.isMarkup"
            :class="item.class"
            :variant="'medium'"
            :label="item.value"
            :color="item.color"
            :id="item.id"
          />
        </template>
      </p>
      <p
        v-if="secondaryItem?.value"
        class="typo-label1-regular mt-8 hyphens-auto"
      >
        <span>{{ secondaryItem.value }}</span>
      </p>
    </div>
  </div>
</template>
