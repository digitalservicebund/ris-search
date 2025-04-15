<script lang="ts" setup>
import { computed } from "vue";
import type { DropdownInputModelType, DropdownItem } from "./types";

const props = defineProps<{
  items: DropdownItem[];
  modelValue?: DropdownInputModelType;
  placeholder?: string;
}>();

const emit = defineEmits<{
  "update:modelValue": [DropdownInputModelType | undefined];
}>();

const localModelValue = computed({
  get: () => props.modelValue ?? "",
  set: (value) => {
    emit("update:modelValue", value);
  },
});

const hasPlaceholder = computed(() =>
  Boolean(!props.modelValue && props.placeholder),
);
</script>

<template>
  <select
    v-model="localModelValue"
    class="ds-select ds-select-medium"
    :data-placeholder="hasPlaceholder ? true : undefined"
    tabindex="0"
  >
    <option v-if="placeholder && !localModelValue" disabled value="">
      {{ placeholder }}
    </option>
    <option v-for="item in items" :key="item.value" :value="item.value">
      {{ item.label }}
    </option>
  </select>
</template>
