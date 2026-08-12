<script setup lang="ts">
import { BadgeColor } from "~/components/ui/Badge.vue";

export type MetadataItemBadge = {
  type: "badge";
  label: string;
  values: string[];
};

export type MetadataItemText = {
  type: "text";
  label: string;
  value?: string;
};

export type MetadataItem = MetadataItemText | MetadataItemBadge;

const { items } = defineProps<{ items: MetadataItem[] }>();
const emptyValuePlaceholder = "—"; // Note: this is an 'em dash'
</script>

<template>
  <dl data-testid="metadata-list" class="flex flex-row flex-wrap gap-24">
    <div v-for="item in items" :key="item.label" class="flex flex-col gap-4">
      <dt class="typo-label1-regular text-gray-900">
        {{ item.label }}
      </dt>
      <dd v-if="item.type === 'badge' && item.values.length > 0">
        <div class="flex flex-wrap gap-4">
          <UiBadge
            v-for="value in item.values"
            :key="value"
            :color="BadgeColor.GRAY"
            :label="value"
          ></UiBadge>
        </div>
      </dd>
      <dd
        v-else-if="item.type === 'text' && item.value"
        class="typo-label1-bold"
      >
        {{ item.value }}
      </dd>
      <dd v-else class="typo-label1-bold">
        {{ emptyValuePlaceholder }}
      </dd>
    </div>
  </dl>
</template>
