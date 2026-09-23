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

const singleTab = computed(() => views.length === 1);

const currentView = computed(() => {
  const allowedPaths = views.map((view) => view.path);
  const queryPath = route.query.view?.toString();

  if (queryPath && allowedPaths.includes(queryPath)) {
    return queryPath;
  } else {
    return views[0].path;
  }
});
</script>

<template>
  <div>
    <div v-if="!singleTab">
      <nav class="-mb-px" aria-label="Tab">
        <UiTabs scroller-class="content-gutters">
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

    <div
      id="content"
      class="min-h-96 border-t border-t-gray-400 bg-white print:py-0"
    >
      <div class="content-wrapper">
        <slot :name="currentView" />
      </div>
    </div>
  </div>
</template>
