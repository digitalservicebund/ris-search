<script setup lang="ts">
// The default layout provides a container, but uses the base layout, which
// provides header and footer elements. Pages that need to use full-width
// ("hero style") elements should use the base layout directly.

import BaseLayout from "./base.vue";
</script>

<template>
  <BaseLayout>
    <div class="container">
      <slot />
    </div>
  </BaseLayout>
</template>
