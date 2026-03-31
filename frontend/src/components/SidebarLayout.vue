<script setup lang="ts">
defineSlots<{
  content(): unknown;
  sidebar(): unknown;
}>();
</script>

<template>
  <div
    class="grid grid-cols-1 grid-rows-1 items-start gap-16 lg:grid-cols-3 print:block"
  >
    <div class="col-span-2 pt-32 pb-32 lg:pb-56"><slot name="content" /></div>

    <div
      v-if="$slots.sidebar"
      class="col-span-1 row-start-1 lg:sticky lg:top-0 lg:row-start-auto lg:h-[calc(min(100%,100dvh))] lg:border-l lg:border-gray-400 print:hidden"
    >
      <slot name="sidebar" />
    </div>
  </div>
</template>
