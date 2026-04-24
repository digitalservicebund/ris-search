<script setup lang="ts">
import { NuxtLink } from "#components";
import { Button, Drawer } from "primevue";
import type { MenuItem } from "primevue/menuitem";
import IcBaselineMoreVert from "~icons/ic/baseline-more-vert";

export type ActionMenuItem = Omit<MenuItem, "icon"> & {
  iconComponent: Component;
  analyticsId?: string;
  command?: () => void | Promise<void>;
  keepDrawerOpen?: boolean;
};

const { actions } = defineProps<{ actions: ActionMenuItem[] }>();

const { visible: drawerVisible, triggerRef: drawerTriggerRef } = useDrawer();

const drawerId = useId();

const handleDrawerItemClick = async (item: ActionMenuItem) => {
  if (!item.keepDrawerOpen) drawerVisible.value = false;
  await item.command?.();
};
</script>

<template>
  <div class="md:hidden">
    <Button
      ref="drawerTriggerRef"
      aria-label="Aktionen anzeigen"
      text
      :aria-controls="drawerId"
      :aria-expanded="drawerVisible"
      @click="drawerVisible = true"
    >
      <template #icon>
        <IcBaselineMoreVert />
      </template>
    </Button>

    <Drawer
      :id="drawerId"
      v-model:visible="drawerVisible"
      aria-label="Aktionen"
      block-scroll
      header="Aktionen"
      position="bottom"
      :close-button-props="{
        size: 'small',
        label: 'Schließen',
        iconPos: 'right',
      }"
    >
      <ul class="-mt-12">
        <li v-for="item in actions">
          <button
            v-if="item.disabled"
            type="button"
            class="ris-body2-regular flex w-full cursor-not-allowed items-center gap-8 py-12 text-gray-800"
            disabled
          >
            <component :is="item.iconComponent" class="shrink-0" />
            <span>{{ item.label }}</span>
          </button>

          <NuxtLink
            v-else-if="item.url"
            class="ris-body2-regular flex items-center gap-8 py-12 no-underline"
            external
            :data-attr="(item as ActionMenuItem).analyticsId"
            :to="item.url"
            @click="drawerVisible = false"
          >
            <component :is="item.iconComponent" class="shrink-0" />
            <span>{{ item.label }}</span>
          </NuxtLink>

          <button
            v-else
            type="button"
            class="ris-body2-regular flex w-full items-center gap-8 py-12"
            :data-attr="(item as ActionMenuItem).analyticsId"
            @click="handleDrawerItemClick(item)"
          >
            <component :is="item.iconComponent" class="shrink-0" />
            <span>{{ item.label }}</span>
          </button>
        </li>
      </ul>
    </Drawer>
  </div>

  <ul role="menubar" class="hidden items-center *:-mx-4 md:flex">
    <li v-for="item in actions" :key="item.label" role="presentation">
      <Button
        v-tooltip.bottom="item.label"
        role="menuitem"
        text
        :disabled="item.disabled"
        :aria-label="item.label"
        :to="item.url"
        :as="item.url ? NuxtLink : undefined"
        :data-attr="(item as ActionMenuItem).analyticsId"
        external
        @click="item.command"
      >
        <template #icon>
          <component :is="(item as ActionMenuItem).iconComponent" />
        </template>
      </Button>
    </li>
  </ul>
</template>
