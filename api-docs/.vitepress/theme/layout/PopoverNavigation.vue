<script setup lang="ts">
import { Popover, PopoverButton, PopoverPanel } from "@headlessui/vue";
import SidebarMenu from "./SidebarMenu.vue";
import { data as menuItems } from "../navigation.data";
import Logo from "../components/Logo.vue";


</script>

<template>
  <Popover as="nav" class="lg:hidden flex-grow" v-slot="{ open }">
    <PopoverButton
      class="text-neutral-secondary py-8 px-16 gap-4 flex flex-row items-center float-end no-underline w-full justify-between"
    >
      <Logo />
      <div class="flex items-center gap-4 hover:underline active:underline" v-if="!open">
        <Icon id="menu" size="1.25em" />Menu
      </div>
      <Icon id="close" size="1.25em" v-else class="my-4" />
    </PopoverButton>
    <PopoverPanel
      class="z-20 bg-background-primary w-full pb-8 px-16 max-h-screen overflow-y-auto"
      v-slot="{ close }"
    >
      <div>
        <SidebarMenu :model="menuItems" @selectLeaf="close" />
        <SwaggerButton />
      </div>
    </PopoverPanel>
  </Popover>
</template>
