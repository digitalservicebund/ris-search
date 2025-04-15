<script setup lang="ts">
const props = defineProps<{ html?: string; textLength?: number }>();

const threshold = 3 * 60;
const isLongFootnote = computed(
  () => props.textLength && props.textLength > threshold,
);
const showFootnote = ref(!isLongFootnote.value);
</script>

<template>
  <div v-if="props.html" class="dokumentenkopf-fussnoten space-y-12">
    <div v-if="showFootnote" v-html="props.html" />
    <div v-if="isLongFootnote">
      <ExpandButton v-model="showFootnote">{{
        showFootnote ? "Fußnote ausblenden" : "Fußnote anzeigen"
      }}</ExpandButton>
    </div>
  </div>
</template>
