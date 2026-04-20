<script lang="ts" setup>
import { PopoverGroup } from "@headlessui/vue";
import { useData } from "vitepress";
import { computed } from "vue";
import NavGroup from "./NavGroup.vue";

const { theme } = useData();
const props = defineProps<{ nav: "primaryNav" | "secondaryNav" }>();
const nav = computed(() => theme.value[props.nav]);
</script>

<template>
  <PopoverGroup as="nav">
    <ul class="flex flex-row flex-wrap items-center gap-x-8 -mx-8">
      <template v-for="item in nav" :key="item.text">
        <NavGroup :item="item" />
      </template>
    </ul>
  </PopoverGroup>
</template>
