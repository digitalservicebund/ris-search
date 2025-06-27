<script setup lang="ts">
import MetadataField from "~/components/MetadataField.vue";
import { isPrototypeProfile } from "~/utils/config";

interface Props {
  abbreviation?: string;
  status?: ExpresssionStatus;
  validFrom?: string;
  validTo?: string;
}

const {
  abbreviation,
  status,
  validFrom = "-",
  validTo = "-",
} = defineProps<Props>();
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
      v-if="!isPrototypeProfile()"
      id="validFrom"
      label="Gültig ab"
      :value="validFrom"
    />
    <MetadataField
      v-if="!isPrototypeProfile()"
      id="validTo"
      label="Gültig bis"
      :value="validTo"
    />
  </div>
</template>
