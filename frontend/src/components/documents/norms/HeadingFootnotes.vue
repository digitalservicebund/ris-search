<script setup lang="ts">
const { html, text } = defineProps<{
  html: string;
  /** Text of the footnotes, used to decide whether they are long */
  text?: string;
}>();

const threshold = 3 * 60;

const wrapFootnote = computed(() => (text?.length ?? 0) > threshold);
</script>

<template>
  <div class="dokumentenkopf-fussnoten">
    <UiAccordion
      v-if="wrapFootnote"
      class="print:hidden"
      header-expanded="Fußnote ausblenden"
      header-collapsed="Fußnote anzeigen"
    >
      <div v-html="html"></div>
    </UiAccordion>

    <div
      :data-show="!wrapFootnote"
      class="hidden data-[show=true]:block print:block"
      v-html="html"
    ></div>
  </div>
</template>

<style scoped>
@reference "~/assets/main.css";

.dokumentenkopf-fussnoten {
  :deep(.fussnoten) {
    @apply ris-label3-regular md:ris-label2-regular my-0 list-none space-y-12 pl-0 text-gray-900;
  }

  :deep(.fussnoten:before) {
    @apply hidden;
  }

  :deep(.rueckverweis) {
    @apply hidden;
  }

  :deep(.fussnote) {
    display: flex;
  }

  :deep(.fussnote .marker) {
    @apply min-w-32 align-super text-sm;
  }
}
</style>
