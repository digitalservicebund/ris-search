<script setup lang="ts">
import { Tab, TabList, Tabs } from "primevue";
import { NuxtLink } from "#components";
import type { BreadcrumbItem } from "~/components/Breadcrumbs.vue";
import type { MetadataItem } from "~/components/Metadata.vue";
import BreadcrumbPageLayout from "./breadcrumbPage.vue";

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
  <BreadcrumbPageLayout>
    <template #breadcrumb>
      <div class="flex items-center gap-4 md:gap-16 print:hidden">
        <Breadcrumbs :items="breadcrumbs" class="grow" />
        <slot name="actionMenu" />
      </div>
    </template>
    <template #default>
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
            class="my-24 sm:my-32 md:my-40 2xl:my-48"
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
          <div class="border-b border-gray-400">
            <nav class="wrapper -mb-1" aria-label="Tab">
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

          <div id="content" class="min-h-96 bg-white">
            <div class="wrapper">
              <slot :name="currentView" />
            </div>
          </div>
        </div>
      </div>
    </template>
  </BreadcrumbPageLayout>
</template>
