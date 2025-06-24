<script setup lang="ts">
import MetadataField from "~/components/MetadataField.vue";

defineProps<{
  abbreviation?: string;
  status?: string;
  validFrom?: string;
  validTo?: string;
}>();
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
    <MetadataField
      v-if="validFrom"
      id="validFrom"
      label="Fassung gültig seit"
      :value="validFrom"
    />
    <MetadataField
      v-if="validTo"
      id="validTo"
      label="Fassung gültig bis"
      :value="validTo"
    />
  </div>
</template>
