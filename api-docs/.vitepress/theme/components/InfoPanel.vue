<script setup lang="ts">
import MdiWarningCircleOutline from '~icons/mdi/warning-circle-outline';
</script>
<template>
  <div class="ris-body2-regular flex bg-blue-100 dark:bg-blue-900 border-l-4 border-blue-800 dark:border-blue-600 dark:border-transparent px-8 lg:px-16 py-12 sm:px-8">
    <div class="mx-auto flex gap-16">
      <MdiWarningCircleOutline class="text-blue-800 dark:text-blue-100 shrink-0" />
      <div>
        <slot/>
      </div>
    </div>
  </div>
</template>
