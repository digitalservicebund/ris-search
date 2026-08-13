<script setup lang="ts">
import { computed, useAttrs, type StyleValue } from "vue";
import { tw } from "../../utils/tags";

const props = defineProps<{
  /** The value this radio button contributes when selected. */
  value: string;
  /** The value selected within the group this radio button belongs to. */
  modelValue?: string;
}>();

// `defineModel` would type the prop and the emitted value identically, but they
// differ here: the group's selection may be `undefined` while this radio button
// always emits its own `value`. Declaring both by hand keeps handlers at call
// sites from having to accept an `undefined` they can never receive.
const emit = defineEmits<{
  "update:modelValue": [value: string];
}>();

const checked = computed(() => props.modelValue === props.value);

function select() {
  emit("update:modelValue", props.value);
}

defineOptions({ inheritAttrs: false });

// Attributes ---------------------------------------------

// `class` and `style` target the wrapper, which owns the size and the styling of
// the adjacent label. Everything else (`id`, `name`, `disabled`, `aria-*`) goes
// to the input, so native attributes keep working without being redeclared.
const attrs = useAttrs();

const rootClass = computed(() => attrs.class as string | undefined);

const rootStyle = computed(() => attrs.style as StyleValue);

const inputAttrs = computed(() => {
  const rest = { ...attrs };
  delete rest.class;
  delete rest.style;
  return rest;
});

// Classes ------------------------------------------------

const root = tw`[&+label]:typo-label1-regular relative inline-block h-32 w-32 [&+label]:ml-8`;

const input = tw`peer h-full w-full cursor-pointer appearance-none rounded-full border-2 border-blue-800 bg-white hover:outline hover:outline-4 hover:-outline-offset-4 hover:outline-blue-800 focus-visible:outline focus-visible:outline-4 focus-visible:-outline-offset-4 focus-visible:outline-blue-800 active:outline-hidden disabled:cursor-not-allowed disabled:border-gray-600 aria-[invalid]:border-red-800 aria-[invalid]:outline-red-800 aria-[invalid]:active:outline-hidden aria-[invalid]:disabled:outline-hidden`;

const box = tw`pointer-events-none absolute inset-0 flex items-center justify-center text-transparent peer-checked:text-blue-800 peer-disabled:text-gray-600 peer-aria-[invalid]:text-red-800`;

const dot = tw`h-16 w-16 rounded-full bg-current`;
</script>

<template>
  <span :class="[root, rootClass]" :style="rootStyle">
    <input
      :checked="checked"
      :class="input"
      :value="value"
      type="radio"
      v-bind="inputAttrs"
      @change="select"
    />
    <span :class="box">
      <span :class="dot" />
    </span>
  </span>
</template>
