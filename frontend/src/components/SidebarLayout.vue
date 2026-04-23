<script setup lang="ts">
defineSlots<{
  content(): unknown;
  sidebar(): unknown;
}>();
</script>

<template>
  <div
    class="grid grid-cols-1 grid-rows-1 items-start gap-16 md:grid-cols-3 print:block"
  >
    <div class="col-span-2 pt-32 pb-32 md:pb-56"><slot name="content" /></div>

    <div
      v-if="$slots.sidebar"
      class="col-span-1 row-start-1 md:row-start-auto md:h-full md:border-l md:border-gray-400 print:hidden"
    >
      <div
        class="overflow-auto md:sticky md:top-0 md:max-h-[calc(min(100%,100dvh))]"
      >
        <slot name="sidebar" />
      </div>
    </div>
  </div>
</template>
