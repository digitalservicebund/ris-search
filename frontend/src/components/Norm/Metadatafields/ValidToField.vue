<script setup lang="ts">
import type { Dayjs } from "dayjs";
import MetadataField from "~/components/MetadataField.vue";

defineProps<{ value?: Dayjs }>();
</script>

<template>
  <MetadataField
    id="validTo"
    label="Gültig bis"
    :value="dateFormattedDDMMYYYY(value) ?? '-'"
  />
</template>
