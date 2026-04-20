<script setup lang="ts">
import Accordion from "primevue/accordion";
import AccordionContent from "primevue/accordioncontent";
import AccordionHeader from "primevue/accordionheader";
import AccordionPanel from "primevue/accordionpanel";
import { computed } from "vue";
import IcOutlineExpandCircleDown from "~icons/ic/outline-expand-circle-down";

const props = defineProps<{
  headerCollapsed: string;
  headerExpanded: string;
}>();

const activeModel = defineModel<boolean>({ default: false });

const activePanel = computed({
  get: () => {
    return activeModel.value ? "0" : "";
  },
  set: (value) => {
    activeModel.value = value === "0";
  },
});
</script>

<template>
  <Accordion
    v-model:value="activePanel"
    expand-icon="hidden"
    collapse-icon="hidden"
  >
    <AccordionPanel value="0">
      <AccordionHeader
        :pt="{
          root: 'ris-label2-bold flex cursor-pointer flex-row items-center gap-8 text-blue-800 outline-blue-800 focus-visible:outline-4 mb-6 outline-offset-4',
        }"
      >
        <template v-if="activePanel === '0'">
          <IcOutlineExpandCircleDown class="rotate-180" />
          <div>{{ props.headerExpanded }}</div>
        </template>

        <template v-else>
          <IcOutlineExpandCircleDown />
          <div>{{ props.headerCollapsed }}</div>
        </template>
      </AccordionHeader>

      <AccordionContent>
        <slot />
      </AccordionContent>
    </AccordionPanel>
  </Accordion>
</template>
