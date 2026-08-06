<script setup lang="ts">
import { computed, useTemplateRef } from "vue";
import IcBaselineClose from "~icons/ic/baseline-close";
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

const showClearButton = computed(() => props.clearable && !!model.value);

function clear() {
  model.value = "";
  emit("clear");
  inputRef.value?.focus();
}

// The template ref is the underlying input, so consumers can focus it or set
// the selection range (see DataFieldPicker).
defineExpose({ input: inputRef });

defineOptions({ inheritAttrs: false });

// Classes ------------------------------------------------

const base = tw`[&+small]:typo-label2-regular border-2 border-blue-800 bg-white placeholder:text-gray-800 read-only:cursor-not-allowed read-only:border-blue-300 read-only:bg-blue-300 hover:outline-4 hover:-outline-offset-4 hover:outline-blue-800 focus-visible:outline-4 focus-visible:-outline-offset-4 focus-visible:outline-blue-800 disabled:border-blue-500 disabled:bg-white disabled:text-blue-500 disabled:outline-hidden aria-[invalid]:border-red-800 aria-[invalid]:bg-red-200 aria-[invalid]:outline-red-800 aria-[invalid]:disabled:outline-hidden [&+small]:mt-4 [&+small]:flex [&+small]:items-center [&+small]:gap-4 [&+small]:text-gray-900 [&[aria-invalid="true"]+small]:text-red-900`;

const small = tw`typo-label2-regular h-48 px-16 py-4`;

const large = tw`typo-label1-regular h-64 px-24 py-4`;

const clearButton = tw`absolute inset-y-6 right-6 flex aspect-square cursor-pointer items-center justify-center text-blue-800 hover:bg-blue-100 hover:text-blue-800 focus-visible:bg-blue-800 focus-visible:text-white focus-visible:outline-none`;

const inputClass = computed(() => ({
  [base]: true,
  [small]: props.size === "small",
  [large]: props.size === "large",
  "w-full": props.fluid || props.clearable,
  "pr-[2.5em]": props.clearable,
}));
</script>

<template>
  <span
    v-if="clearable"
    :class="{ 'w-full': fluid }"
    class="relative inline-flex"
  >
    <input
      ref="inputRef"
      v-model="model"
      :class="inputClass"
      :data-size="size"
      v-bind="$attrs"
    />
    <button
      v-if="showClearButton"
      :class="clearButton"
      aria-label="Entfernen"
      type="button"
      @click="clear"
    >
      <IcBaselineClose class="h-[1em] w-[1em]" />
    </button>
  </span>
  <input
    v-else
    :class="inputClass"
    :data-size="size"
    ref="inputRef"
    v-bind="$attrs"
    v-model="model"
  />
</template>

<style scoped>
@reference "../../assets/main.css";

/*
 * Style the browser's native clear icon for search inputs. Kept here with the
 * component; the larger size matches the `large` input variant (via data-size).
 */
input[type="search"] {
  &::-webkit-search-cancel-button {
    @apply -mr-12 h-36 w-36 cursor-pointer appearance-none;
    background-image: url("data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' viewBox='-16 -16 52 52'><path fill='currentColor' d='M19 6.41L17.59 5L12 10.59L6.41 5L5 6.41L10.59 12L5 17.59L6.41 19L12 13.41L17.59 19L19 17.59L13.41 12z'></path></svg>");
  }

  &::-webkit-search-cancel-button:hover {
    @apply bg-blue-100;
  }

  &[data-size="large"]::-webkit-search-cancel-button {
    @apply mr-[-1.125rem] h-48 w-48;
  }
}
</style>
