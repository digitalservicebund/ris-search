<script setup lang="ts">
import { computed, useSlots, type Component } from "vue";
import { tw } from "../../utils/tags";
import ProgressSpinner from "./ProgressSpinner.vue";

const props = withDefaults(
  defineProps<{
    as?: string | Component;
    disabled?: boolean;
    iconPos?: "left" | "right";
    label?: string;
    loading?: boolean;
    rounded?: boolean;
    severity?: "primary" | "secondary" | "danger" | "warn" | "info";
    size?: "small" | "normal" | "large";
    text?: boolean;
    type?: "button" | "submit" | "reset";
  }>(),
  {
    as: "button",
    disabled: false,
    iconPos: "left",
    label: undefined,
    loading: false,
    rounded: false,
    severity: "primary",
    size: "normal",
    text: false,
    type: "button",
  },
);

const slots = useSlots();

const isNativeButton = computed(() => props.as === "button");

const hasLabel = computed(() => props.label != null || slots.default != null);

const iconOnly = computed(
  () => (slots.icon != null || props.loading) && !hasLabel.value,
);

// Classes ------------------------------------------------

const base = tw`relative inline-flex max-w-full cursor-pointer items-center justify-center gap-8 text-center focus-visible:outline-4 focus-visible:outline-offset-4 focus-visible:outline-blue-800 focus-visible:not-focus-visible:outline-none active:outline-none disabled:cursor-not-allowed disabled:outline-hidden`;

const primary = tw`bg-blue-800 text-white hover:bg-blue-700 active:bg-blue-500 active:text-blue-800 disabled:bg-gray-400 disabled:text-gray-600`;

const secondary = tw`border-2 border-blue-800 bg-white text-blue-800 hover:bg-blue-200 focus-visible:bg-blue-200 active:bg-blue-400 disabled:border-blue-500 disabled:text-blue-500 disabled:hover:bg-white`;

const danger = tw`border-2 border-red-800 bg-white text-red-800 hover:bg-red-100 focus-visible:bg-red-100 active:border-red-800 active:bg-red-300 disabled:border-red-500 disabled:text-red-500 disabled:hover:bg-white`;

const warn = tw`border-gray-1000 color-gray-1000 border-4 bg-yellow-500 underline decoration-2 underline-offset-2`;

const info = tw`border border-blue-500 bg-white text-blue-800 hover:bg-gray-200 focus-visible:bg-gray-200 active:border-white active:bg-white disabled:border-blue-500 disabled:text-blue-500 disabled:hover:bg-white`;

const primaryText = tw`border-2 border-transparent bg-transparent text-blue-800 underline hover:border-gray-500 hover:bg-white focus-visible:border-gray-500 active:border-blue-600 active:bg-blue-400 disabled:border-transparent disabled:bg-transparent disabled:text-gray-500`;

const sizeClass = computed(() => {
  const { severity } = props;
  const pad = iconOnly.value
    ? { small: tw`w-40 px-4`, normal: tw`w-48 px-4`, large: tw`w-64 px-4` }
    : { small: tw`px-12`, normal: tw`px-16`, large: tw`px-24` };

  return {
    small: tw`${severity === "info" ? "typo-label2-regular" : "typo-label2-bold"} h-40 py-4 ${pad.small}`,
    normal: tw`${severity === "warn" ? "typo-label1-bold" : "typo-label2-bold"} h-48 py-4 ${pad.normal}`,
    large: tw`typo-label1-bold h-64 py-4 ${pad.large}`,
  };
});

const rootClass = computed(() => {
  const { severity, size, text, rounded } = props;
  const sizes = sizeClass.value;

  return {
    [base]: true,
    "rounded-full": rounded,
    [sizes.small]: size === "small",
    [sizes.normal]: size === "normal",
    [sizes.large]: size === "large",
    [primary]: !text && severity === "primary",
    [secondary]: !text && severity === "secondary",
    [danger]: !text && severity === "danger",
    [warn]: !text && severity === "warn",
    [info]: !text && severity === "info",
    [primaryText]: text && severity === "primary",
  };
});

const iconClass = computed(() => ({ "order-last": props.iconPos === "right" }));

const loadingIconClass = computed(() =>
  props.size === "large"
    ? "!h-24 !w-24 !text-current"
    : "!h-[1.34em] !w-[1.34em] !text-current",
);
</script>

<template>
  <component
    :class="rootClass"
    :disabled="isNativeButton ? disabled || loading : undefined"
    :is="as"
    :type="isNativeButton ? type : undefined"
  >
    <ProgressSpinner
      v-if="loading"
      :class="loadingIconClass"
      aria-hidden="true"
    />
    <slot v-else name="icon" :class="iconClass" />
    <span v-if="hasLabel">
      <slot>{{ label }}</slot>
    </span>
  </component>
</template>
