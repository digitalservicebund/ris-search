<script lang="ts" setup>
import { vMaska } from "maska/vue";
import { computed, ref, useTemplateRef, watch } from "vue";
import UiInputText from "./InputText.vue";

const props = withDefaults(
  defineProps<{
    /** HTML element ID of the form field. */
    id: string;

    /** Value of the form field (4-digit year string). */
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
    showClear: true,
  },
);

const emit = defineEmits<{
  /**
   * Emitted when the user changes the value of the form field. Note that this
   * is only emitted when the value is empty or a complete 4-digit year. All
   * other states (e.g. partial input while typing) are handled internally and
   * not emitted.
   */
  "update:modelValue": [value?: string];
}>();

/** Internal input state. */
const inputValue = ref(props.modelValue || undefined);

/** Sync internal state when modelValue prop changes externally. */
watch(
  () => props.modelValue,
  (is) => {
    inputValue.value = is || undefined;
  },
);

const yearPattern = /^\d{4}$/;

/** Whether the input is a complete 4-digit year. */
const inputCompleted = computed(() => {
  return yearPattern.test(inputValue.value || "");
});

/** Emit model updates only when input is empty or complete. */
watch(inputValue, (is) => {
  if (is === "") {
    emit("update:modelValue", undefined);
  } else if (inputCompleted.value) {
    emit("update:modelValue", is);
  }
});

const inputEl = useTemplateRef("inputEl");

/** Focus the input element programmatically. */
function focus() {
  inputEl.value?.input?.focus();
}

defineExpose({ focus });
</script>

<template>
  <UiInputText
    :id="id"
    ref="inputEl"
    v-model="inputValue"
    v-maska="'####'"
    :clearable="showClear"
    :readonly="isReadOnly"
    :disabled="isReadOnly"
    fluid
    inputmode="numeric"
    placeholder="JJJJ"
  />
</template>
