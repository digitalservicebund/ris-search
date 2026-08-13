<script setup lang="ts">
import { computed } from "vue";
import { tw } from "../../utils/tags";

export type BadgeColor = "blue" | "green" | "yellow" | "red" | "gray";

const props = withDefaults(
  defineProps<{
    label: string;
    color: BadgeColor;
    variant?: "standard" | "extraSmall" | "small" | "medium" | "large";
  }>(),
  {
    variant: "standard",
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

const extraSmall = tw`ris-label3-regular sm:ris-label2-regular 2xl:ris-label1-regular`;

const small = tw`ris-label2-regular sm:ris-label1-regular`;

const medium = tw`ris-label2-regular 2xl:ris-label1-regular`;

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
</script>

<template>
  <span :class="rootClass">
    {{ label }}
  </span>
</template>
