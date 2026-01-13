<script setup lang="ts">
import PrimevueSelect from "primevue/select";

const model = defineModel<number>({ required: true });

const pageSizeOptions = computed(() => {
  const steps = [10, 50, 100];
  const currentOption = model.value;
  if (!steps.includes(currentOption)) {
    steps.push(currentOption);
  }
  return steps.map((s) => ({ label: s.toString(), value: s.toString() }));
});

const itemsPerPage = computed({
  get: () => model.value.toString(),
  set: (value) => {
    model.value = Number.parseInt(value);
  },
});

const itemsPerPageSelectId = useId();
</script>

<template>
  <span class="flex w-auto items-center gap-8">
    <label :for="itemsPerPageSelectId" class="ris-label2-regular"
      >Einträge pro Seite</label
    >
    <PrimevueSelect
      :id="itemsPerPageSelectId"
      v-model="itemsPerPage"
      option-label="label"
      option-value="value"
      :options="pageSizeOptions"
      label="label"
    />
  </span>
</template>
