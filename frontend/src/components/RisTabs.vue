<script setup lang="ts">
import { onMounted, ref } from "vue";
import type { RouteLocationRaw } from "#vue-router";

export interface LinkTab {
  id: string;
  href: RouteLocationRaw;
  label: string;
  icon?: Component;
  iconClass?: string;
}

const props = defineProps<{
  tabs: LinkTab[];
  defaultTab?: string;
}>();

const isClient = ref(false);
onMounted(() => (isClient.value = true));

const activeTab = ref<string>(props.defaultTab || props.tabs[0]?.id || "");

function handleTabClick(tabId: string, event: Event) {
  if (isClient.value) {
    event.preventDefault();
    activeTab.value = tabId;
  }
}

const getTabClasses = (tabId: string) => {
  const isActive = activeTab.value === tabId;
  return `ris-body2-bold h-64 py-4 pl-20 pr-24 border-b-4 border-b-transparent outline-blue-800 outline-0 -outline-offset-4 focus-visible:outline-4 inline-flex items-center gap-8 no-underline ${isActive ? "border-gray-600 text-black shadow-active-tab bg-white z-10" : "text-blue-800 hover:border-b-blue-800 cursor-pointer"}`;
};
</script>

<template>
  <nav
    class="relative before:absolute before:bottom-0 before:left-[50%] before:h-px before:w-full before:-translate-x-1/2 before:bg-gray-600 print:hidden"
    aria-label="Tab-Liste"
  >
    <div class="container flex">
      <NuxtLink
        v-for="tab in tabs"
        :key="tab.id"
        :to="tab.href"
        :aria-current="activeTab === tab.id ? 'page' : undefined"
        :class="getTabClasses(tab.id)"
        @click="handleTabClick(tab.id, $event)"
      >
        <component :is="tab.icon" v-if="tab.icon" aria-hidden="true" />
        {{ tab.label }}
      </NuxtLink>
    </div>
  </nav>
  <div>
    <slot :active-tab="activeTab" :is-client="isClient" />
  </div>
</template>
