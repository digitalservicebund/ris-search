<script setup lang="ts">
import DefaultLayout from "./default.vue";

defineProps<{
  staticContent: string;
}>();
</script>

<template>
  <DefaultLayout>
    <div>
      <slot name="breadcrumb" />
    </div>

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
    @apply ris-body2-regular sm:ris-body1-regular max-w-prose 2xl:text-[1.25rem];
  }

  h1 {
    @apply ris-heading3-bold md:ris-heading2-bold pt-24 pb-8 hyphens-auto max-sm:leading-48 md:hyphens-none 2xl:text-[2.5rem];
  }

  h2 {
    @apply ris-heading3-regular pt-24 pb-8;
  }

  h3 {
    @apply ris-subhead-regular 2xl:ris-heading3-regular pt-16 pb-8;
  }

  h4 {
    @apply ris-subhead-regular sm:ris-body1-bold 2xl:ris-subhead-bold py-8;
  }

  h5 {
    @apply ris-body2-bold sm:ris-body1-bold py-8 2xl:text-[1.25rem];
  }

  a {
    @apply ris-link2-regular sm:ris-link1-regular 2xl:text-[1.25rem];
  }

  :is(p, ul, ol, li) {
    @apply pb-16;
  }

  :is(ul, ol) {
    @apply list-outside list-disc pl-24;
  }
}
</style>
