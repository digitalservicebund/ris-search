<script setup lang="ts">
import BaseLayout from "./base.vue";
</script>

<template>
  <BaseLayout>
    <div class="border-b border-b-gray-400 pt-32 lg:pt-64">
      <slot />
    </div>
  </BaseLayout>
</template>
