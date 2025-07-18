<script setup lang="ts">
const props = defineProps<{ html?: string; textLength?: number }>();

const threshold = 3 * 60;
const isLongFootnote = computed(
  () => props.textLength && props.textLength > threshold,
);
const showFootnote = ref(!isLongFootnote.value);
</script>

<template>
  <div v-if="isLongFootnote" class="mt-8 print:hidden">
    <ExpandButton v-model="showFootnote">{{
      showFootnote ? "Fußnote ausblenden" : "Fußnote anzeigen"
    }}</ExpandButton>
  </div>
  <div v-if="props.html" class="dokumentenkopf-fussnoten space-y-12">
    <div
      :data-show="showFootnote"
      class="hidden data-[show=true]:block print:block"
      v-html="props.html"
    />
  </div>
</template>
