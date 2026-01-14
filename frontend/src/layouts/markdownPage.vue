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

<style>
@reference "~/assets/main.css";

/* This uses native CSS scoping rather than Vue's own scoping solution to:

- Avoid having to write :deep all the time
- Having the ability to limit where typography styles are applied in order to make
  it possible for custom components used in Markdown to be unaffected by the typography
  styles defined here.

see: https://developer.mozilla.org/en-US/docs/Web/CSS/Reference/At-rules/@scope */
@scope (.prose) to (.no-prose) {
  @apply max-w-prose;

  h1 {
    @apply ris-heading1-regular mt-24 mb-48 hyphens-auto max-sm:leading-48;
  }

  h2 {
    @apply ris-heading2-regular mt-64 mb-16;
  }

  h3 {
    @apply ris-heading3-regular my-24;
  }

  h4 {
    @apply ris-subhead-bold mt-32 mb-8;
  }

  a {
    @apply ris-link1-regular;
  }

  :is(p, ul, ol, li) {
    @apply my-16;
  }

  :is(ul, ol) {
    @apply list-outside list-disc pl-24;
  }
}
</style>
