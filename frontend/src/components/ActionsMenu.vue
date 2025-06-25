<script setup lang="ts">
import type { MenuItem } from "primevue/menuitem";
import Menu, { type MenuMethods } from "primevue/menu";
import Button from "primevue/button";
import MdiDotsVertical from "~icons/mdi/dots-vertical";

export type ActionsMenuProps = { items: ActionMenuItem[] };
const { items } = defineProps<ActionsMenuProps>();

export type ActionMenuItem = Omit<MenuItem, "icon"> & {
  disabled?: boolean;
  iconComponent: Component;
};

const menuRef = useTemplateRef<MenuMethods>("menu");

const toggle = (event: Event) => {
  menuRef.value?.toggle(event);
};

defineExpose({ toggle });
</script>

<template>
  <div class="hidden items-center *:-mx-4 sm:flex">
    <Button
      v-for="item in items"
      :key="item.key"
      text
      :disabled="item.disabled"
      :aria-label="item.label"
      :href="item.url"
      :as="item.url ? 'a' : undefined"
      @click.prevent="item.command && item.command()"
    >
      <template #icon
        ><component :is="(item as ActionMenuItem).iconComponent"
      /></template>
    </Button>
  </div>
  <Button class="sm:hidden" text aria-label="Aktionen anzeigen" @click="toggle">
    <template #icon>
      <MdiDotsVertical />
    </template>
  </Button>
  <Menu ref="menu" :popup="true" :model="items" class="print:hidden">
    <template #item="{ item }">
      <div
        class="flex h-full items-center py-4"
        data-pc-section="itemcontent"
        bis_skin_checked="1"
      >
        <a
          v-if="!item.disabled"
          class="flex cursor-pointer items-center gap-8 no-underline"
          :href="item.url"
          data-pc-section="itemlink"
          :data-attr="item.dataAttribute"
        >
          <component :is="(item as ActionMenuItem).iconComponent" />
          <span>{{ item.label }}</span>
        </a>
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
</template>
