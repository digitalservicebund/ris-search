<script setup lang="ts">
import MetadataField from "~/components/MetadataField.vue";

const datePlaceholder = "-";
withDefaults(
  defineProps<{
    abbreviation?: string;
    status?: string;
    validFrom?: string;
    validTo?: string;
  }>(),
  {
    validFrom: datePlaceholder,
    validTo: datePlaceholder,
  },
);
</script>

<template>
  <div class="mt-8 mb-48 flex flex-wrap items-end gap-24">
    <MetadataField
      v-if="abbreviation"
      id="abbreviation"
      label="Abkürzung"
      :value="abbreviation"
    />
    <MetadataField id="status" label="Status" :value="status" />
    <MetadataField id="validFrom" label="Gültig ab" :value="validFrom" />
    <MetadataField id="validTo" label="Gültig bis" :value="validTo" />
  </div>
</template>
