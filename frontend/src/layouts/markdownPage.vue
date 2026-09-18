<script setup lang="ts">
import BreadcrumbPageLayout from "./breadcrumbPage.vue";

defineProps<{
  staticContent: string;
}>();
</script>

<template>
  <BreadcrumbPageLayout>
    <template #breadcrumb>
      <slot name="breadcrumb" />
    </template>

    <div class="content-wrapper pb-32 md:pb-56">
      <MDC :value="staticContent" class="markdown-content content-grid"></MDC>
    </div>
  </BreadcrumbPageLayout>
</template>

<style>
@reference "~/assets/main.css";

/* This uses native CSS scoping rather than Vue's own scoping solution to:

- Avoid having to write :deep all the time
- Having the ability to limit where typography styles are applied in order to make
  it possible for custom components used in Markdown to be unaffected by the typography
  styles defined here.

see: https://developer.mozilla.org/en-US/docs/Web/CSS/Reference/At-rules/@scope */
@scope (.markdown-content) to (.no-markdown-content) {
  :scope {
    @apply typo-body-regular;
  }

  :is(h2, h3, h4, h5, h6, p, ol, ul) {
    @apply content-grid-textblock col-start-1;
  }

  h1 {
    @apply typo-headline1-bold col-span-12 pb-8 hyphens-auto md:hyphens-none;
  }

  h2 {
    @apply ris-heading3-regular md:ris-heading2-regular pt-24 pb-8;
  }

  h3 {
    @apply ris-subhead-regular md:ris-heading3-regular pt-16 pb-8;
  }

  h4 {
    @apply ris-body1-bold sm:ris-subhead-bold py-8;
  }

  h5 {
    @apply typo-body-bold py-8;
  }

  a {
    @apply typo-link1-regular;
  }

  :is(p, ul, ol, li) {
    @apply pb-16;
  }

  :is(ul, ol) {
    @apply list-outside list-disc pl-24;
  }
}

.no-markdown-content {
  @apply content-grid-textblock col-start-1;
}
</style>
