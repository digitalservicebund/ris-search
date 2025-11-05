<script setup lang="ts">
import { Button } from "primevue";
import type { DataField } from "~/utils/search/dataFields";
import IcBaselinePlus from "~icons/ic/baseline-plus";

const { dataFields = [] } = defineProps<{
  /** Data fields that should be displayed. */
  dataFields?: DataField[];
}>();

defineEmits<{
  /** Emitted when a data field has been clicked, contains the data field. */
  clickDataField: [value: DataField];
}>();
</script>

<template>
  <ul
    aria-label="Durchsuchbare Datenfelder"
    class="flex flex-col gap-4 md:flex-row md:flex-wrap"
  >
    <li v-for="field in dataFields" :key="field.pattern">
      <!-- z-index is incresed for focused buttons to prevent visual glitches
      from overlapping buttons/focus outlines -->
      <Button
        severity="info"
        size="small"
        rounded
        :aria-label="`${field.label} suchen`"
        :label="field.label"
        class="gap-4! pl-12! text-nowrap focus-visible:z-10"
        @click="$emit('clickDataField', field)"
      >
        <template #icon>
          <IcBaselinePlus />
        </template>
      </Button>
    </li>
  </ul>
</template>
