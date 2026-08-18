<script setup lang="ts">
import sanitizeHtml from "sanitize-html";
import { computed } from "vue";
import { tw } from "../../utils/tags";

export type BadgeColor = "blue" | "green" | "yellow" | "red" | "gray";

const props = withDefaults(
  defineProps<{
    label?: string;
    color: BadgeColor;
    variant?: "standard" | "extraSmall" | "small" | "medium" | "large";
    isMarkup?: boolean;
  }>(),
  {
    variant: "standard",
    isMarkup: false,
  },
);

const base = tw`inline-block rounded-xs border px-8 py-4 hyphens-auto`;

const green = tw`border-green-400 bg-green-100 text-green-800`;
const yellow = tw`border-yellow-600 bg-yellow-200 text-orange-800`;
const blue = tw`border-blue-500 bg-blue-200 text-blue-800`;
const red = tw`border-red-400 bg-red-200 text-red-800`;
const gray = tw`text-gray-1000 border-gray-400`;

// "old" styles, will be removed with the implementation of RISDEV-11103, RISDEV-12247
// just kept for the transition phase
const standard = tw`typo-label2-bold flex-none`;

// for badges in the Metadata section of the document pages
const extraSmall = tw`ris-label3-regular sm:ris-label2-regular 2xl:ris-label1-regular`;

// for badges in the norm version list (fassungen tab)
const small = tw`ris-label2-regular sm:ris-label1-regular`;

// for badges in the search result header
const medium = tw`typo-label2-regular`;

// for badges in the details tab and 'about this service' page
const large = tw`typo-label1-regular`;

const rootClass = computed(() => {
  const { color, variant } = props;
  return {
    [base]: true,
    [green]: color === "green",
    [yellow]: color === "yellow",
    [blue]: color === "blue",
    [red]: color === "red",
    [gray]: color === "gray",
    [standard]: variant === "standard",
    [extraSmall]: variant === "extraSmall",
    [small]: variant === "small",
    [medium]: variant === "medium",
    [large]: variant === "large",
  };
});

const sanitizedLabel = computed(() => {
  return sanitizeHtml(props.label ?? "", { allowedTags: ["mark"] });
});
</script>

<template>
  <span v-if="isMarkup" :class="rootClass" v-html="sanitizedLabel" />
  <span v-else :class="rootClass">
    {{ label }}
  </span>
</template>
