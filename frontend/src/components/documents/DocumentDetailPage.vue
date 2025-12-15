<script setup lang="ts">
import { computed } from "vue";
import DocumentTitle from "./DocumentTitle.vue";
import type { BreadcrumbItem } from "~/components/Breadcrumbs.vue";
import IncompleteDataMessage from "~/components/documents/IncompleteDataMessage.vue";
import type { MetadataItem } from "~/components/Metadata.vue";
import SidebarLayout from "~/components/SidebarLayout.vue";
import IcBaselineSubject from "~icons/ic/baseline-subject";
import IcOutlineInfo from "~icons/ic/outline-info";

export interface DocumentDetailPageProps {
  title?: string;
  titlePlaceholder: string;
  isEmptyDocument?: boolean;
  breadcrumbItems: BreadcrumbItem[];
  metadataItems: MetadataItem[];
  documentHtmlClass: string;
  html?: string;
}

const {
  title,
  titlePlaceholder,
  isEmptyDocument = false,
  breadcrumbItems,
  metadataItems,
  documentHtmlClass,
  html,
} = defineProps<DocumentDetailPageProps>();

const tabs = computed(() => [
  {
    id: "text",
    href: "#text",
    label: "Text",
    icon: IcBaselineSubject,
  },
  {
    id: "details",
    href: "#details",
    label: "Details",
    icon: IcOutlineInfo,
  },
]);

const detailsTabPanelId = useId();
</script>

<template>
  <div class="container text-left">
    <div class="flex items-center gap-8 print:hidden">
      <Breadcrumbs :items="breadcrumbItems" class="grow" />
      <slot name="actionMenu" />
    </div>
    <DocumentTitle :title="title" :placeholder="titlePlaceholder" />
    <Metadata :items="metadataItems" class="mb-48" />
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
    <RisTabs :tabs="tabs">
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
          :aria-labelledby="detailsTabPanelId"
        >
          <div class="container pb-56">
            <slot name="details" :details-tab-panel-id="detailsTabPanelId" />
          </div>
        </section>
      </template>
    </RisTabs>
  </div>
</template>
