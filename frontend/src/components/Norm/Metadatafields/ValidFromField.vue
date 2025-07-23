<script setup lang="ts">
import type { Dayjs } from "dayjs";
import MetadataField from "~/components/MetadataField.vue";

defineProps<{ value?: Dayjs }>();
</script>

<template>
  <MetadataField
    id="validFrom"
    label="Gültig ab"
    :value="dateFormattedDDMMYYYY(value) ?? '-'"
  />
</template>
