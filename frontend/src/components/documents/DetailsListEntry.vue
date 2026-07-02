<script setup lang="ts">
import { isEmpty } from "lodash-es";
import type { HTMLAttributes } from "vue";

const props = defineProps<{
  label: string;
  value?: string;
  placeholder?: string;
  valueList?: string[];
  valueClass?: HTMLAttributes["class"];
}>();

const emptyProp = computed(() =>
  isStringEmpty(props.value) && isEmpty(props.valueList) ? "empty" : null,
);

const emptyValuePlaceholder = "nicht vorhanden";
</script>

<template>
  <div class="col-span-12 grid grid-cols-subgrid">
    <dt
      class="typo-label1-bold col-span-12 hyphens-auto md:col-span-3 xl:col-span-2"
    >
      {{ label }}
    </dt>

    <dd
      v-if="$slots.default"
      class="typo-label1-regular col-span-12 md:col-span-9 md:col-start-4 lg:col-span-6 lg:col-start-4"
      :class="valueClass"
    >
      <div>
        <slot />
      </div>
    </dd>

    <dd
      v-for="listItem in valueList"
      v-else-if="valueList?.length"
      :key="listItem"
      class="typo-label1-regular col-span-12 md:col-span-9 md:col-start-4 lg:col-span-6 lg:col-start-4"
      :class="valueClass"
    >
      {{ listItem }}
    </dd>

    <dd
      v-else
      class="typo-label1-regular col-span-12 data-empty:text-gray-900 md:col-span-9 md:col-start-4 lg:col-span-6 lg:col-start-4"
      :data-empty="emptyProp"
      :class="valueClass"
    >
      {{ value || placeholder || emptyValuePlaceholder }}
    </dd>
  </div>
</template>
