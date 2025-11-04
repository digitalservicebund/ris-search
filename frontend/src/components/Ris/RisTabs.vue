<script setup lang="ts">
import { ref, onMounted } from "vue";
import {
  tabBaseClasses,
  tabActiveClasses,
  tabInactiveClasses,
  tabListContentClass,
  tabListClass,
} from "~/components/Tabs.styles";

export interface LinkTab {
  id: string;
  href: string;
  label: string;
  ariaLabel: string;
  icon?: Component;
  iconClass?: string;
}

const props = defineProps<{
  tabs: LinkTab[];
  ariaLabel: string;
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
  return `${tabBaseClasses} ${isActive ? tabActiveClasses : tabInactiveClasses}`;
};
</script>

<template>
  <nav :class="tabListContentClass" :aria-label="ariaLabel">
    <div :class="tabListClass">
      <a
        v-for="tab in tabs"
        :key="tab.id"
        :href="tab.href"
        :aria-current="activeTab === tab.id ? 'page' : undefined"
        :aria-label="tab.ariaLabel"
        :class="getTabClasses(tab.id)"
        @click="handleTabClick(tab.id, $event)"
      >
        <component :is="tab.icon" v-if="tab.icon" aria-hidden="true" />
        {{ tab.label }}
      </a>
    </div>
  </nav>
  <div>
    <slot :active-tab="activeTab" :is-client="isClient" />
  </div>
</template>
