<script setup lang="ts">
import type { LegislationExpression } from "~/types/api";

const props = defineProps<{
  htmlParts?: NormContent["htmlParts"];
  metadata: LegislationExpression;
}>();

// only show the alternate name as secondary if it isn't already being used as the main title
const hasHeading = computed(() => !!props.htmlParts?.heading);
const normTitle = computed(() => getNormTitle(props.metadata));
</script>

<template>
  <div class="dokumentenkopf">
    <hgroup>
      <p
        v-if="hasHeading"
        class="word-wrap typo-headline3-regular mb-8 wrap-break-word hyphens-auto"
      >
        {{ metadata.alternateName }}
      </p>

      <client-only>
        <ExpandableText :length="6">
          <div
            v-if="hasHeading"
            class="wrap-break-word hyphens-auto"
            v-html="props.htmlParts?.heading"
          />
          <div v-else class="titel wrap-break-word">
            {{ normTitle }}
          </div>
        </ExpandableText>

        <template #fallback>
          <div
            v-if="props.htmlParts?.heading"
            class="wrap-break-word hyphens-auto"
            v-html="props.htmlParts.heading"
          ></div>
          <div v-else class="titel wrap-break-word">
            {{ normTitle }}
          </div>
        </template>
      </client-only>
    </hgroup>

    <div class="content-grid">
      <DocumentsNormsHeadingFootnotes
        v-if="props.htmlParts?.headingAuthorialNotes"
        class="content-grid-textblock mt-8"
        :html="props.htmlParts.headingAuthorialNotes"
        :text-length="props.htmlParts?.headingAuthorialNotesLength"
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
