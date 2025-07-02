<script setup lang="ts">
import MetadataField from "~/components/MetadataField.vue";
import { isPrototypeProfile } from "~/utils/config";
import type { Dayjs } from "dayjs";
import { getValidityStatusLabel } from "~/utils/normUtils";

interface Props {
  abbreviation?: string;
  status?: ValidityStatus;
  validFrom?: Dayjs;
  validTo?: Dayjs;
}

defineProps<Props>();
</script>

<template>
  <div class="mt-8 mb-48 flex flex-wrap items-end gap-24">
    <MetadataField
      v-if="abbreviation"
      id="abbreviation"
      label="Abkürzung"
      :value="abbreviation"
    />
    <MetadataField
      id="status"
      label="Status"
      :value="getValidityStatusLabel(status)"
    />
    <MetadataField
      v-if="!isPrototypeProfile()"
      id="validFrom"
      label="Gültig ab"
      :value="dateFormattedDDMMYYYY(validFrom) ?? '-'"
    />
    <MetadataField
      v-if="!isPrototypeProfile()"
      id="validTo"
      label="Gültig bis"
      :value="dateFormattedDDMMYYYY(validTo) ?? '-'"
    />
  </div>
</template>
