<script setup lang="ts">
import { Popover, PopoverButton, PopoverPanel } from "@headlessui/vue";
import Icon from "../components/Icon.vue";
import type { NavItem as NavItemType } from "../types";
import NavItem from "./NavItem.vue";

const props = defineProps<{ item: NavItemType }>();
const item = props.item;
</script>

<template>
  <Popover as="li">
    <PopoverButton
      v-if="item.items && item.items.length"
      class="text-xl gap-4 py-4 px-8 flex flex-row items-center hover:underline"
    >
      <Icon :id="item.icon" size="1.2em" v-if="!!item.icon" />
      {{ item.text }}
    </PopoverButton>
    <template v-else>
      <NavItem :item="item" />
    </template>

    <PopoverPanel v-if="item.items && item.items.length">
      <ul
        class="flex flex-col md:flex-row gap-8 absolute left-0 right-0 z-10 bg-background-primary border-b-2 border-neutral-tertiary"
      >
        <li v-for="child in item.items">
          <NavItem :item="child" />
        </li>
      </ul>
    </PopoverPanel>
  </Popover>
</template>
