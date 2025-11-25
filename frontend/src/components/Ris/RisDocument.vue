<script setup lang="ts">
import ContentWrapper from "~/components/CustomLayouts/ContentWrapper.vue";
import RisBreadcrumb, {
  type BreadcrumbItem,
} from "~/components/Ris/RisBreadcrumb.vue";
import RisDocumentTitle from "~/components/Ris/RisDocumentTitle.vue";
import type { RisTextAndDetailsTabsProps } from "~/components/Ris/RisTextAndDetailsTabs.vue";

export interface RisDocumentProps {
  title?: string;
  titlePlaceholder: string;
  isEmptyDocument?: boolean;
  breadcrumbItems: BreadcrumbItem[];
  tabsProps: RisTextAndDetailsTabsProps;
}

const {
  title,
  titlePlaceholder,
  isEmptyDocument = false,
  breadcrumbItems,
  tabsProps,
} = defineProps<RisDocumentProps>();
</script>

<template>
  <ContentWrapper border>
    <div class="container text-left">
      <div class="flex items-center gap-8 print:hidden">
        <RisBreadcrumb :items="breadcrumbItems" class="grow" />
        <slot name="actionsMenu" />
      </div>
      <RisDocumentTitle :title="title" :placeholder="titlePlaceholder" />
      <slot name="metadata" />
    </div>
    <div
      v-if="isEmptyDocument"
      class="min-h-96 border-t border-t-gray-400 bg-white print:py-0"
    >
      <div class="container pt-24 pb-80">
        <slot name="details" />
      </div>
    </div>
    <div v-else>
      <RisTextAndDetailsTabs
        :tabs-label="tabsProps.tabsLabel"
        :text-tab-aria-label="tabsProps.textTabAriaLabel"
        :details-tab-aria-label="tabsProps.detailsTabAriaLabel"
        :document-html-class="tabsProps.documentHtmlClass"
        :html="tabsProps.html"
      >
        <template #sidebar>
          <slot name="sidebar" />
        </template>
        <template #details>
          <slot name="details" />
        </template>
      </RisTextAndDetailsTabs>
    </div>
  </ContentWrapper>
</template>
