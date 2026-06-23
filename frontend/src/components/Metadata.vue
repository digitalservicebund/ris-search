<script setup lang="ts">
export interface MetadataItem {
  label: string;
  value?: string;
}

const { items } = defineProps<{ items: MetadataItem[] }>();
const emptyValuePlaceholder = "—"; // Note: this is an 'em dash'
</script>

<template>
  <dl data-testid="metadata-list" class="flex flex-row flex-wrap gap-24">
    <div v-for="item in items" :key="item.label" class="flex flex-col gap-4">
      <dt class="typo-label1-regular text-gray-900">
        {{ item.label }}
      </dt>
      <dd class="typo-label1-bold">
        {{ item.value || emptyValuePlaceholder }}
      </dd>
    </div>
  </dl>
</template>
