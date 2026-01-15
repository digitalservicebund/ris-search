<script setup lang="ts">
import Button from "primevue/button";
import Menu, { type MenuMethods } from "primevue/menu";
import type { MenuItem } from "primevue/menuitem";
import { NuxtLink } from "#components";
import MdiDotsVertical from "~icons/mdi/dots-vertical";

export type ActionMenuItem1 = Omit<MenuItem, "icon"> & {
  iconComponent: Component;
};

const { actions } = defineProps<{ actions: ActionMenuItem1[] }>();
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
        <div
          class="flex h-full items-center py-4"
          data-pc-section="itemcontent"
          bis_skin_checked="1"
        >
          <NuxtLink
            v-if="!item.disabled"
            class="flex cursor-pointer items-center gap-8 no-underline"
            :to="item.url"
            data-pc-section="itemlink"
          >
            <component :is="(item as ActionMenuItem1).iconComponent" />
            <span>{{ item.label }}</span>
          </NuxtLink>
          <span
            v-else
            class="flex cursor-not-allowed items-center gap-8 text-gray-800"
          >
            <component :is="(item as ActionMenuItem1).iconComponent" />
            <span>{{ item.label }}</span>
          </span>
        </div>
      </template>
    </Menu>
  </div>
  <div class="hidden sm:flex">
    <div class="flex items-center *:-mx-4">
      <Button
        v-for="item in actions"
        :key="item.key"
        v-tooltip.bottom="item.label"
        text
        :disabled="item.disabled"
        :aria-label="item.label"
        :to="item.url"
        :as="item.url ? NuxtLink : undefined"
        external
        @click="item.command && item.command()"
      >
        <template #icon>
          <component :is="(item as ActionMenuItem1).iconComponent" />
        </template>
      </Button>
    </div>
  </div>
</template>
