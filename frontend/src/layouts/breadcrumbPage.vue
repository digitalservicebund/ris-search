<script setup lang="ts">
import DefaultLayout from "./default.vue";
</script>

<template>
  <DefaultLayout>
    <!-- Target spacing for breadcrumbs is 16/24px, but the breadcrumbs define
   8px extra padding to ensure the outline is visible. -->
    <div class="mt-8 md:mt-16">
      <div
        class="wrapper mb-8 has-data-[breadcrumbs-adjust=actionmenu]:-mt-4 md:mb-16"
      >
        <slot name="breadcrumb" />
      </div>
      <slot />
    </div>
  </DefaultLayout>
</template>
