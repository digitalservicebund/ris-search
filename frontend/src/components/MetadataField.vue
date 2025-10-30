<script lang="ts" setup>
interface Props {
  label: string;
  value?: string;
}

const props = defineProps<Props>();

const labelId = useId();
const emptyValuePlaceholder = "—"; // Note: this is an 'em dash'
</script>

<template>
  <div class="flex flex-col gap-4">
    <div :id="labelId" class="ris-label2-regular text-gray-900">
      {{ props.label }}
    </div>
    <div class="ris-label1-bold" :aria-labelledby="labelId">
      {{ props.value || emptyValuePlaceholder }}
    </div>
  </div>
</template>
