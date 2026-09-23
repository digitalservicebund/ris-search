<script setup lang="ts">
import {
  ComboboxAnchor,
  ComboboxCancel,
  ComboboxContent,
  ComboboxEmpty,
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
  options?: ComboboxOption[];
  loading?: boolean;
  /** Display text for a preselected value, used before `options` contains it. */
  initialLabel?: string;
  /**
   * Where to portal the suggestion panel. Defaults to `document.body`. Pass a
   * Drawer's `append-target` when rendering inside one to guarantee correct
   * placement of the overlay.
   */
  appendTo?: HTMLElement;
}>();

const modelValue = defineModel<string>();

const searchTerm = defineModel<string>("searchTerm", { default: "" });

const open = defineModel<boolean>("open", { default: false });

// modelValue is just an id; ComboboxRoot needs the full option. Selections are
// cached here so the label survives `options` changing after selection (e.g.
// after performing a search). Falls back to initialLabel, then the raw id.
const knownOptions = new Map<string, ComboboxOption>();

const selectedOption = computed<ComboboxOption | undefined>({
  get: () => {
    if (!modelValue.value) return undefined;

    const found = options.find((option) => option.id === modelValue.value);
    if (found) {
      knownOptions.set(found.id, found);
      return found;
    }

    const known = knownOptions.get(modelValue.value);
    if (known) return known;

    return { id: modelValue.value, label: initialLabel ?? modelValue.value };
  },

  set: (option) => {
    if (option) knownOptions.set(option.id, option);
    modelValue.value = option?.id;
  },
});

const displayValue = (option: ComboboxOption | undefined) =>
  option?.label ?? "";

defineOptions({ inheritAttrs: false });

// Shared between the cancel and trigger buttons.
const buttonClass = tw`flex size-36 shrink-0 cursor-pointer items-center justify-center self-center text-blue-800 hover:bg-blue-100 hover:text-blue-800 focus-visible:bg-blue-800 focus-visible:text-white focus-visible:outline-none`;
</script>

<template>
  <ComboboxRoot
    v-model="selectedOption"
    v-model:open="open"
    by="id"
    ignore-filter
    reset-model-value-on-clear
  >
    <ComboboxAnchor
      class="typo-label2-regular flex min-h-48 w-full cursor-pointer border-2 border-blue-800 bg-white py-4 pr-4 pl-16 -outline-offset-4 outline-blue-800 hover:outline-4 has-focus-visible:outline-4"
    >
      <ComboboxInput
        v-bind="$attrs"
        v-model="searchTerm"
        class="w-full bg-transparent placeholder:text-gray-800 focus-visible:outline-hidden"
        :display-value="displayValue"
      />

      <ProgressSpinner
        v-if="loading"
        class="mr-4 !h-24 !w-24 shrink-0 self-center"
      />

      <ComboboxCancel
        v-if="searchTerm || modelValue"
        aria-label="Entfernen"
        :class="buttonClass"
        tabindex="0"
      >
        <IcBaselineClose class="h-[1em] w-[1em]" />
      </ComboboxCancel>

      <ComboboxTrigger
        aria-label="Vorschläge anzeigen"
        :class="[
          buttonClass,
          {
            'bg-blue-800 text-white hover:bg-blue-800 hover:text-white': open,
          },
        ]"
        tabindex="0"
      >
        <IcBaselineKeyboardArrowDown class="h-[1.25em] w-[1.25em]" />
      </ComboboxTrigger>
    </ComboboxAnchor>

    <ComboboxPortal :to="appendTo">
      <!-- When portaled into a Drawer's `append-target`, that target is
      `pointer-events-none` by default (it only exists to escape the enclosing
      native <dialog>'s inertness), so content placed inside it needs its own
      `pointer-events-auto` to stay interactive. Harmless when portaled to
      `document.body` instead, which doesn't set `pointer-events` at all. -->
      <ComboboxContent
        class="pointer-events-auto z-20 max-h-[min(14rem,var(--reka-combobox-content-available-height))] w-(--reka-combobox-trigger-width) overflow-auto bg-white p-8 shadow-md"
        position="popper"
      >
        <ComboboxViewport>
          <ComboboxEmpty
            class="typo-label2-regular flex min-h-48 items-center p-8 text-gray-900"
          >
            Keine Ergebnisse gefunden
          </ComboboxEmpty>

          <ComboboxItem
            v-for="option in options"
            :key="option.id"
            class="flex min-h-48 cursor-pointer flex-col justify-center gap-2 border-l-4 border-transparent px-12 py-10 data-highlighted:border-blue-600 data-highlighted:bg-blue-200 data-[state=checked]:border-blue-800 data-[state=checked]:bg-blue-200"
            :text-value="option.label"
            :value="option"
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
