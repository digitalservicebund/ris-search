<script setup lang="ts">
import type { MenuItem, MenuItemCommandEvent } from "primevue/menuitem";
import PanelMenu from "primevue/panelmenu";
import { computeExpandedKeys, items } from "./CategoryFilter.data";
import { useSimpleSearchParamsStore } from "~/stores/searchParams";

const store = useSimpleSearchParamsStore();

const update = (event: MenuItemCommandEvent) => {
  let key = event.item.key;
  if (key?.endsWith(".all")) {
    key = key.substring(0, key.length - 4);
  }
  if (key) store.category = key;
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

const model = addUpdate(items);

const expandedKeys = computed(() => computeExpandedKeys(store.category));
</script>
<template>
  <fieldset>
    <legend class="sr-only">Dokumentarten</legend>
    <PanelMenu
      id="panelMenu"
      :model="model"
      :expanded-keys="expandedKeys"
      class="w-full md:w-200"
      :pt="{
        headercontent: { class: 'group' },
        headerlink: { class: 'no-underline group-hover:underline' },
        itemlink: { class: 'no-underline group-hover:underline' },
      }"
      ><template #submenuicon><i /></template
    ></PanelMenu>
  </fieldset>
</template>
