<script setup lang="ts">
export interface NavItem {
  text: string;
  link: string;
  icon?: Component;
}

defineProps<{ item: NavItem }>();
</script>

<template>
  <NuxtLink
    class="flex flex-row items-center gap-4 px-12 py-8 text-xl hover:underline active:underline"
    active-class="underline"
    :to="item.link"
  >
    <component :is="item.icon" class="h-20" />
    {{ item.text }}
  </NuxtLink>
</template>
