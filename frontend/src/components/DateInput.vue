<script lang="ts" setup>
import dayjs from "dayjs";
import customParseFormat from "dayjs/plugin/customParseFormat";
import { InputMask } from "primevue";
import { computed, nextTick, ref, useTemplateRef, watch } from "vue";
import IconErrorOutline from "~icons/ic/baseline-error-outline";
import ClearButtonWrapper from "~/components/ClearButtonWrapper.vue";

/** Form field validation error. */
export type ValidationError = {
  /** Error code (intended for differentiating types in code). */
  code?: string;

  /** Error message (intended for displaying to the user). */
  message: string;

  /** Identifier that can be used for connecting the error with a UI control. */
  instance: string;
};

const props = withDefaults(
  defineProps<{
    /** HTML element ID of the form field. */
    id: string;

    /** Value of the form field. */
    modelValue?: string;

    /** Visual size of the form field. */
    size?: "regular" | "medium" | "small";

    /** Enable or disable editing the form field. */
    isReadOnly?: boolean;

    /** Label of the form field. */
    label?: string;

    /** Validation error and message to display. */
    validationError?: ValidationError;

    /** Whether to show a clear button. */
    withClearButton?: boolean;
  }>(),
  {
    modelValue: "",
    size: "small",
    isReadOnly: false,
    label: undefined,
    validationError: undefined,
    withClearButton: false,
  },
);

const emit = defineEmits<{
  /**
   * Emitted when the user changes the value of the form field. Note that this
   * is only emitted when the value is empty or a valid date. All other states
   * (e.g. partial dates while typing) are handled internally and not emitted.
   */
  "update:modelValue": [value?: string];

  /**
   * Emitted when the form field enters an invalid state based on user inputs
   * (e.g. the date is invalid).
   */
  "update:validationError": [value?: ValidationError];
}>();

const HUMAN_READABLE_FORMAT = "DD.MM.YYYY";
const MACHINE_FORMAT = "YYYY-MM-DD";

dayjs.extend(customParseFormat);

const inputValue = ref(
  props.modelValue
    ? dayjs(props.modelValue).format(HUMAN_READABLE_FORMAT)
    : undefined,
);

watch(
  () => props.modelValue,
  (is) => {
    inputValue.value = is
      ? dayjs(is, MACHINE_FORMAT, true).format(HUMAN_READABLE_FORMAT)
      : undefined;
  },
);

watch(inputValue, (is) => {
  if (is === "") emit("update:modelValue", undefined);
  else if (isValidDate.value) {
    emit(
      "update:modelValue",
      dayjs(is, HUMAN_READABLE_FORMAT, true).format(MACHINE_FORMAT),
    );
  }
});

const inputCompleted = computed(() => {
  const datePattern = /^\d{2}\.\d{2}\.\d{4}$/;
  return datePattern.test(inputValue.value || "");
});

const localValidationError = ref<ValidationError | undefined>(undefined);

const internalHasError = computed(() => {
  return inputCompleted.value && !isValidDate.value;
});

const effectiveHasError = computed(() => {
  return internalHasError.value || !!localValidationError.value;
});

watch(
  () => props.validationError,
  (newVal) => {
    localValidationError.value = newVal;
  },
  { immediate: true },
);

const errorMessage = computed(() => {
  if (internalHasError.value && !localValidationError.value) {
    return "Ungültige Eingabe";
  } else if (localValidationError.value) {
    return localValidationError.value.message ?? "Ungültige Eingabe";
  } else return undefined;
});

const isValidDate = computed(() => {
  return dayjs(inputValue.value, HUMAN_READABLE_FORMAT, true).isValid();
});

const key = ref<string>();

function validateInput() {
  if (inputCompleted.value) {
    if (isValidDate.value) {
      emit("update:validationError", undefined);
    } else {
      const validationError = {
        message: "Kein valides Datum",
        instance: props.id,
      };
      emit("update:validationError", validationError);
    }
  } else if (inputValue.value) {
    const validationError = {
      message: "Unvollständiges Datum",
      instance: props.id,
    };
    emit("update:validationError", validationError);
  } else {
    emit("update:validationError", undefined);
  }
}

function backspaceDelete() {
  emit("update:validationError", undefined);
}

function onBlur() {
  validateInput();
}

watch(inputCompleted, (is) => {
  if (is) validateInput();
});

const inputMaskEl = useTemplateRef("inputMaskEl");

function focus() {
  // @ts-expect-error -- $el is not found, but this is what PrimeVue recommends
  inputMaskEl.value?.$el.focus();
}

async function clear() {
  inputValue.value = "";
  // Setting v-model to "" alone is apparently not enough to reset InputMask's internal
  // buffer. Changing :key forces Vue to fully unmount and remount the component,
  // which makes sure the input is actually cleared when the user starts typing again.
  key.value = crypto.randomUUID();
  await nextTick();
  focus();
}

defineExpose({ focus });
</script>

<template>
  <ClearButtonWrapper
    :clearButtonVisible="!!inputValue && props.withClearButton"
    @clear="clear"
  >
    <InputMask
      :id="id"
      :key
      ref="inputMaskEl"
      v-model="inputValue"
      :auto-clear="false"
      :invalid="effectiveHasError"
      :readonly="isReadOnly"
      :disabled="isReadOnly"
      class="w-full"
      mask="99.99.9999"
      placeholder="TT.MM.JJJJ"
      @blur="onBlur"
      @keydown="backspaceDelete"
    />

    <small v-if="errorMessage" :id="`${id}-hint`">
      <IconErrorOutline />
      {{ errorMessage }}
    </small>
  </ClearButtonWrapper>
</template>
