<script setup lang="ts">
import { NuxtLink } from "#components";
import type { RouteLocationRaw } from "#vue-router";
import { Breadcrumb } from "primevue";
import ChevronRightIcon from "~icons/ic/outline-chevron-right";

export type BreadcrumbItem = {
  label: string;
  route?: RouteLocationRaw;
};

const props = defineProps<{
  items?: BreadcrumbItem[];
}>();

const items = computed(() => {
  const breadcrumbItems: BreadcrumbItem[] = [
    { label: "Startseite", route: "/" },
    ...(props.items ?? []),
  ];

  return breadcrumbItems;
});
</script>

<template>
  <Breadcrumb :model="items" aria-label="Pfadnavigation">
    <template #item="{ item, props: breadcrumbProps }">
      <NuxtLink
        v-if="item.route && item != items[items.length - 1]"
        v-slot="{ href, navigate }"
        :to="item.route"
        custom
      >
        <a
          :href="href"
          v-bind="breadcrumbProps.action"
          class="ris-link2-regular link-hover"
          @click="navigate"
        >
          <span class="ris-body2-regular line-clamp-1">
            {{ item.label }}
          </span>
        </a>
      </NuxtLink>
      <span v-else class="ris-body2-regular line-clamp-1 text-gray-900">{{
        item.label
      }}</span>
    </template>
    <template #separator>
      <ChevronRightIcon />
    </template>
  </Breadcrumb>
</template>
