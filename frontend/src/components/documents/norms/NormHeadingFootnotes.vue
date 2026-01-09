<script setup lang="ts">
import { RisSingleAccordion } from "@digitalservicebund/ris-ui/components";

const props = defineProps<{ html: string; textLength?: number }>();

const threshold = 3 * 60;

const wrapFootnote = computed(
  () => props.textLength && props.textLength > threshold,
);
</script>

<template>
  <div class="dokumentenkopf-fussnoten">
    <RisSingleAccordion
      v-if="wrapFootnote"
      class="print:hidden"
      header-expanded="Fußnote ausblenden"
      header-collapsed="Fußnote anzeigen"
    >
      <div v-html="props.html"></div>
    </RisSingleAccordion>

    <div
      :data-show="!wrapFootnote"
      class="hidden data-[show=true]:block print:block"
      v-html="props.html"
    ></div>
  </div>
</template>

<style scoped>
@reference "~/assets/main.css";

.dokumentenkopf-fussnoten {
  :deep(.fussnoten) {
    @apply ris-body2-regular my-0 list-none space-y-12 pl-0 text-gray-900;
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
