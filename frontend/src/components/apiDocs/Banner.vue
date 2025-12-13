<script setup lang="ts">
import MaterialSymbolsWarningOutline from "~icons/material-symbols/warning-outline";
</script>
<template>
  <div
    class="ris-body2-regular flex border-b border-yellow-700 bg-yellow-200 px-8 py-12 text-black sm:px-8 lg:px-16 dark:border-transparent dark:bg-orange-900 dark:text-white"
  >
    <div class="mx-auto flex gap-16">
      <MaterialSymbolsWarningOutline class="shrink-0" />
      <div>
        <p>
          <span class="ris-body2-bold">This is a trial service.</span>
          This API may be subject to changes and improvements as we learn from
          usage.
        </p>
        <p>
          This means that you may use this software and build applications that
          utilise it. However as we learn from feedback we may make changes to
          the software.
        </p>
      </div>
    </div>
  </div>
</template>
