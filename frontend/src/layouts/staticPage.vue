<script setup lang="ts">
import DefaultLayout from "./default.vue";

const breadcrumbHeadingId = useId();
</script>

<template>
  <DefaultLayout>
    <nav :aria-labelledby="breadcrumbHeadingId">
      <h2 :id="breadcrumbHeadingId" class="sr-only">Brotkrumen</h2>
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
  </DefaultLayout>
</template>
