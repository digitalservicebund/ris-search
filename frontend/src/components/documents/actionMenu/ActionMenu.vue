<script setup lang="ts">
import Button from "primevue/button";
import Menu, { type MenuMethods } from "primevue/menu";
import type { MenuItem } from "primevue/menuitem";
import { useToast } from "primevue/usetoast";
import { NuxtLink } from "#components";
import { createActionMenuItems } from "~/utils/actionMenu";
import MdiDotsVertical from "~icons/mdi/dots-vertical";

export type ActionMenuProps = {
  link?: {
    url: string;
    label: string;
  };
  permalink: {
    url: string;
    label: string;
    disabled?: boolean;
  };
  xmlUrl?: string;
  translationUrl?: string;
};

export type ActionMenuItem = Omit<MenuItem, "icon"> & {
  iconComponent: Component;
  analyticsId?: string;
};

const props = defineProps<ActionMenuProps>();
const toastService = useToast();

async function copyUrlCommand(url: string) {
  await navigator.clipboard.writeText(url);
  toastService.add({
    severity: "success",
    summary: "Kopiert!",
    life: 3000,
    closable: false,
  });
}

async function navigationToXml(xmlUrl: string) {
  await navigateTo(xmlUrl, { external: true });
}

const actions = createActionMenuItems(props, copyUrlCommand, navigationToXml);
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
        :data-attr="(item as ActionMenuItem).analyticsId"
        @click.prevent="item.command && item.command()"
      >
        <template #icon
          ><component :is="(item as ActionMenuItem).iconComponent"
        /></template>
      </Button>
    </div>
  </div>
</template>
