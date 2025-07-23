<script setup lang="ts">
import type { Dayjs } from "dayjs";
import ValidFromField from "~/components/Norm/Metadatafields/ValidFromField.vue";
import ValidToField from "~/components/Norm/Metadatafields/ValidToField.vue";
import { isPrototypeProfile } from "~/utils/config";

interface Props {
  validFrom?: Dayjs;
  validTo?: Dayjs;
}

defineProps<Props>();
</script>

<template>
  <div
    v-if="!isPrototypeProfile()"
    class="container mb-24 flex flex-col space-y-16 space-x-0 md:space-y-0 lg:flex-row lg:space-x-24"
    data-testid="metadata"
  >
    <ValidFromField :value="validFrom" />
    <ValidToField :value="validTo" />
  </div>
</template>
