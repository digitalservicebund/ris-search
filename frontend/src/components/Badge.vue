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
}
</script>

<template>
  <div
    class="ris-label3-bold inline-block px-8 py-4"
    :class="{
      'border border-green-200 bg-green-100 text-green-800':
        color === BadgeColor.GREEN,
      'border border-yellow-300 bg-yellow-200 text-[#634B18]':
        color === BadgeColor.YELLOW,
      'border border-blue-500 bg-blue-200 text-blue-800':
        color === BadgeColor.BLUE,
      'border border-red-400 bg-red-200 text-red-900': color === BadgeColor.RED,
    }"
  >
    {{ label }}
  </div>
</template>
