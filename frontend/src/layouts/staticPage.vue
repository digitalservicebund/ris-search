<script setup lang="ts">
import DefaultLayout from "./default.vue";
</script>

<template>
  <DefaultLayout>
    <div class="py-32 lg:py-64">
      <nav aria-labelledby="breadcrumb-heading">
        <h2 id="breadcrumb-heading" class="sr-only">Brotkrumen</h2>
        <slot name="breadcrumb" />
      </nav>

      <h1
        class="ris-heading1-regular inline-block max-w-prose pt-24 pb-48 hyphens-auto max-sm:leading-48"
      >
        <slot name="pageTitle"></slot>
      </h1>

      <div class="max-w-prose">
        <slot />
      </div>
    </div>
  </DefaultLayout>
</template>
