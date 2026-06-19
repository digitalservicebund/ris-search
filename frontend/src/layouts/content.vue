<script setup lang="ts">
import BaseLayout from "./base.vue";
</script>

<template>
  <BaseLayout>
    <div class="mt-16 md:mt-24">
      <div class="wrapper mb-16 md:mb-24">
        <slot name="breadcrumb" />
      </div>
      <slot />
    </div>
  </BaseLayout>
</template>
