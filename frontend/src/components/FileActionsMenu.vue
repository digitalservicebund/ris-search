<script setup lang="ts">
import type { MenuItem } from "primevue/menuitem";
import Menu, { type MenuMethods } from "primevue/menu";
import Button from "primevue/button";
import MdiDotsVertical from "~icons/mdi/dots-vertical";
import MaterialSymbolsLink from "~icons/material-symbols/link";
import XMLIcon from "~/components/icons/XMLIcon.vue";
import PDFIcon from "~/components/icons/PDFIcon.vue";
import { isPrototypeProfile } from "@/utils/config";
const { xmlUrl } = defineProps<{ xmlUrl?: string }>();

const enablePdfButton = !isPrototypeProfile();
const model: ComputedRef<MenuItem[]> = computed(() => {
  const items: MenuItem[] = [
    {
      label: "Link kopieren",
      icon: "link",
      disabled: true,
    },
    {
      label: "Drucken oder als PDF speichern",
      icon: "pdf",
      command: onPrint,
      disabled: !enablePdfButton,
    },
  ];

  if (xmlUrl) {
    items.push({
      label: "XML anzeigen",
      icon: "xml",
      dataAttribute: "xml-view",
      url: xmlUrl,
    });
  }
  return items;
});

const getIcon = (name: string) => {
  switch (name) {
    case "link":
      return MaterialSymbolsLink;
    case "pdf":
      return PDFIcon;
    case "xml":
      return XMLIcon;
  }
};

const menuRef = useTemplateRef<MenuMethods>("menu");

const toggle = (event: Event) => {
  menuRef.value?.toggle(event);
};

defineExpose({ toggle });

const onPrint = () => {
  if (window) window.print();
};
</script>

<template>
  <div class="hidden items-center *:-mx-4 sm:flex">
    <Button text disabled aria-label="Link kopieren">
      <template #icon><MaterialSymbolsLink /></template>
    </Button>
    <Button
      text
      aria-label="Drucken oder als PDF speichern"
      :disabled="!enablePdfButton"
      @click="onPrint"
    >
      <template #icon><PDFIcon /></template>
    </Button>
    <Button
      v-if="!!xmlUrl"
      text
      aria-label="XML anzeigen"
      data-attr="xml-view"
      @click="navigateTo(xmlUrl, { external: true })"
    >
      <template #icon><XMLIcon /></template>
    </Button>
  </div>
  <Button class="sm:hidden" text @click="toggle">
    <template #icon>
      <MdiDotsVertical />
    </template>
  </Button>
  <Menu ref="menu" :popup="true" :model="model" class="print:hidden">
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
          <component :is="getIcon(item.icon)" v-if="item.icon" />
          <span>{{ item.label }}</span>
        </a>
        <span
          v-else
          class="flex cursor-not-allowed items-center gap-8 text-gray-800"
        >
          <component :is="getIcon(item.icon)" v-if="item.icon" />
          <span>{{ item.label }}</span>
        </span>
      </div>
    </template>
  </Menu>
</template>
