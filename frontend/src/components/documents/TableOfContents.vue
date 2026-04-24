<script setup lang="ts">
import type { RouteLocationRaw } from "#vue-router";
import { Drawer } from "primevue";
import type { TreeItem } from "~/components/TreeView.vue";
import IcOutlineArrowUpward from "~icons/ic/outline-arrow-upward";

interface Props {
  tableOfContents: TreeItem[];
  selectedKey?: string;
  subheading?: string;
  subheadingTo?: RouteLocationRaw;
}

const props = defineProps<Props>();

const { visible: mobileTocVisible, triggerRef: openButtonRef } = useDrawer();

const drawerId = useId();
</script>

<template>
  <Transition
    enter-active-class="transition-transform duration-300 ease-in-out delay-150"
    enter-from-class="translate-y-full"
    leave-active-class="transition-transform duration-150 ease-in-out"
    leave-to-class="translate-y-full"
  >
    <button
      v-if="!mobileTocVisible"
      ref="openButtonRef"
      class="shadow-gray-1000/15 fixed inset-x-0 bottom-0 z-10 flex cursor-pointer items-center justify-between gap-8 bg-white p-16 shadow-[0_0_0.5rem] -outline-offset-4 outline-blue-800 focus-visible:outline-4 md:hidden"
      :aria-expanded="mobileTocVisible"
      :aria-controls="drawerId"
      @click="mobileTocVisible = true"
    >
      <div class="line-clamp-1">
        <span class="ris-subhead-bold">Inhalte</span>{{ " " }}
        <span class="ris-subhead-regular">{{ subheading }}</span>
      </div>

      <IcOutlineArrowUpward class="ris-body2-regular text-blue-800" />
    </button>
  </Transition>

  <Drawer
    v-model:visible="mobileTocVisible"
    aria-label="Inhalte"
    block-scroll
    position="bottom"
    :id="drawerId"
    :close-button-props="{
      size: 'small',
      label: 'Schließen',
      iconPos: 'right',
    }"
  >
    <template #header>
      <div class="line-clamp-1">
        <span class="ris-subhead-bold">Inhalte</span>{{ " " }}
        <span class="ris-subhead-regular">{{ subheading }}</span>
      </div>
    </template>

    <TreeView
      :items="tableOfContents"
      :selected="selectedKey"
      :expand-to-key="selectedKey"
      :selection-enabled="!!selectedKey"
      label="Inhalte"
      class="-mx-16 h-full"
      @click="mobileTocVisible = false"
    />
  </Drawer>

  <TreeView
    :items="tableOfContents"
    :selected="selectedKey"
    :expand-to-key="selectedKey"
    :selection-enabled="!!selectedKey"
    :subheading="subheading"
    :subheading-to="subheadingTo"
    heading="Inhalte"
    class="hidden h-full md:block md:pt-16"
  />
</template>
