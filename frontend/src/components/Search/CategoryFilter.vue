<script setup lang="ts">
import type { MenuItem, MenuItemCommandEvent } from "primevue/menuitem";
import PanelMenu from "primevue/panelmenu";
import {
  computeExpandedKeys,
  categoryFilterItems,
} from "~/utils/search/categoryFilter";

const model = defineModel<string>({ required: true });

const update = (event: MenuItemCommandEvent) => {
  let key = event.item.key;
  if (key?.endsWith(".all")) {
    key = key.substring(0, key.length - 4);
  }
  if (key) model.value = key;
};

const addUpdate = (items: MenuItem[]) => {
  return items.map(
    (item: MenuItem): MenuItem => ({
      ...item,
      command: update,
      items: item.items ? addUpdate(item.items) : item.items,
    }),
  );
};

const selectedItems = addUpdate(categoryFilterItems);

const expandedKeys = computed(() => computeExpandedKeys(model.value));
</script>
<template>
  <fieldset>
    <legend class="sr-only">Dokumentarten</legend>
    <PanelMenu
      id="panelMenu"
      :model="selectedItems"
      :expanded-keys="expandedKeys"
      class="w-full md:w-200"
      ><template #submenuicon><i /></template
    ></PanelMenu>
  </fieldset>
</template>
