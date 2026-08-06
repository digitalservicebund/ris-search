<script setup lang="ts"></script>

<template>
  <div
    aria-label="Ladestatus"
    role="status"
    class="mx-auto inline-block h-28 w-28 animate-spin text-blue-800"
  >
    <svg viewBox="25 25 50 50" class="h-full w-full">
      <circle
        cx="50"
        cy="50"
        r="20"
        class="fill-transparent stroke-current stroke-[4px] [stroke-dasharray:200] [stroke-dashoffset:100]"
      />
    </svg>
  </div>
</template>
