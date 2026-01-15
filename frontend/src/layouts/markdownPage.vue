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
  :scope {
    @apply max-w-prose;
  }

  h1 {
    @apply ris-heading1-regular pt-32 pb-24 hyphens-auto max-sm:leading-48;
  }

  h2 {
    @apply ris-heading2-regular py-24;
  }

  h3 {
    @apply ris-heading3-regular py-16;
  }

  h4 {
    @apply ris-subhead-bold py-8;
  }

  h5 {
    @apply ris-body1-bold pb-8;
  }

  a {
    @apply ris-link1-regular;
  }

  :is(p, ul, ol, li) {
    @apply pb-16;
  }

  :is(ul, ol) {
    @apply list-outside list-disc pl-24;
  }
}
</style>
