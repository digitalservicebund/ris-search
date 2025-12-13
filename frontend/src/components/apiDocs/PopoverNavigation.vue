<script setup lang="ts">
import { Popover, PopoverButton, PopoverPanel } from "@headlessui/vue";
import { data as menuItems } from "./navigation.data";
import SidebarMenu from "./SidebarMenu.vue";
import IcBaselineClose from "~icons/ic/baseline-close";
import IcBaselineMenu from "~icons/ic/baseline-menu";
</script>

<template>
  <Popover v-slot="{ open }" as="nav" class="grow lg:hidden">
    <PopoverButton
      class="text-neutral-secondary float-end flex w-full flex-row items-center justify-between gap-4 px-16 py-8 no-underline"
    >
      <AppLogo />
      <div
        v-if="!open"
        class="flex items-center gap-4 hover:underline active:underline"
      >
        <IcBaselineMenu id="menu" size="1.25em" />Menu
      </div>
      <IcBaselineClose v-else id="close" size="1.25em" class="my-4" />
    </PopoverButton>
    <PopoverPanel
      v-slot="{ close }"
      class="bg-background-primary z-20 max-h-screen w-full overflow-y-auto px-16 pb-8"
    >
      <div>
        <SidebarMenu :model="menuItems" @select-leaf="close" />
      </div>
    </PopoverPanel>
  </Popover>
</template>
