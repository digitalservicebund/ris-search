<script lang="ts" setup>
import { data as menuItems } from "./navigation.data";
import SidebarMenu from "./SidebarMenu.vue";
</script>

<template>
  <div>
    <div class="h-fit max-h-full overflow-y-auto px-24 pt-24">
      <AppLogo />
      <nav class="overflow-y mt-96 h-full w-full py-8">
        <SidebarMenu :model="menuItems" />
      </nav>
    </div>
  </div>
</template>
