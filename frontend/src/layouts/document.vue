<script setup lang="ts">
import { Tab, TabList, Tabs } from "primevue";
import BaseLayout from "./base.vue";
import { NuxtLink } from "#components";
import type { BreadcrumbItem } from "~/components/Breadcrumbs.vue";
import type { MetadataItem } from "~/components/Metadata.vue";

export type DocumentView = {
  label: string;
  path: string;
  icon: Component;
  analyticsId?: string;
};

const { titlePlaceholder = "Titelzeile nicht vorhanden", views } = defineProps<{
  title?: string;
  titlePlaceholder?: string;
  isEmptyDocument?: boolean;
  breadcrumbs?: BreadcrumbItem[];
  metadata?: MetadataItem[];
  views: OneOrMore<DocumentView>;
}>();

const route = useRoute();

const currentView = computed(
  () => route.query.view?.toString() ?? views[0].path,
);
</script>

<template>
  <BaseLayout>
    <div class="pt-32 lg:pt-64">
      <!-- Header -->
      <div class="container text-left">
        <div class="flex items-center gap-8 print:hidden">
          <Breadcrumbs :items="breadcrumbs" class="grow" />
          <slot name="actionMenu" />
        </div>

        <DocumentsDocumentTitle
          :title="title"
          :placeholder="titlePlaceholder"
        />

        <Metadata v-if="metadata?.length" :items="metadata" class="mb-48" />
      </div>

      <!-- Empty documents -->
      <div
        v-if="isEmptyDocument"
        class="min-h-96 border-t border-t-gray-400 bg-white print:py-0"
      >
        <div class="container pt-24 pb-80">
          <slot name="details" />
        </div>
      </div>

      <div v-else>
        <!-- Tabs -->
        <div class="border-b border-gray-400">
          <nav class="container -mb-1">
            <Tabs :value="currentView" :show-navigators="false">
              <TabList>
                <!-- Note that we need to override aria-controls manually,
                otherwise PrimeVue will insert the ID of a tab panel that
                doesn't exist -->
                <Tab
                  v-for="view in views"
                  :key="view.path"
                  :value="view.path"
                  :as="NuxtLink"
                  :to="{ query: { view: view.path } }"
                  :aria-controls="undefined"
                  :data-attr="view.analyticsId"
                  class="flex items-center gap-8"
                >
                  <component :is="view.icon" />
                  {{ view.label }}
                </Tab>
              </TabList>
            </Tabs>
          </nav>
        </div>

        <div class="min-h-96 bg-white py-24 print:py-0">
          <div class="container">
            <slot :name="currentView" />
          </div>
        </div>
      </div>
    </div>
  </BaseLayout>
</template>
