<script setup lang="ts">
import type { Dayjs } from "dayjs";
import MetadataField from "~/components/MetadataField.vue";
import { dateFormattedDDMMYYYY } from "~/utils/dateFormatting";

interface Props {
  validFrom?: Dayjs;
  validTo?: Dayjs;
}

defineProps<Props>();
</script>

<template>
  <MetadataField
    id="validFrom"
    label="Gültig ab"
    :value="dateFormattedDDMMYYYY(validFrom) ?? '-'"
  />
  <MetadataField
    id="validTo"
    label="Gültig bis"
    :value="dateFormattedDDMMYYYY(validTo) ?? '-'"
  />
</template>
