<script setup lang="ts">
interface Props {
  label: string;
  color: BadgeColor;
}

const props = defineProps<Props>();

const badgeStyle = computed(() => {
  switch (props.color) {
    case BadgeColor.GREEN:
      return "bg-green-100 text-green-800 border-1 border-green-200";
    case BadgeColor.YELLOW:
      return "bg-yellow-100 text-gray-900 border-1 border-yellow-300";
    case BadgeColor.BLUE:
      return "bg-blue-200 text-blue-800 border-1 border-blue-500";
    case BadgeColor.RED:
      return "bg-red-200 text-red-900 border-1 border-red-400";
    default:
      return undefined;
  }
});
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
  <div class="ris-label3-bold inline-block px-8 py-4" :class="badgeStyle">
    {{ label }}
  </div>
</template>
