<script setup lang="ts">
import { RisExpandableText } from "@digitalservicebund/ris-ui/components";
import NormHeadingFootnotes from "./NormHeadingFootnotes.vue";
import { getNormTitle } from "./titles";
import type { NormContent } from "./useNormData";
import type { LegislationWork } from "~/types";
const props = defineProps<{
  htmlParts?: NormContent["htmlParts"];
  metadata: LegislationWork;
}>();

// show very long titles in smaller font
const heading1MaxLength = 180;
const isLongTitle = computed(
  () => props.metadata.name.length > heading1MaxLength,
);

// only show the alternate name as secondary if it isn't already being used as the main title
const hasHeading = computed(() => !!props.htmlParts?.heading);
</script>
<template>
  <div class="dokumentenkopf mt-24 mb-48 max-w-prose">
    <hgroup>
      <p
        v-if="hasHeading"
        class="word-wrap ris-heading3-regular mb-12 break-words hyphens-auto max-sm:text-[20px]"
      >
        {{ metadata.alternateName }}
      </p>
      <client-only>
        <RisExpandableText :length="6"
          ><div
            v-if="hasHeading"
            :data-longTitle="isLongTitle || null"
            class="break-words hyphens-auto max-sm:text-[26px]"
            v-html="props.htmlParts?.heading"
          />
          <div v-else class="titel break-words max-sm:text-[26px]">
            {{ getNormTitle(props.metadata) }}
          </div>
        </RisExpandableText>
        <template #fallback>
          <div
            v-if="props.htmlParts?.heading"
            :data-longTitle="isLongTitle || null"
            class="break-words hyphens-auto max-sm:text-[26px]"
            v-html="props.htmlParts.heading"
          ></div>
          <div v-else class="titel break-words max-sm:text-[26px]">
            {{ getNormTitle(props.metadata) }}
          </div>
        </template>
      </client-only>
    </hgroup>
    <NormHeadingFootnotes
      :html="props.htmlParts?.headingAuthorialNotes"
      :text-length="props.htmlParts?.headingAuthorialNotesLength"
    />
  </div>
</template>
