<script setup lang="ts">
import PrimevueSelect from "primevue/select";
import { useSimpleSearchParamsStore } from "~/stores/searchParams";

const store = useSimpleSearchParamsStore();

const pageSizeOptions = computed(() => {
  const steps = [10, 50, 100];
  const currentOption = store.itemsPerPage;
  if (!steps.includes(currentOption)) {
    steps.push(currentOption);
  }
  return steps.map((s) => ({ label: s.toString(), value: s.toString() }));
});

const itemsPerPage = computed({
  get: () => store.itemsPerPage.toString(),
  set: (value) => {
    store.itemsPerPage = Number.parseInt(value);
  },
});
</script>

<template>
  <span class="flex w-auto items-center gap-8">
    <label for="items-per-page-select" class="ris-label2-regular"
      >Einträge pro Seite</label
    >
    <PrimevueSelect
      id="items-per-page-select"
      v-model="itemsPerPage"
      option-label="label"
      option-value="value"
      :options="pageSizeOptions"
      label="label"
    />
  </span>
</template>
