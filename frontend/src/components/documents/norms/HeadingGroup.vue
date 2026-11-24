<script setup lang="ts">
import type { LegislationExpression } from "~/types/api";

const { html, metadata } = defineProps<{
  html?: NormHtml;
  metadata: LegislationExpression;
}>();

const normTitle = computed(() => getNormTitle(metadata));
</script>

<template>
  <div class="dokumentenkopf">
    <hgroup>
      <!-- only show the alternate name as secondary if it isn't already being used as the main title -->
      <p
        v-if="html?.parts.heading"
        class="word-wrap typo-headline3-regular mb-8 wrap-break-word hyphens-auto"
      >
        {{ metadata.alternateName }}
      </p>

      <div
        v-if="html?.parts.heading"
        class="wrap-break-word hyphens-auto"
        v-html="html.parts.heading"
      ></div>
      <div v-else class="titel wrap-break-word">
        {{ normTitle }}
      </div>
    </hgroup>

    <div class="content-grid">
      <DocumentsNormsHeadingFootnotes
        v-if="html?.parts.headingAuthorialNotes"
        class="content-grid-textblock mt-8"
        :html="html.parts.headingAuthorialNotes"
        :text-length="html.text.headingAuthorialNotesLength"
      />
    </div>
  </div>
</template>

<style scoped>
@reference "~/assets/main.css";

.dokumentenkopf {
  :deep(.titel) {
    @apply typo-headline1-bold hyphens-auto;
  }
}
</style>
