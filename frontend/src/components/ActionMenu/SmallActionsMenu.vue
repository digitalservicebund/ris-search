<script setup lang="ts">
import Button from "primevue/button";
import Menu, { type MenuMethods } from "primevue/menu";
import type { ActionMenuItem } from "~/components/ActionMenu/ActionsMenu.vue";
import MdiDotsVertical from "~icons/mdi/dots-vertical";

const { actions } = defineProps<{ actions: ActionMenuItem[] }>();
const menuRef = useTemplateRef<MenuMethods>("menu");

const toggle = (event: Event) => {
  menuRef.value?.toggle(event);
};

defineExpose({ toggle });
</script>

<template>
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
