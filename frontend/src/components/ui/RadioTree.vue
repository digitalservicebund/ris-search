<script setup lang="ts">
import { useId } from "vue";
import { tw } from "../../utils/tags";

export type RadioTreeChild = {
  /** Value emitted when this item is selected. */
  value: string;
  /** Text shown in the item's row. */
  label: string;
};

export type RadioTreeItem = RadioTreeChild & {
  /**
   * Sub-items of this item. They will be visible when the parent item is
   * selected.
   */
  children?: RadioTreeChild[];
  /**
   * Label of the row standing in for this item at the top of its own list of
   * children, e.g. "Alle Gerichtsentscheidungen" below
   * "Gerichtsentscheidungen". It is a second label for the same radio rather
   * than a value of its own, so selecting either row selects this item. Ignored
   * without `children`.
   */
  selfLabel?: string;
};

const { items, modelValue } = defineProps<{
  /** Items to render, in order. */
  items: RadioTreeItem[];
  /** The selected value. */
  modelValue?: string;
}>();

// `defineModel` would type the prop and the emitted value identically, but they
// differ here: the selection may be `undefined` while the tree always emits the
// value of the item that was selected.
const emit = defineEmits<{
  "update:modelValue": [value: string];
}>();

/** A branch is open exactly while the selection is inside it. */
function isExpanded(item: RadioTreeItem) {
  return (
    item.value === modelValue ||
    !!item.children?.some((child) => child.value === modelValue)
  );
}

/** Whether the item is repeated as the first row of its own children. */
function hasSelfRow(item: RadioTreeItem) {
  return !!item.selfLabel && !!item.children?.length && isExpanded(item);
}

const uid = useId();

const inputId = (value: string) => `${uid}-${value}`;

const labelId = (value: string) => `${uid}-${value}-label`;

// Classes ------------------------------------------------

const input = tw`peer absolute inset-0 cursor-pointer appearance-none opacity-0`;

const row = tw`flex w-full border-l-4 border-transparent p-16 pl-[1.125rem] text-left text-blue-800`;

const radioRow = tw`pointer-events-none peer-hover:border-blue-500 peer-hover:bg-blue-200 peer-focus-visible:outline-4 peer-focus-visible:-outline-offset-4 peer-focus-visible:outline-blue-800 peer-active:border-blue-800 peer-active:bg-blue-300`;

const selfRow = tw`cursor-pointer hover:border-blue-500 hover:bg-blue-200 active:border-blue-800 active:bg-blue-300`;

const inactive = tw`typo-label1-regular`;

const branch = tw`typo-label1-bold border-l-blue-500 bg-blue-200 text-blue-900 peer-hover:border-l-blue-500 peer-hover:bg-blue-300 peer-active:border-l-blue-800 peer-active:bg-blue-300`;

const selected = tw`typo-label1-bold border-l-blue-800 bg-blue-300 text-blue-900 peer-hover:border-l-blue-900 peer-hover:bg-blue-500 peer-active:border-l-blue-900 peer-active:bg-blue-500 hover:border-l-blue-900 hover:bg-blue-500`;

const list = tw`flex flex-col gap-2`;

const nestedList = tw`mt-2 flex flex-col gap-2 pl-20`;

function itemClass(item: RadioTreeItem) {
  const isSelected = item.value === modelValue && !hasSelfRow(item);

  return {
    [row]: true,
    [radioRow]: true,
    [inactive]: !isSelected && !isExpanded(item),
    [branch]: !isSelected && isExpanded(item),
    [selected]: isSelected,
  };
}

function childClass(value: string) {
  return {
    [row]: true,
    [radioRow]: true,
    [inactive]: value !== modelValue,
    [selected]: value === modelValue,
  };
}

function selfClass(item: RadioTreeItem) {
  return {
    [row]: true,
    [selfRow]: true,
    [inactive]: item.value !== modelValue,
    [selected]: item.value === modelValue,
  };
}
</script>

<template>
  <ul :class="list">
    <li v-for="item in items" :key="item.value">
      <div class="relative">
        <input
          :aria-labelledby="hasSelfRow(item) ? labelId(item.value) : undefined"
          :checked="item.value === modelValue"
          :class="input"
          :id="inputId(item.value)"
          :name="uid"
          :value="item.value"
          type="radio"
          @change="emit('update:modelValue', item.value)"
        />
        <label
          :class="itemClass(item)"
          :for="inputId(item.value)"
          :id="labelId(item.value)"
        >
          {{ item.label }}
        </label>
      </div>

      <ul v-if="item.children?.length && isExpanded(item)" :class="nestedList">
        <!-- Hidden from assistive technology: this row is a second label for
        the radio of the item above, so announcing it would repeat a control
        that has already been announced under its own name. -->
        <li v-if="hasSelfRow(item)" aria-hidden="true">
          <label :class="selfClass(item)" :for="inputId(item.value)">
            {{ item.selfLabel }}
          </label>
        </li>

        <li v-for="child in item.children" :key="child.value" class="relative">
          <input
            :checked="child.value === modelValue"
            :class="input"
            :id="inputId(child.value)"
            :name="uid"
            :value="child.value"
            type="radio"
            @change="emit('update:modelValue', child.value)"
          />
          <label :class="childClass(child.value)" :for="inputId(child.value)">
            {{ child.label }}
          </label>
        </li>
      </ul>
    </li>
  </ul>
</template>
