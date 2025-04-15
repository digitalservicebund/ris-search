<script lang="ts" setup>
import type { NavItem } from "./AppNavItem.vue";
import IconSearch from "~icons/ic/search";

const nav: NavItem[] = [
  { text: "Suche", link: "/", icon: IconSearch },
  { text: "Gesetze & Verordnungen", link: "/search?category=N" },
  { text: "Rechtsprechung", link: "/search?category=R" },
];
</script>

<template>
  <nav aria-label="Primär">
    <ul class="flex flex-row items-center gap-16">
      <template v-for="item in nav" :key="item.text">
        <AppNavGroup :item="item" />
      </template>
    </ul>
  </nav>
</template>
