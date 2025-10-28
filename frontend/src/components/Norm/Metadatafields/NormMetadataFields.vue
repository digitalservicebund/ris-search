<script setup lang="ts">
import type { Dayjs } from "dayjs";
import MetadataField from "~/components/MetadataField.vue";
import ValidityDatesMetadataFields from "~/components/Norm/Metadatafields/ValidityDatesMetadataFields.vue";
import { getValidityStatusLabel, type ValidityStatus } from "~/utils/norm";
import { isPrototypeProfile } from "~/utils/profile";

interface Props {
  abbreviation?: string;
  status?: ValidityStatus;
  validFrom?: Dayjs;
  validTo?: Dayjs;
}

const props = defineProps<Props>();
const formattedStatus = computed(() => getValidityStatusLabel(props.status));
</script>

<template>
  <div class="mt-8 mb-48 flex flex-wrap items-end gap-24">
    <MetadataField
      v-if="abbreviation"
      label="Abkürzung"
      :value="abbreviation"
    />
    <MetadataField label="Status" :value="formattedStatus" />
    <ValidityDatesMetadataFields
      v-if="!isPrototypeProfile()"
      :valid-from="validFrom"
      :valid-to="validTo"
    />
  </div>
</template>
