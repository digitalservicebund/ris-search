<script setup lang="ts">
import type { MenuItem } from "primevue/menuitem";
import Menu, { type MenuMethods } from "primevue/menu";
import Button from "primevue/button";
import MdiDotsVertical from "~icons/mdi/dots-vertical";
import MaterialSymbolsLink from "~icons/material-symbols/link";
import MaterialSymbolsCode from "~icons/material-symbols/code";
import MdiPrinter from "~icons/mdi/printer";
import XMLFolderIcon from "~/components/icons/XMLFolderIcon.vue";
const { xmlUrl, zipUrl } = defineProps<{ xmlUrl?: string; zipUrl?: string }>();

const model: ComputedRef<MenuItem[]> = computed(() => {
  const items: MenuItem[] = [
    {
      label: "Link kopieren",
      icon: "link",
      disabled: true,
    },
    {
      label: "PDF anzeigen",
      icon: "pdf",
      command: onPrint,
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
  if (zipUrl) {
    items.push({
      label: "XML-Archiv herunterladen",
      icon: "xml-zip",
      dataAttribute: "xml-zip-view",
      url: zipUrl,
    });
  }
  return items;
});

const getIcon = (name: string) => {
  switch (name) {
    case "link":
      return MaterialSymbolsLink;
    case "pdf":
      return MdiPrinter;
    case "xml":
      return MaterialSymbolsCode;
    case "xml-zip":
      return XMLFolderIcon;
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
    <Button text aria-label="Drucken" @click="onPrint">
      <template #icon><MdiPrinter /></template>
    </Button>
    <Button
      v-if="!!xmlUrl"
      text
      aria-label="XML anzeigen"
      @click="navigateTo(xmlUrl, { external: true })"
    >
      <template #icon><MaterialSymbolsCode /></template>
    </Button>
    <Button
      v-if="!!zipUrl"
      text
      aria-label="XML-Archiv herunterladen"
      @click="navigateTo(zipUrl, { external: true })"
    >
      <template #icon><XMLFolderIcon /></template>
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
          class="flex cursor-pointer items-center gap-8"
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
