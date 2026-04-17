<script setup lang="ts">
import MaterialSymbolsWarningOutline from "~icons/material-symbols/warning-outline";
</script>
<template>
  <div class="ris-body2-regular flex bg-yellow-200 dark:bg-orange-900 border-b border-yellow-700 px-8 lg:px-16 py-12 sm:px-8 text-black dark:text-white dark:border-transparent">
    <div class="mx-auto flex gap-16">
      <MaterialSymbolsWarningOutline class="shrink-0" />
      <div>
        <p>
          <span class="ris-body2-bold"
          >This is a trial service.</span
          >
          This API may be subject to changes and improvements as we learn from usage.
        </p>
        <p>
          This means that you may use this software and build applications that utilise it. However as we learn from feedback we may make changes to the software.
        </p>
      </div>
    </div>
  </div>
</template>
