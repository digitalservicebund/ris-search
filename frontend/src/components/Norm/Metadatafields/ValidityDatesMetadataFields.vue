<script setup lang="ts">
import type { Dayjs } from "dayjs";
import MetadataField from "~/components/MetadataField.vue";
import { dateFormattedDDMMYYYY } from "~/utils/dateFormatting";

interface Props {
  validFrom?: Dayjs;
  validTo?: Dayjs;
}

const { validFrom, validTo } = defineProps<Props>();
const formattedValidFrom = computed(() => dateFormattedDDMMYYYY(validFrom));
const formattedValidTo = computed(() => dateFormattedDDMMYYYY(validTo));
</script>

<template>
  <MetadataField label="Gültig ab" :value="formattedValidFrom" />
  <MetadataField label="Gültig bis" :value="formattedValidTo" />
</template>
