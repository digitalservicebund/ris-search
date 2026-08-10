<script setup lang="ts">
import { computed, useAttrs, useTemplateRef, type StyleValue } from "vue";
import CloseSmallIcon from "~icons/custom/closeSmall";
import { tw } from "../../utils/tags";

const props = withDefaults(
  defineProps<{
    clearable?: boolean;
    fluid?: boolean;
    size?: "small" | "large";
  }>(),
  {
    clearable: false,
    fluid: false,
    size: "small",
  },
);

const model = defineModel<string>();

const emit = defineEmits<{
  clear: [];
}>();

const inputRef = useTemplateRef<HTMLInputElement>("inputRef");

const attrs = useAttrs();

// `disabled` and `readonly` arrive as fallthrough attributes, and a valueless
// one (`<InputText disabled />`) reaches us as an empty string.
const hasBooleanAttr = (value: unknown) => value === "" || !!value;

const showClearButton = computed(
  () =>
    props.clearable &&
    !!model.value &&
    !hasBooleanAttr(attrs.disabled) &&
    !hasBooleanAttr(attrs.readonly),
);

function clear() {
  model.value = "";
  emit("clear");
  inputRef.value?.focus();
}

// The template ref is the underlying input, so consumers can focus it or set
// the selection range (see DataFieldPicker).
defineExpose({ input: inputRef });

defineOptions({ inheritAttrs: false });

// Attributes ---------------------------------------------

// `class` and `style` always target the root element, which is the wrapper when
// the clear button is rendered. Everything else goes to the input. Vue
// normalizes both before they reach `attrs`.
const rootClass = computed(() => attrs.class as string | undefined);

const rootStyle = computed(() => attrs.style as StyleValue);

const inputAttrs = computed(() => {
  const rest = { ...attrs };
  delete rest.class;
  delete rest.style;
  return rest;
});

// Classes ------------------------------------------------

const base = tw`[&+small]:typo-label2-regular border-2 border-blue-800 bg-white placeholder:text-gray-800 read-only:cursor-not-allowed read-only:border-blue-300 read-only:bg-blue-300 hover:outline-4 hover:-outline-offset-4 hover:outline-blue-800 focus-visible:outline-4 focus-visible:-outline-offset-4 focus-visible:outline-blue-800 disabled:border-blue-500 disabled:bg-white disabled:text-blue-500 disabled:outline-hidden aria-[invalid]:border-red-800 aria-[invalid]:bg-red-200 aria-[invalid]:outline-red-800 aria-[invalid]:disabled:outline-hidden [&+small]:mt-4 [&+small]:flex [&+small]:items-center [&+small]:gap-4 [&+small]:text-gray-900 [&[aria-invalid="true"]+small]:text-red-900`;

const small = tw`typo-label2-regular h-48 px-16 py-4`;

const large = tw`typo-label1-regular h-64 px-24 py-4`;

const clearButton = tw`absolute inset-y-6 right-6 flex aspect-square cursor-pointer items-center justify-center text-blue-800 hover:bg-blue-800 hover:text-white focus-visible:bg-blue-800 focus-visible:text-white focus-visible:outline-none`;

const clearButtonFocus = tw`group-has-[button:focus-visible]/input:outline-4 group-has-[button:focus-visible]/input:-outline-offset-4 not-aria-[invalid]:group-has-[button:focus-visible]/input:outline-blue-800`;

const inputClass = computed(() => ({
  [base]: true,
  [clearButtonFocus]: props.clearable,
  [large]: props.size === "large",
  [small]: props.size === "small",
  "pr-[2.5em]": props.clearable,
  "w-full": props.fluid || props.clearable,
}));
</script>

<template>
  <span
    v-if="clearable"
    :class="[{ 'w-full': fluid }, rootClass]"
    :style="rootStyle"
    class="group/input relative inline-flex"
  >
    <input
      ref="inputRef"
      v-model="model"
      :class="inputClass"
      v-bind="inputAttrs"
    />
    <button
      v-if="showClearButton"
      :class="clearButton"
      aria-label="Entfernen"
      type="button"
      @click="clear"
    >
      <CloseSmallIcon />
    </button>
  </span>
  <input
    v-else
    :class="inputClass"
    ref="inputRef"
    v-bind="$attrs"
    v-model="model"
  />
</template>

<style scoped>
/*
 * Remove the browser's native clear icon for search inputs. Its size, position
 * and hover styling differ per engine and it is absent in Firefox entirely, so
 * `clearable` provides a consistent replacement we fully control.
 */
input[type="search"]::-webkit-search-cancel-button {
  -webkit-appearance: none;
  appearance: none;
  display: none;
}
</style>
