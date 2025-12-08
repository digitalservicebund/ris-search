<script setup lang="ts">
import { RisSingleAccordion } from "@digitalservicebund/ris-ui/components";

const props = defineProps<{ html: string; textLength?: number }>();

const threshold = 3 * 60;

const wrapFootnote = computed(
  () => props.textLength && props.textLength > threshold,
);
</script>

<template>
  <div class="dokumentenkopf-fussnoten max-w-prose">
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
