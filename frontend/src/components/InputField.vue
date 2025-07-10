<script lang="ts" setup>
import { computed, ref, watch } from "vue";
import type { ValidationError } from "./types";
import errors from "~/i18n/errors.json";
import { isErrorCode } from "~/i18n/utils";

interface WithHtml {
  html: string;
}

interface Props {
  id: string;
  label: string | string[] | WithHtml;
  required?: boolean;
  labelPosition?: LabelPosition;
  labelClass?: string;
  validationError?: ValidationError;
  visuallyHideLabel?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  required: false,
  labelPosition: LabelPosition.TOP,
  labelClass: undefined,
  validationError: undefined,
});

defineSlots<{
  default(props: {
    id: Props["id"];
    hasError: boolean;
    updateValidationError: typeof updateValidationError;
  }): unknown;
}>();

/* -------------------------------------------------- *
 * Label                                              *
 * -------------------------------------------------- */

const wrapperClasses = computed(() => ({
  "flex-col": props.labelPosition === LabelPosition.TOP,
  "flex-row":
    props.labelPosition === LabelPosition.RIGHT ||
    props.labelPosition === LabelPosition.LEFT,
}));

const labelConverted = computed(() => {
  if (Array.isArray(props.label)) return props.label;
  if (typeof props.label === "string") return [props.label];
  else return [];
});

const labelHtml = computed(() => {
  if (props.label && (props.label as WithHtml).html)
    return (props.label as WithHtml).html;
  else return undefined;
});

/* -------------------------------------------------- *
 * Validation error handling                          *
 * -------------------------------------------------- */

function updateValidationError(newValidationError?: ValidationError) {
  localValidationError.value = newValidationError;
}

const localValidationError = ref<ValidationError | undefined>(
  props.validationError,
);

const errorMessage = computed(() => {
  if (!localValidationError.value) return undefined;

  const { code, message } = localValidationError.value;
  if (code && isErrorCode(code)) return errors[code].title;
  else return message;
});

watch(
  () => props.validationError,
  (is) => {
    if (is) {
      localValidationError.value = is;
    } else {
      localValidationError.value = undefined;
    }
  },
);
</script>

<script lang="ts">
export enum LabelPosition {
  TOP = "top",
  RIGHT = "right",
  LEFT = "left",
}
</script>

<template>
  <div class="flex-start flex w-full gap-4" :class="wrapperClasses">
    <div
      v-if="(labelConverted && labelConverted.length !== 0) || labelHtml"
      class="flex flex-row items-center"
      :class="{
        'order-1': labelPosition === LabelPosition.RIGHT,
        'sr-only': visuallyHideLabel,
      }"
      data-testid="label-wrapper"
    >
      <label
        v-if="!labelHtml"
        class="grid items-center"
        :class="[
          { 'pr-4': labelPosition === LabelPosition.LEFT },

          { 'pl-4': labelPosition === LabelPosition.RIGHT },
          labelClass ? labelClass : 'ris-label2-regular',
        ]"
        :for="id"
      >
        <span v-for="(line, index) in labelConverted" :key="line">
          {{ line }}
          <span
            v-if="index === labelConverted.length - 1 && required"
            class="ml-4"
            >*</span
          >
        </span>
      </label>
      <label
        v-if="!!labelHtml"
        class="grid items-center"
        :class="[
          { 'pr-4': labelPosition === LabelPosition.LEFT },
          { 'pl-4': labelPosition === LabelPosition.RIGHT },
          labelClass ? labelClass : 'ris-label2-regular',
        ]"
        :for="id"
        v-html="labelHtml"
      />
    </div>

    <div class="flex flex-row items-center">
      <slot
        :id="id"
        :has-error="!!localValidationError"
        :update-validation-error="updateValidationError"
      />
    </div>

    <div v-if="localValidationError" class="flex flex-row items-center">
      <div
        class="lex-row ris-label3-regular mt-2 text-red-800"
        :data-testid="id + '-validationError'"
      >
        {{ errorMessage }}
      </div>
    </div>
  </div>
</template>
