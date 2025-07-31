<script setup lang="ts">
import Button from "primevue/button";
import type { ActionMenuItem } from "~/components/ActionMenu/ActionsMenu.vue";

const { actions } = defineProps<{ actions: ActionMenuItem[] }>();
</script>

<template>
  <div class="flex items-center *:-mx-4">
    <Button
      v-for="item in actions"
      :key="item.key"
      v-tooltip.bottom="item.label"
      text
      :disabled="item.disabled"
      :aria-label="item.label"
      :href="item.url"
      :as="item.url ? 'a' : undefined"
      @click.prevent="item.command && item.command()"
    >
      <template #icon
        ><component :is="(item as ActionMenuItem).iconComponent"
      /></template>
    </Button>
  </div>
</template>
