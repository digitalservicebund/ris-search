<script setup lang="ts">
import DefaultLayout from "./default.vue";
</script>

<template>
  <DefaultLayout>
    <div class="mt-16 md:mt-24">
      <div class="wrapper mb-16 md:mb-24">
        <slot name="breadcrumb" />
      </div>
      <slot />
    </div>
  </DefaultLayout>
</template>
