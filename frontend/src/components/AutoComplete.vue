<script lang="ts" setup>
import IcBaselineKeyboardArrowDown from "~icons/ic/baseline-keyboard-arrow-down";
import IcBaselineClose from "~icons/ic/baseline-close";
import AutoCompleteComponent, {
  type AutoCompleteProps,
} from "primevue/autocomplete";
import ProgressSpinner from "primevue/progressspinner";
import { ref } from "vue";

export interface AutoCompleteSuggestion {
  id: string;
  label: string;
  secondaryLabel?: string;
}

/*
To keep TypeScript for the inner component's props, this wrapper component explicitly declares them
even though they are only passed through.
Ideally, they would be passed on using v-bind="mergeProps($attrs, props)" instead of being declared one-by-one,
but using this approach broke the forwarding of emits.
*/
export type RisAutoCompleteProps = Pick<
  AutoCompleteProps,
  | "dropdown"
  | "ariaLabel"
  | "ariaLabelledby"
  | "delay"
  | "completeOnFocus"
  | "optionDisabled"
  | "typeahead"
  | "scrollHeight"
  | "dropdownMode"
  | "placeholder"
  | "loading"
  | "invalid"
  | "disabled"
  | "forceSelection"
  | "autoOptionFocus"
  | "selectOnFocus"
  | "focusOnHover"
  | "appendTo"
> & {
  suggestions?: AutoCompleteSuggestion[];
  initialLabel?: string;
};

const props = defineProps<RisAutoCompleteProps>();
const model = defineModel<string>();

const isActiveOption = (option: AutoCompleteSuggestion) => {
  return option.id === model.value;
};

defineOptions({
  inheritAttrs: false,
});

/*
This wrapper component exposes its own v-model, which holds the ID of the currently selected item.
This is different from the PrimeVue AutoComplete behavior, where the model either holds the prefix
currently being typed by the user, or the selected object.
The outer model is changed
- in the @option-select emit, and
- in @update:model-value emit, only if the typed input resets to "".
 */

const innerValue = ref(props.initialLabel ?? model.value);
const onUpdateInnerValue = (
  newValue: string | AutoCompleteSuggestion | undefined,
) => {
  if (typeof newValue === "string") {
    innerValue.value = newValue;
  } else if (newValue && newValue.label) {
    innerValue.value = newValue.label;
  }

  if (newValue === "") {
    model.value = undefined;
  }
};

const onClear = () => onUpdateInnerValue("");

const dropdownButtonFocus = ref(false);
</script>

<template>
  <AutoCompleteComponent
    ref="autoCompleteRef"
    v-bind="$attrs"
    :suggestions="props.suggestions"
    :model-value="innerValue"
    :dropdown="props.dropdown"
    :dropdown-mode="props.dropdownMode"
    :disabled="props.disabled"
    :force-selection="props.forceSelection"
    :placeholder="props.placeholder"
    :aria-label="props.ariaLabel"
    :aria-labelledby="props.ariaLabelledby"
    :delay="props.delay"
    :complete-on-focus="props.completeOnFocus"
    :typeahead="props.typeahead"
    :scroll-height="props.scrollHeight"
    :loading="props.loading"
    :invalid="props.invalid"
    :auto-option-focus="props.autoOptionFocus"
    :select-on-focus="props.selectOnFocus"
    :focus-on-hover="props.focusOnHover"
    :append-to="props.appendTo"
    :fluid="true"
    :option-disabled="props.optionDisabled"
    option-label="label"
    data-key="value"
    @update:model-value="onUpdateInnerValue"
    @option-select="(e) => (model = e.value.id)"
    @show="dropdownButtonFocus = true"
    @hide="dropdownButtonFocus = false"
  >
    <template #loader>
      <ProgressSpinner class="absolute inset-y-0 right-8 my-auto" />
    </template>
    <template #dropdown="slotProps">
      <button
        v-if="innerValue"
        class="cursor-pointer p-8 text-blue-800 hover:bg-blue-100 hover:text-blue-800 focus-visible:bg-blue-800 focus-visible:text-white focus-visible:outline-none"
        aria-label="Entfernen"
        @click="onClear"
      >
        <IcBaselineClose class="h-[1em] w-[1em]" />
      </button>

      <button
        :class="{
          'cursor-pointer p-8 text-blue-800 focus-visible:bg-blue-800 focus-visible:text-white focus-visible:outline-none': true,
          'bg-blue-800 text-white': dropdownButtonFocus,
          'hover:bg-blue-100 hover:text-blue-800': !dropdownButtonFocus,
        }"
        aria-label="Vorschläge anzeigen"
        aria-haspopup="listbox"
        @click="slotProps.toggleCallback"
      >
        <IcBaselineKeyboardArrowDown class="h-[1.25em] w-[1.25em]" />
      </button>
    </template>
    <template #option="slotProps: { option: AutoCompleteSuggestion }">
      <div
        :data-variant="isActiveOption(slotProps.option) && 'active'"
        class="flex min-h-48 flex-col justify-center gap-2 border-l-4 border-transparent px-12 py-10 data-[variant=active]:-ml-4 data-[variant=active]:border-blue-800 data-[variant=active]:bg-blue-200"
      >
        <div class="ris-label1-regular">
          {{ slotProps.option.label }}
        </div>
        <div
          v-if="slotProps.option.secondaryLabel"
          class="ris-label2-regular text-gray-900"
        >
          {{ slotProps.option.secondaryLabel }}
        </div>
      </div>
    </template>
  </AutoCompleteComponent>
</template>
