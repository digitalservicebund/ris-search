<script setup lang="ts">
import IcBaselineMoreVert from "~icons/ic/baseline-more-vert";
import { NuxtLink } from "#components";

export type ActionMenuItem = {
  label: string;
  disabled?: boolean;
  url?: string;
  command?: () => void | Promise<void>;
  iconComponent: Component;
  analyticsId?: string;
  keepDrawerOpen?: boolean;
};

const { actions } = defineProps<{ actions: ActionMenuItem[] }>();

const {
  visible: drawerVisible,
  // @ts-expect-error -- usage in template not detected
  triggerRef: drawerTriggerRef,
} = useDrawer();

const drawerId = useId();

const handleDrawerItemClick = async (item: ActionMenuItem) => {
  if (!item.keepDrawerOpen) drawerVisible.value = false;
  await item.command?.();
};
</script>

<template>
  <!-- data attribute can be used by the layout to adjust spacings when an action
  menu exists -->
  <div class="md:hidden" v-bind="$attrs" data-breadcrumbs-adjust="actionmenu">
    <UiButton
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
    </UiButton>

    <UiDrawer
      :id="drawerId"
      v-model:visible="drawerVisible"
      aria-label="Aktionen"
      header="Aktionen"
    >
      <ul class="-mt-8">
        <li v-for="item in actions">
          <button
            v-if="item.disabled"
            type="button"
            class="typo-body-regular flex w-full cursor-not-allowed items-center gap-8 py-12 text-left text-gray-800"
            disabled
          >
            <component :is="item.iconComponent" class="shrink-0" />
            <span>{{ item.label }}</span>
          </button>

          <NuxtLink
            v-else-if="item.url"
            class="typo-body-regular flex items-center gap-8 py-12 no-underline"
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
            class="typo-body-regular flex w-full items-center gap-8 py-12 text-left"
            :data-attr="(item as ActionMenuItem).analyticsId"
            @click="handleDrawerItemClick(item)"
          >
            <component :is="item.iconComponent" class="shrink-0" />
            <span>{{ item.label }}</span>
          </button>
        </li>
      </ul>
    </UiDrawer>
  </div>

  <ul role="menubar" class="hidden items-center *:-mx-4 md:flex">
    <li v-for="item in actions" :key="item.label" role="presentation">
      <UiTooltip :text="item.disabled ? undefined : item.label" side="bottom">
        <UiButton
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
            <component
              :is="(item as ActionMenuItem).iconComponent"
              class="ris-label2-regular"
            />
          </template>
        </UiButton>
      </UiTooltip>
    </li>
  </ul>
</template>
