<script setup lang="ts">
import type { NormContent } from "~/composables/useNormData";
import type { LegislationExpression } from "~/types/api";
import { getNormTitle } from "~/utils/norm";
import NormHeadingFootnotes from "./NormHeadingFootnotes.vue";
const props = defineProps<{
  htmlParts?: NormContent["htmlParts"];
  metadata: LegislationExpression;
}>();

// show very long titles in smaller font
const heading1MaxLength = 180;
const isLongTitle = computed(
  () => props.metadata.name.length > heading1MaxLength,
);

// only show the alternate name as secondary if it isn't already being used as the main title
const hasHeading = computed(() => !!props.htmlParts?.heading);
const normTitle = computed(() => getNormTitle(props.metadata));
</script>
<template>
  <div class="dokumentenkopf mb-48">
    <hgroup>
      <p
        v-if="hasHeading"
        class="word-wrap typo-headline3-regular mb-12 wrap-break-word hyphens-auto max-md:text-xl"
      >
        {{ metadata.alternateName }}
      </p>

      <client-only>
        <ExpandableText :length="6">
          <div
            v-if="hasHeading"
            :data-longTitle="isLongTitle || null"
            class="wrap-break-word hyphens-auto max-sm:text-2xl"
            v-html="props.htmlParts?.heading"
          />
          <div v-else class="titel wrap-break-word">
            {{ normTitle }}
          </div>
        </ExpandableText>

        <template #fallback>
          <div
            v-if="props.htmlParts?.heading"
            :data-longTitle="isLongTitle || null"
            class="wrap-break-word hyphens-auto max-sm:text-2xl"
            v-html="props.htmlParts.heading"
          ></div>
          <div v-else class="titel wrap-break-word">
            {{ normTitle }}
          </div>
        </template>
      </client-only>
    </hgroup>

    <NormHeadingFootnotes
      v-if="props.htmlParts?.headingAuthorialNotes"
      class="my-48 max-w-prose"
      :html="props.htmlParts.headingAuthorialNotes"
      :text-length="props.htmlParts?.headingAuthorialNotesLength"
    />
  </div>
</template>

<style scoped>
@reference "~/assets/main.css";

.dokumentenkopf {
  :deep(.titel) {
    @apply typo-headline1-bold hyphens-auto max-md:text-2xl;
  }

  :deep(*[data-longTitle] .titel) {
    @apply typo-headline3-bold;
  }
}
</style>
