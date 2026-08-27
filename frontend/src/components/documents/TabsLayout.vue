<script setup lang="ts">
import { NuxtLink } from "#components";

export type TabView = {
  label: string;
  path: string;
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
      <nav class="-mb-px" aria-label="Tab">
        <UiTabs class="content-gutters">
          <UiTab
            v-for="view in views"
            :key="view.path"
            :active="view.path === currentView"
            :as="NuxtLink"
            :to="{ query: { ...route.query, view: view.path } }"
            :data-attr="view.analyticsId"
          >
            {{ view.label }}
          </UiTab>
        </UiTabs>
      </nav>
    </div>

    <div id="content" class="min-h-96 bg-white print:py-0">
      <div class="content-wrapper">
        <slot :name="currentView" />
      </div>
    </div>
  </div>
</template>
