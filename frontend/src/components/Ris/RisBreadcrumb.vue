<script setup lang="ts">
import { DocumentKind } from "~/types";
import ChevronRightIcon from "~icons/material-symbols/chevron-right";
import HomeFilledIcon from "~icons/material-symbols/home";
import HomeOutlineIcon from "~icons/material-symbols/home-outline";

export interface BreadcrumbItem {
  label: string;
  route?: string;
  type?: string;
}

interface Props {
  type?: "norm" | "caselaw";
  items?: BreadcrumbItem[];
  title?: string;
  basePath?: string;
}

const props = defineProps<Props>();

const items = computed(() => {
  const items: BreadcrumbItem[] = [
    {
      label: "Startseite",
      type: "home",
      route: "/",
    },
  ];

  if (props.type) {
    const label =
      props.type === "norm"
        ? "Gesetze & Verordnungen"
        : "Gerichtsentscheidungen";
    const documentKind =
      props.type === "norm" ? DocumentKind.Norm : DocumentKind.CaseLaw;
    const route = `/search?category=${documentKind}`;
    items.push({
      label,
      route,
    });
  }

  if (props.title) {
    items.push({
      label: props.title,
      route: props.basePath,
    });
  }

  return items.concat(...(props.items ?? []));
});
const isHomeHovered = ref(false);
</script>

<template>
  <Breadcrumb :model="items" aria-label="Pfadnavigation">
    <template #item="{ item, props: breadcrumbProps }">
      <router-link
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
          <template v-if="item.type === 'home'">
            <span
              aria-hidden="true"
              @mouseenter="isHomeHovered = true"
              @mouseleave="isHomeHovered = false"
            >
              <template v-if="isHomeHovered">
                <HomeFilledIcon class="-ml-2" />
              </template>
              <template v-else>
                <HomeOutlineIcon class="-ml-2" />
              </template>
            </span>
            <span class="sr-only">Startseite</span>
          </template>
          <span v-else class="ris-body2-regular line-clamp-1">
            {{ item.label }}
          </span>
        </a>
      </router-link>
      <span v-else class="ris-body2-regular line-clamp-1 text-gray-900">{{
        item.label
      }}</span>
    </template>
    <template #separator>
      <ChevronRightIcon />
    </template>
  </Breadcrumb>
</template>
