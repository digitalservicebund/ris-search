<script lang="ts" setup>
import { data as menuItems } from "../navigation.data";

import SidebarMenu from "./SidebarMenu.vue";
import Logo from "../components/Logo.vue";
</script>

<template>
  <div>
    <div class="pt-24 px-24 h-fit max-h-full overflow-y-auto">
      <Logo />
      <nav class="mt-96 overflow-y h-full w-full py-8">
        <SidebarMenu :model="menuItems" />
      </nav>
    </div>
  </div>
</template>
