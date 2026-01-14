<script setup lang="ts">
const { href } = defineProps<{
  href: string;
  target?: string;
}>();

const isExternal = computed(() => href.match(/^https?:\/\//));
</script>

<template>
  <ExternalLink v-if="isExternal" :url="href">
    <slot />
  </ExternalLink>

  <NuxtLink v-else :href :target>
    <slot />
  </NuxtLink>
</template>
