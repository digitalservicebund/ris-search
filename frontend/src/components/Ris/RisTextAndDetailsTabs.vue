<script setup lang="ts">
import { computed } from "vue";
import SidebarLayout from "~/components/CustomLayouts/SidebarLayout.vue";
import IncompleteDataMessage from "~/components/IncompleteDataMessage.vue";
import RisTabs from "~/components/Ris/RisTabs.vue";
import { tabPanelClass } from "~/utils/tabsStyles";
import IcBaselineSubject from "~icons/ic/baseline-subject";
import IcOutlineInfo from "~icons/ic/outline-info";

export interface RisTextAndDetailsTabsProps {
  tabsLabel: string;
  textTabAriaLabel: string;
  detailsTabAriaLabel: string;
  documentHtmlClass: string;
  html?: string;
}

const {
  tabsLabel,
  textTabAriaLabel,
  detailsTabAriaLabel,
  documentHtmlClass,
  html,
} = defineProps<RisTextAndDetailsTabsProps>();

const tabs = computed(() => [
  {
    id: "text",
    href: "#text",
    label: "Text",
    ariaLabel: textTabAriaLabel,
    icon: IcBaselineSubject,
  },
  {
    id: "details",
    href: "#details",
    label: "Details",
    ariaLabel: detailsTabAriaLabel,
    icon: IcOutlineInfo,
  },
]);
</script>

<template>
  <RisTabs :tabs="tabs" :label="tabsLabel">
    <template #default="{ activeTab, isClient }">
      <section
        id="text"
        :class="tabPanelClass"
        :hidden="isClient && activeTab !== 'text'"
        aria-labelledby="textSectionHeading"
      >
        <SidebarLayout class="container">
          <template #content>
            <h2 id="textSectionHeading" class="sr-only">Text</h2>
            <IncompleteDataMessage class="mb-16" />
            <div :class="documentHtmlClass" v-html="html"></div>
          </template>
          <template #sidebar>
            <slot name="sidebar" />
          </template>
        </SidebarLayout>
      </section>
      <section
        id="details"
        :class="tabPanelClass"
        :hidden="isClient && activeTab !== 'details'"
        aria-labelledby="detailsTabPanelTitle"
      >
        <div class="container pb-56">
          <slot name="details" />
        </div>
      </section>
    </template>
  </RisTabs>
</template>
