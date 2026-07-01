<script setup lang="ts">
import type { BreadcrumbItem } from "~/components/Breadcrumbs.vue";
import type { MetadataItem } from "~/components/Metadata.vue";
import type { TabView } from "~/components/TabsLayout.vue";
import BreadcrumbPageLayout from "./breadcrumbPage.vue";

const { titlePlaceholder = "Titelzeile nicht vorhanden", views } = defineProps<{
  title?: string;
  titlePlaceholder?: string;
  isEmptyDocument?: boolean;
  breadcrumbs?: BreadcrumbItem[];
  metadata?: MetadataItem[];
  views: OneOrMore<TabView>;
}>();
</script>

<template>
  <BreadcrumbPageLayout>
    <template #breadcrumb>
      <div class="flex items-center gap-4 md:gap-16 print:hidden">
        <Breadcrumbs :items="breadcrumbs" class="grow" />
        <slot name="actionMenu" />
      </div>
    </template>

    <div>
      <!-- Header -->
      <div class="wrapper text-left">
        <DocumentsDocumentTitle
          :title="title"
          :placeholder="titlePlaceholder"
        />

        <Metadata
          v-if="metadata?.length"
          :items="metadata"
          class="my-24 sm:my-32 md:my-40"
        />
      </div>

      <!-- Empty documents -->
      <div
        v-if="isEmptyDocument"
        class="min-h-96 border-t border-t-gray-400 bg-white print:py-0"
      >
        <div class="wrapper">
          <slot name="details" />
        </div>
      </div>

      <div v-else>
        <!-- Tabs -->
        <TabsLayout :views>
          <template v-for="(_, name) in $slots" #[name]>
            <slot :name="name" />
          </template>
        </TabsLayout>
      </div>
    </div>
  </BreadcrumbPageLayout>
</template>
