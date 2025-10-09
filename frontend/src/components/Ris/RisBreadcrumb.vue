<script setup lang="ts">
import { Breadcrumb } from "primevue";
import { DocumentKind } from "~/types";
import ChevronRightIcon from "~icons/material-symbols/chevron-right";
import HomeFilledIcon from "~icons/material-symbols/home";
import HomeOutlineIcon from "~icons/material-symbols/home-outline";

export interface BreadcrumbItem {
  label: string;
  route?: string;
  type?: string;
}

type BreadcrumbType = "norm" | "caselaw" | "literature";

interface Props {
  type?: BreadcrumbType;
  items?: BreadcrumbItem[];
  title?: string;
  basePath?: string;
}

function getItemForType(type: BreadcrumbType): BreadcrumbItem {
  let label: string;
  let documentKind: string;

  switch (type) {
    case "norm":
      label = "Gesetze & Verordnungen";
      documentKind = DocumentKind.Norm;
      break;
    case "caselaw":
      label = "Gerichtsentscheidungen";
      documentKind = DocumentKind.CaseLaw;
      break;
    case "literature":
      label = "Literaturnachweise";
      documentKind = DocumentKind.Literature;
  }

  const route = `/search?category=${documentKind}`;

  return {
    label,
    route,
  };
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
    const item = getItemForType(props.type);
    items.push(item);
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
