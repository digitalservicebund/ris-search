<script setup lang="ts">
import _ from "lodash";
import { isStringEmpty } from "~/utils/textFormatting";
const props = defineProps<{
  label: string;
  value?: string;
  placeholder?: string;
  valueList?: string[];
}>();

const emptyProp = computed(() =>
  isStringEmpty(props.value) && _.isEmpty(props.valueList) ? "empty" : null,
);

const emptyValuePlaceholder = "nicht vorhanden";
const descriptionTermId = useId();
</script>

<template>
  <div
    class="flex flex-col gap-8 md:grid md:grid-cols-4 md:gap-8 md:px-0 lg:grid-cols-5 xl:grid-cols-6"
  >
    <dt :id="descriptionTermId" class="ris-label1-bold hyphens-auto">
      {{ label }}
    </dt>
    <dd
      v-if="$slots.default"
      class="ris-label1-regular max-w-prose md:col-span-3"
      :aria-labelledby="descriptionTermId"
    >
      <div>
        <slot />
      </div>
    </dd>
    <dd
      v-for="listItem in valueList"
      v-else-if="valueList?.length"
      :key="listItem"
      class="ris-label1-regular max-w-prose md:col-span-3 md:col-start-2"
      :aria-labelledby="descriptionTermId"
    >
      {{ listItem }}
    </dd>
    <dd
      v-else
      class="ris-label1-regular max-w-prose data-empty:text-gray-900 md:col-span-3"
      :data-empty="emptyProp"
      :aria-labelledby="descriptionTermId"
    >
      {{ value || placeholder || emptyValuePlaceholder }}
    </dd>
  </div>
</template>
