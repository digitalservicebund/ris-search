<script setup lang="ts">
import { Breadcrumb, Drawer } from "primevue";
import type { MenuItem } from "primevue/menuitem";
import IcBaselineMoreHoriz from "~icons/ic/baseline-more-horiz";
import ChevronRightIcon from "~icons/ic/outline-chevron-right";
import { NuxtLink } from "#components";
import type { RouteLocationRaw } from "#vue-router";

export type BreadcrumbItem = {
  /** Default label of the item */
  label: string;
  /**
   * Optional label with more information. Will be shown instead of the label in
   * places where there's more space available (currently: the drawer)
   */
  extendedLabel?: string;
  /** Navigation target of the item */
  route?: RouteLocationRaw;
};

const {
  collapse = false,
  items = [],
  label = "Pfadnavigation",
  forceOneLine = true,
} = defineProps<{
  /**
   * When set, the breadcrumbs will show a maximum of 3 items, after which all
   * items between the first and the last item will be tucked away in a drawer
   * that can be opened with a button.
   */
  collapse?: boolean;
  items?: BreadcrumbItem[];
  label?: string;
  /** When set, forces the breadcrumbs to always be limited to one line. */
  forceOneLine?: boolean;
}>();

const itemsWithHome = computed(() => [
  { label: "Start", route: { name: "index" } },
  ...items,
]);

const effectiveItems = computed(() => {
  const collapseAfter = 3;

  const all = [...itemsWithHome.value];
  const shouldCollapse = collapse && all.length > collapseAfter;
  let result: MenuItem[] = all;

  if (shouldCollapse) {
    const drawerMenuItem: MenuItem = {
      label: "Navigiere zu",
      command: () => (drawerVisible.value = true),
    };
    result = [all[0]!, drawerMenuItem, all.at(-1)!];
  }

  return result;
});

const {
  visible: drawerVisible,
  // @ts-expect-error -- usage in template not detected
  triggerRef: drawerTriggerRef,
  closeButtonProps,
} = useDrawer();

const drawerId = useId();
</script>

<template>
  <Breadcrumb
    :model="effectiveItems"
    :aria-label="label"
    :class="[
      '-ml-8 py-8 pl-8', // Offset to ensure focus outline is visible
      { 'overflow-hidden [&_ol]:flex-nowrap!': forceOneLine },
    ]"
    v-bind="$attrs"
  >
    <template #item="slot">
      <span v-if="slot.item.command">
        <button
          ref="drawerTriggerRef"
          type="button"
          class="typo-body-regular flex h-32 w-32 items-center justify-center rounded-xs border border-blue-500 bg-blue-200 text-blue-800 outline-offset-4 outline-blue-800 focus-visible:outline-4 active:bg-blue-400"
          :aria-label="
            typeof slot.item.label === 'string' ? slot.item.label : undefined
          "
          :aria-controls="drawerId"
          :aria-expanded="drawerVisible"
          @click="
            (e) => slot.item.command?.({ item: slot.item, originalEvent: e })
          "
        >
          <IcBaselineMoreHoriz />
        </button>
      </span>

      <NuxtLink
        v-else-if="slot.item.route && slot.item !== effectiveItems.at(-1)"
        :to="slot.item.route"
      >
        {{ slot.item.label }}
      </NuxtLink>

      <span v-else>
        {{ slot.item.label }}
      </span>
    </template>

    <template #separator>
      <ChevronRightIcon width="1rem" height="1rem" />
    </template>
  </Breadcrumb>

  <Drawer
    v-model:visible="drawerVisible"
    aria-label="Navigiere zu"
    block-scroll
    header="Navigiere zu"
    position="bottom"
    :id="drawerId"
    :close-button-props="closeButtonProps"
  >
    <ul class="-mt-8">
      <li v-for="i in itemsWithHome" :key="i.label">
        <NuxtLink
          v-if="i.route && i !== itemsWithHome.at(-1)"
          :to="i.route"
          @click="drawerVisible = false"
          class="typo-link-regular link-hover flex py-12"
        >
          {{ i.extendedLabel ?? i.label }}
        </NuxtLink>

        <span
          v-else
          class="typo-body-regular flex cursor-not-allowed py-12 text-gray-900"
        >
          {{ i.extendedLabel ?? i.label }}
        </span>
      </li>
    </ul>
  </Drawer>
</template>
