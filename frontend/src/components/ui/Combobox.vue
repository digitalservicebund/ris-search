<script setup lang="ts">
import {
  ComboboxAnchor,
  ComboboxCancel,
  ComboboxContent,
  ComboboxInput,
  ComboboxItem,
  ComboboxPortal,
  ComboboxRoot,
  ComboboxTrigger,
  ComboboxViewport,
} from "reka-ui";
import { computed } from "vue";
import IcBaselineClose from "~icons/ic/baseline-close";
import IcBaselineKeyboardArrowDown from "~icons/ic/baseline-keyboard-arrow-down";
import { tw } from "../../utils/tags";
import ProgressSpinner from "./ProgressSpinner.vue";

export interface ComboboxOption {
  id: string;
  label: string;
  secondaryLabel?: string;
}

const {
  options = [],
  loading = false,
  initialLabel,
  appendTo,
} = defineProps<{
  /** The list of currently offered options, e.g. search results. */
  options?: ComboboxOption[];
  /** Shows a spinner instead of the trigger icon while a search is in flight. */
  loading?: boolean;
  /** Display text for a preselected value, used before `options` contains it. */
  initialLabel?: string;
  /**
   * Where to portal the suggestion panel. Defaults to `document.body`. Pass a
   * Drawer's `append-target` when rendering inside one: a native `<dialog>`
   * makes everything outside its own DOM subtree inert, so `document.body` is
   * unreachable there.
   */
  appendTo?: HTMLElement;
}>();

const modelValue = defineModel<string>();
const searchTerm = defineModel<string>("searchTerm", { default: "" });
const open = defineModel<boolean>("open", { default: false });

/*
 * The public model is the selected option's id (a plain string), so callers
 * don't need to hold onto option objects. ComboboxRoot's own model needs the
 * full object (for `display-value` and the `by="id"` comparator), so this
 * bridges the two. For an id that isn't in `options` yet (e.g. a preselected
 * value before its label has loaded), falls back to `initialLabel`, and to
 * the raw id itself if that isn't given either, rather than showing nothing.
 */
const selectedOption = computed<ComboboxOption | undefined>({
  get: () =>
    options.find((option) => option.id === modelValue.value) ??
    (modelValue.value
      ? { id: modelValue.value, label: initialLabel ?? modelValue.value }
      : undefined),
  set: (option) => {
    modelValue.value = option?.id;
  },
});

const displayValue = (option: ComboboxOption | undefined) =>
  option?.label ?? "";

defineOptions({ inheritAttrs: false });

// Classes ------------------------------------------------

const anchorClass = tw`typo-label2-regular flex min-h-48 w-full cursor-pointer border-2 border-blue-800 bg-white py-4 pr-4 pl-16 -outline-offset-4 outline-blue-800 hover:outline-4 has-focus-visible:outline-4`;

const inputClass = tw`w-full bg-transparent focus-visible:outline-hidden placeholder:text-gray-800`;

const buttonClass = tw`flex size-36 shrink-0 cursor-pointer items-center justify-center self-center text-blue-800 hover:bg-blue-100 hover:text-blue-800 focus-visible:bg-blue-800 focus-visible:text-white focus-visible:outline-none`;

const triggerActiveClass = tw`bg-blue-800 text-white hover:bg-blue-800 hover:text-white`;

// When portaled into a Drawer's `append-target`, that target is
// `pointer-events-none` by default (it only exists to escape the enclosing
// native <dialog>'s inertness), so content placed inside it needs its own
// `pointer-events-auto` to stay interactive. Harmless when portaled to
// `document.body` instead, which doesn't set `pointer-events` at all.
const contentClass = tw`pointer-events-auto z-20 max-h-[min(14rem,var(--reka-combobox-content-available-height))] w-[var(--reka-combobox-trigger-width)] overflow-auto bg-white p-8 shadow-md`;

const itemClass = tw`flex min-h-48 cursor-pointer flex-col justify-center gap-2 border-l-4 border-transparent px-12 py-10 data-[highlighted]:border-blue-600 data-[highlighted]:bg-blue-200 data-[state=checked]:border-blue-800 data-[state=checked]:bg-blue-200`;
</script>

<template>
  <ComboboxRoot
    v-model="selectedOption"
    v-model:open="open"
    by="id"
    ignore-filter
    reset-model-value-on-clear
  >
    <ComboboxAnchor :class="anchorClass">
      <ComboboxInput
        v-bind="$attrs"
        v-model="searchTerm"
        :class="inputClass"
        :display-value="displayValue"
      />

      <ProgressSpinner
        v-if="loading"
        class="mr-4 !h-24 !w-24 shrink-0 self-center"
      />

      <ComboboxCancel
        v-if="searchTerm || modelValue"
        tabindex="0"
        :class="buttonClass"
        aria-label="Entfernen"
      >
        <IcBaselineClose class="h-[1em] w-[1em]" />
      </ComboboxCancel>

      <ComboboxTrigger
        tabindex="0"
        :class="[buttonClass, { [triggerActiveClass]: open }]"
        aria-label="Vorschläge anzeigen"
      >
        <IcBaselineKeyboardArrowDown class="h-[1.25em] w-[1.25em]" />
      </ComboboxTrigger>
    </ComboboxAnchor>

    <ComboboxPortal :to="appendTo">
      <ComboboxContent position="popper" :class="contentClass">
        <ComboboxViewport>
          <ComboboxItem
            v-for="option in options"
            :key="option.id"
            :value="option"
            :text-value="option.label"
            :class="itemClass"
          >
            <div class="typo-label1-regular">{{ option.label }}</div>
            <div
              v-if="option.secondaryLabel"
              class="typo-label2-regular text-gray-900"
            >
              {{ option.secondaryLabel }}
            </div>
          </ComboboxItem>
        </ComboboxViewport>
      </ComboboxContent>
    </ComboboxPortal>
  </ComboboxRoot>
</template>
