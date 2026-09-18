<script setup lang="ts">
import DefaultLayout from "./default.vue";
</script>

<template>
  <DefaultLayout>
    <!-- Breadcrumbs add an extra 8px because they have padding (needed to
    ensure the outline isn't cropped) -->
    <div class="mt-8 md:mt-16">
      <div
        class="content-wrapper mb-16 md:mb-24 md:has-data-[breadcrumbs-adjust=actionmenu]:-mt-4 md:has-data-[breadcrumbs-adjust=actionmenu]:mb-20"
      >
        <slot name="breadcrumb" />
      </div>
      <slot />
    </div>
  </DefaultLayout>
</template>
