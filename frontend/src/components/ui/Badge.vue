<script setup lang="ts">
defineProps<{
  label: string;
  color: BadgeColor;
}>();
</script>

<script lang="ts">
export enum BadgeColor {
  BLUE,
  GREEN,
  YELLOW,
  RED,
  GRAY,
}
</script>

<template>
  <span
    class="typo-label2-bold inline-block px-8 py-4"
    :class="{
      'flex-none border border-green-400 bg-green-100 text-green-800':
        color === BadgeColor.GREEN,
      'flex-none border border-yellow-600 bg-yellow-200 text-orange-800':
        color === BadgeColor.YELLOW,
      'flex-none border border-blue-500 bg-blue-200 text-blue-800':
        color === BadgeColor.BLUE,
      'flex-none border border-red-400 bg-red-200 text-red-800':
        color === BadgeColor.RED,
      'ris-label3-bold sm:ris-label2-bold 2xl:ris-label1-bold rounded-xs border border-gray-400 bg-gray-100 hyphens-auto':
        color === BadgeColor.GRAY,
    }"
  >
    {{ label }}
  </span>
</template>
