<script setup lang="ts">
import BaseLayout from "./base.vue";
</script>

<template>
  <BaseLayout>
    <div class="pt-32 lg:pt-64">
      <slot />
    </div>
  </BaseLayout>
</template>
