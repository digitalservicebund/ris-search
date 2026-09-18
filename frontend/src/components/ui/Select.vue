<script setup lang="ts">
import { computed, useAttrs, type StyleValue } from "vue";
import IcBaselineExpandMore from "~icons/ic/baseline-expand-more";
import { tw } from "../../utils/tags";

defineProps<{
  /** The selectable options. */
  options: readonly SelectOption[];
  /**
   * Text shown while nothing is selected. Rendered as a disabled option so it
   * can never end up being submitted as a value.
   */
  placeholder?: string;
}>();

const model = defineModel<string>();

const labelOf = (option: SelectOption) =>
  typeof option === "string" ? option : option.label;

const valueOf = (option: SelectOption) =>
  typeof option === "string" ? option : option.value;

defineOptions({ inheritAttrs: false });

// Attributes ---------------------------------------------

// `class` and `style` target the wrapper, which draws the border and the chevron.
// Everything else (`id`, `aria-labelledby`, `disabled`, …) goes to the select, so
// it lands on the element that actually carries the combobox semantics.
const attrs = useAttrs();

const rootClass = computed(() => attrs.class as string | undefined);

const rootStyle = computed(() => attrs.style as StyleValue);

const selectAttrs = computed(() => {
  const rest = { ...attrs };
  delete rest.class;
  delete rest.style;
  return rest;
});

// There is no `invalid` prop: the state is read back from the fallthrough
// `aria-invalid`, so the accessible state and the styling can never disagree.
const invalid = computed(
  () => attrs["aria-invalid"] === "true" || attrs["aria-invalid"] === true,
);

// Classes ------------------------------------------------

const rootBase = tw`group relative inline-flex`;

const hint = tw`[&+small]:typo-label2-regular [&+small]:mt-4 [&+small]:flex [&+small]:items-center [&+small]:gap-4 [&+small]:text-gray-900`;

const hintInvalid = tw`[&+small]:text-red-900`;

const wrapperClass = computed(() => ({
  [rootBase]: true,
  [hint]: true,
  [hintInvalid]: invalid.value,
}));

const selectBase = tw`typo-label2-regular peer field-sizing-content h-48 w-full cursor-pointer appearance-none border-2 py-4 pr-48 pl-16 hover:outline-4 hover:-outline-offset-4 focus-visible:outline-4 focus-visible:-outline-offset-4 disabled:cursor-not-allowed disabled:outline-hidden`;

const selectDefault = tw`border-blue-800 bg-white hover:outline-blue-800 focus-visible:outline-blue-800 disabled:border-blue-500 disabled:text-blue-500`;

const selectInvalid = tw`border-red-800 bg-red-200 hover:outline-red-800 focus-visible:outline-red-800`;

const selectClass = computed(() => ({
  [selectBase]: true,
  [selectDefault]: !invalid.value,
  [selectInvalid]: invalid.value,
}));

const chevronBase = tw`pointer-events-none absolute inset-y-6 right-6 flex w-36 items-center justify-center peer-disabled:bg-transparent peer-disabled:text-blue-500`;

const chevronDefault = tw`text-blue-800 peer-hover:bg-blue-200 peer-focus-visible:bg-blue-800 peer-focus-visible:text-white`;

const chevronInvalid = tw`text-red-800 peer-hover:bg-red-400 peer-focus-visible:bg-red-800 peer-focus-visible:text-white`;

const chevronClass = computed(() => ({
  [chevronBase]: true,
  [chevronDefault]: !invalid.value,
  [chevronInvalid]: invalid.value,
}));
</script>

<script lang="ts">
/**
 * A plain string is used as both the label and the value. Pass an object to
 * have them differ.
 */
export type SelectOption = string | { label: string; value: string };
</script>

<template>
  <div :class="[wrapperClass, rootClass]" :style="rootStyle">
    <select v-model="model" :class="selectClass" v-bind="selectAttrs">
      <option v-if="placeholder" disabled value="">{{ placeholder }}</option>
      <option
        v-for="option in options"
        :key="valueOf(option)"
        :value="valueOf(option)"
      >
        {{ labelOf(option) }}
      </option>
    </select>
    <span :class="chevronClass" aria-hidden="true">
      <IcBaselineExpandMore class="h-24 w-24" />
    </span>
  </div>
</template>
