<script setup lang="ts">
import { ProgressSpinner } from "primevue";
</script>

<template>
  <div
    class="delay-visibility m-y-10 top-0 right-0 bottom-0 left-0 grid justify-items-center gap-y-8 p-10"
  >
    <ProgressSpinner />
    <slot></slot>
  </div>
</template>

<style scoped>
.delay-visibility {
  animation: 1s fadeIn;
  animation-fill-mode: forwards;

  visibility: hidden;
}
@keyframes fadeIn {
  0% {
    opacity: 0;
  }
  50% {
    opacity: 0;
  }
  100% {
    visibility: visible;
    opacity: 1;
  }
}
</style>
