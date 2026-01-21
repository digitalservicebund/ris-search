<script setup lang="ts">
import Button from "primevue/button";
import Menu, { type MenuMethods } from "primevue/menu";
import type { MenuItem } from "primevue/menuitem";
import { NuxtLink } from "#components";
import MdiDotsVertical from "~icons/mdi/dots-vertical";

export type ActionMenuItem = Omit<MenuItem, "icon"> & {
  iconComponent: Component;
  analyticsId?: string;
  command?: () => void | Promise<void>;
};

const { actions } = defineProps<{ actions: ActionMenuItem[] }>();
const menuRef = useTemplateRef<MenuMethods>("menu");

const toggle = (event: Event) => {
  menuRef.value?.toggle(event);
};
</script>

<template>
  <div class="sm:hidden">
    <Button text aria-label="Aktionen anzeigen" @click="toggle">
      <template #icon>
        <MdiDotsVertical />
      </template>
    </Button>
    <Menu ref="menu" :popup="true" :model="actions" class="print:hidden">
      <template #item="{ item }">
        <NuxtLink
          v-if="!item.disabled"
          class="flex cursor-pointer items-center gap-8 no-underline"
          :to="item.url"
          data-pc-section="itemlink"
          :data-attr="(item as ActionMenuItem).analyticsId"
        >
          <component :is="(item as ActionMenuItem).iconComponent" />
          <span>{{ item.label }}</span>
        </NuxtLink>
        <span
          v-else
          class="flex cursor-not-allowed items-center gap-8 text-gray-800"
        >
          <component :is="(item as ActionMenuItem).iconComponent" />
          <span>{{ item.label }}</span>
        </span>
      </template>
    </Menu>
  </div>

  <ul role="menu" class="hidden items-center *:-mx-4 sm:flex">
    <li v-for="item in actions" :key="item.label" role="menuitem">
      <Button
        v-tooltip.bottom="item.label"
        text
        :disabled="item.disabled"
        :aria-label="item.label"
        :to="item.url"
        :as="item.url ? NuxtLink : undefined"
        :data-attr="(item as ActionMenuItem).analyticsId"
        external
        @click="item.command"
      >
        <template #icon>
          <component :is="(item as ActionMenuItem).iconComponent" />
        </template>
      </Button>
    </li>
  </ul>
</template>
