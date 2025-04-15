<script lang="ts" setup>
interface Props {
  id: string;
  label: string;
  class?: string;
  value?: string;
}

const props = withDefaults(defineProps<Props>(), {
  class: "flex flex-col gap-4",
  value: undefined,
});

const labelId = computed(() => `label-${props.id}`);
const emptyValuePlaceholder = "nicht vorhanden";
</script>

<template>
  <div :class="props.class">
    <div :id="labelId" class="ris-label2-regular text-gray-900">
      {{ props.label }}
    </div>
    <div
      :id="props.id"
      class="ris-label1-bold"
      :class="props.value ? '' : 'text-gray-900'"
      :aria-labelledby="labelId"
    >
      {{ props.value || emptyValuePlaceholder }}
    </div>
  </div>
</template>
