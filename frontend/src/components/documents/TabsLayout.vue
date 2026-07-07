<script setup lang="ts">
import { Tab, TabList, Tabs } from "primevue";
import { NuxtLink } from "#components";

export type TabView = {
  label: string;
  path: string;
  icon: Component;
  analyticsId?: string;
};

const { views } = defineProps<{
  views: OneOrMore<TabView>;
}>();

const route = useRoute();

const currentView = computed(
  () => route.query.view?.toString() ?? views[0].path,
);
</script>

<template>
  <div>
    <div class="border-b border-gray-400">
      <nav class="content-wrapper -mb-1 overflow-x-auto pt-1" aria-label="Tab">
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
              :to="{ query: { ...route.query, view: view.path } }"
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

    <div id="content" class="min-h-96 bg-white print:py-0">
      <div class="content-wrapper">
        <slot :name="currentView" />
      </div>
    </div>
  </div>
</template>
