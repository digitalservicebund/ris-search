<script setup lang="ts">
import PrimevueAccordion from "primevue/accordion";
import AccordionContent from "primevue/accordioncontent";
import AccordionHeader from "primevue/accordionheader";
import AccordionPanel from "primevue/accordionpanel";
import GravityUiCircleChevronDown from "~icons/gravity-ui/circle-chevron-down";
import GravityUiCircleChevronDownFill from "~icons/gravity-ui/circle-chevron-down-fill";
import GravityUiCircleChevronUp from "~icons/gravity-ui/circle-chevron-up";
import GravityUiCircleChevronUpFill from "~icons/gravity-ui/circle-chevron-up-fill";

const isHovered = ref(false);

const props = defineProps<{
  headerCollapsed: string;
  headerExpanded: string;
  value?: string;
}>();

const activePanel = ref(props.value || "");
const accordionHeaderClasses = "flex flex-row space-x-8 py-24 items-center";
</script>

<template>
  <PrimevueAccordion
    v-model:value="activePanel"
    expand-icon="hidden"
    collapse-icon="hidden"
    class="p"
  >
    <AccordionPanel unstyled value="0">
      <AccordionHeader unstyled>
        <div
          v-if="activePanel === '0'"
          :class="accordionHeaderClasses"
          @mouseover="isHovered = true"
          @mouseleave="isHovered = false"
        >
          <div class="flex flex-col gap-4">
            <GravityUiCircleChevronUpFill v-if="isHovered" />
            <GravityUiCircleChevronUp v-else />
          </div>
          <div class="flex flex-col gap-4">{{ props.headerExpanded }}</div>
        </div>
        <div
          v-else
          :class="accordionHeaderClasses"
          @mouseover="isHovered = true"
          @mouseleave="isHovered = false"
        >
          <div class="flex flex-col gap-4">
            <GravityUiCircleChevronDownFill v-if="isHovered" />
            <GravityUiCircleChevronDown v-else />
          </div>
          <div class="flex flex-col gap-4">{{ props.headerCollapsed }}</div>
        </div>
      </AccordionHeader>
      <AccordionContent unstyled>
        <slot></slot>
      </AccordionContent>
    </AccordionPanel>
  </PrimevueAccordion>
</template>
