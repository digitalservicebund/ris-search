<script setup lang="ts">
import DefaultLayout from "./default.vue";

defineProps<{
  staticContent: string;
}>();

const breadcrumbHeadingId = useId();
</script>

<template>
  <DefaultLayout>
    <nav :aria-labelledby="breadcrumbHeadingId">
      <h2 :id="breadcrumbHeadingId" class="sr-only">Brotkrumen</h2>
      <slot name="breadcrumb" />
    </nav>

    <div class="prose">
      <MDC :value="staticContent"></MDC>
    </div>
  </DefaultLayout>
</template>

<style scoped>
@reference "~/assets/main.css";

.prose {
  @apply max-w-prose;
}

.prose :deep(h1) {
  @apply ris-heading1-regular mt-24 mb-48 hyphens-auto max-sm:leading-48;
}

.prose :deep(h2) {
  @apply ris-heading2-regular mt-64 mb-16;
}

.prose :deep(a) {
  @apply ris-link1-regular;
}

.prose :deep(:is(p, ul, ol, li)) {
  @apply my-16;
}

.prose :deep(:is(ul, ol)) {
  @apply list-outside list-disc pl-24;
}
</style>
