<script setup lang="ts">
import type { LegislationWork } from "~/types";
import { translateLegalForce } from "~/utils/dateFormatting";

const props = defineProps<{ item: LegislationWork }>();

const temporalCoverage = computed(() =>
  splitTemporalCoverage(props.item.workExample.temporalCoverage),
);
</script>

<template>
  <tr>
    <td class="align-top">
      <NuxtLink
        :to="`/norms/${item.workExample.legislationIdentifier}`"
        class="ris-link1-regular text-xl"
        >{{ temporalCoverage?.[0] }}</NuxtLink
      >
    </td>
    <td class="space-y-8 py-2">
      <div class="flex gap-x-24">
        <MetadataField
          id="valid-from"
          label="Gültig von"
          :value="temporalCoverage?.[0]"
        />
        <MetadataField
          id="valid-to"
          label="Gültig bis"
          :value="temporalCoverage?.[1]"
        />
      </div>
      <MetadataField
        id="status"
        label="Status"
        :value="translateLegalForce(item.workExample.legislationLegalForce)"
      />
    </td>
  </tr>
</template>
