<script setup lang="ts">
import { Breadcrumb } from "primevue";
import { NuxtLink } from "#components";
import type { RouteLocationRaw } from "#vue-router";
import ChevronRightIcon from "~icons/ic/outline-chevron-right";

export interface BreadcrumbItem {
  label: string;
  route?: RouteLocationRaw;
  type?: string;
}

interface Props {
  items?: BreadcrumbItem[];
}

const props = defineProps<Props>();

const items = computed(() => {
  const items: BreadcrumbItem[] = [
    {
      label: "Startseite",
      type: "home",
      route: "/",
    },
    ...(props.items ?? []),
  ];

  return items;
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
          class="link-hover"
          @click="navigate"
        >
          <template v-if="item.type === 'home'">Startseite</template>
          <span v-else class="ris-body2-regular line-clamp-1">
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
