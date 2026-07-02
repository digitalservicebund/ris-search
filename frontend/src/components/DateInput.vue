<script lang="ts" setup>
import dayjs from "dayjs";
import customParseFormat from "dayjs/plugin/customParseFormat";
import { InputMask } from "primevue";
import IconErrorOutline from "~icons/ic/baseline-error-outline";

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

    /** Whether to show a clear button. */
    showClear?: boolean;
  }>(),
  {
    modelValue: "",
    size: "small",
    isReadOnly: false,
    label: undefined,
    showClear: false,
  },
);

const emit = defineEmits<{
  /**
   * Emitted when the user changes the value of the form field. Note that this
   * is only emitted when the value is empty or a valid date. All other states
   * (e.g. partial dates while typing) are handled internally and not emitted.
   */
  "update:modelValue": [value?: string];
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
  errorMessage.value = undefined;

  if (is === "") {
    emit("update:modelValue", undefined);
  } else if (dayjs(is, HUMAN_READABLE_FORMAT, true).isValid()) {
    emit(
      "update:modelValue",
      dayjs(is, HUMAN_READABLE_FORMAT, true).format(MACHINE_FORMAT),
    );
  } else if (inputCompleted.value) {
    errorMessage.value = "Kein valides Datum";
  }
});

const inputCompleted = computed(() => {
  const datePattern = /^\d{2}\.\d{2}\.\d{4}$/;
  return datePattern.test(inputValue.value || "");
});

const errorMessage = ref<string | undefined>(undefined);
const key = ref<string>();

function onBlur() {
  if (!inputCompleted.value && inputValue.value) {
    errorMessage.value = "Unvollständiges Datum";
  }
}

const inputMaskEl = useTemplateRef("inputMaskEl");

function focus() {
  // @ts-expect-error -- $el is not found, but this is what PrimeVue recommends
  inputMaskEl.value?.$el.focus();
}

async function clear() {
  inputValue.value = "";
  errorMessage.value = undefined;
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
  <div>
    <WithClearButton
      :clearButtonVisible="!!inputValue && props.showClear"
      @clear="clear"
    >
      <InputMask
        :id="id"
        :key
        ref="inputMaskEl"
        v-model="inputValue"
        :auto-clear="false"
        :invalid="errorMessage !== undefined"
        :readonly="isReadOnly"
        :disabled="isReadOnly"
        class="w-full"
        :class="{ 'pr-[2.5em]': props.showClear }"
        mask="99.99.9999"
        placeholder="TT.MM.JJJJ"
        @blur="onBlur"
      />
    </WithClearButton>

    <small
      v-if="errorMessage"
      class="mt-4 flex items-center gap-4 text-red-900"
      :id="`${id}-hint`"
    >
      <IconErrorOutline />
      {{ errorMessage }}
    </small>
  </div>
</template>
