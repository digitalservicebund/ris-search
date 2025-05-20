<script setup lang="ts">
import type { VNodeChild } from "vue";

defineSlots<{
  content(): VNodeChild;
  sidebar(): VNodeChild;
}>();
</script>

<template>
  <div
    class="grid grid-cols-1 grid-rows-1 items-start gap-y-24 lg:grid-cols-3 lg:gap-x-48 print:block"
  >
    <div class="col-span-2"><slot name="content" /></div>
    <div
      class="col-span-1 row-start-1 lg:sticky lg:top-0 lg:row-start-auto lg:h-[calc(min(100%,100vh))] print:hidden"
    >
      <slot name="sidebar" />
    </div>
  </div>
</template>
