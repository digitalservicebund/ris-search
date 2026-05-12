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

const {
  visible: drawerVisible,
  // @ts-expect-error -- usage in template not detected
  triggerRef: drawerTriggerRef,
  closeButtonProps,
} = useDrawer();

const drawerId = useId();

const handleDrawerItemClick = async (item: ActionMenuItem) => {
  if (!item.keepDrawerOpen) drawerVisible.value = false;
  await item.command?.();
};
</script>

<template>
  <div class="md:hidden" v-bind="$attrs">
    <Button
      ref="drawerTriggerRef"
      aria-label="Aktionen anzeigen"
      text
      size="small"
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
      :close-button-props="closeButtonProps"
    >
      <ul class="-mt-8">
        <li v-for="item in actions">
          <button
            v-if="item.disabled"
            type="button"
            class="body-font flex w-full cursor-not-allowed items-center gap-8 py-12 text-left text-gray-800"
            disabled
          >
            <component :is="item.iconComponent" class="shrink-0" />
            <span>{{ item.label }}</span>
          </button>

          <NuxtLink
            v-else-if="item.url"
            class="body-font flex items-center gap-8 py-12 no-underline"
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
            class="body-font flex w-full items-center gap-8 py-12 text-left"
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
