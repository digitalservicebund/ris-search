<script setup lang="ts" generic="T extends TreeItem">
import type { RouteLocationRaw } from "#vue-router";

export type TreeItem = {
  key: string;
  title: string;
  subtitle?: string;
  to?: RouteLocationRaw;
  children?: TreeItem[];
};

const props = defineProps<{
  items: T[];
  heading?: string;
  subheading?: string;
  label?: string;
}>();

const expandedKeys = defineModel<string[]>("expandedKeys", {
  default: () => [],
});

const selected = defineModel<string>("selected");

const emit = defineEmits<{
  click: [item: T];
}>();

const headingId = useId();
const treeRef = ref<HTMLElement>();

// Focus management ---------------------------------------
//
// The focus management consists of 3 parts:
//
// - Collecting the currently visible items (all root level items + all children
//   of expanded items)
// - A keyboard handler that manages the focused item key by moving around on
//   the list of visible items based on the pressed keys. The keys and actions
//   are based on the W3 pattern for tree views [1]
// - Implementing a roving tab index [2] to shift the focus to the item with the
//   focused item key
//
// [1]: https://www.w3.org/WAI/ARIA/apg/patterns/treeview/
// [2]: https://www.w3.org/WAI/ARIA/apg/practices/keyboard-interface/#kbd_roving_tabindex

const focusedKey = ref<string>(selected.value ?? props.items[0]?.key ?? "");

watch(
  () => selected.value,
  (val) => {
    if (val) focusedKey.value = val;
  },
);

async function focusCurrent() {
  await nextTick();
  treeRef.value?.querySelector<HTMLElement>('[tabindex="0"]')?.focus();
}

function getVisibleItems(items: TreeItem[], expanded: string[]): TreeItem[] {
  const result: TreeItem[] = [];

  items.forEach((i) => {
    result.push(i);
    if (i.children?.length && expanded.includes(i.key)) {
      result.push(...getVisibleItems(i.children, expanded));
    }
  });

  return result;
}

const visibleItems = computed(() =>
  getVisibleItems(props.items, expandedKeys.value),
);

function collectParentsByKey(
  items: TreeItem[],
  currentParent?: TreeItem,
  parents = new Map<string, TreeItem | undefined>(),
): Map<string, TreeItem | undefined> {
  for (const item of items) {
    parents.set(item.key, currentParent);

    if (item.children?.length) {
      collectParentsByKey(item.children, item, parents);
    }
  }

  return parents;
}

const parentByKey = computed(() => collectParentsByKey(props.items));

function moveFocusTo(item?: TreeItem) {
  if (!item) return;

  focusedKey.value = item.key;
  focusCurrent();
}

function expandItem(item: TreeItem) {
  expandedKeys.value = [...expandedKeys.value, item.key];
}

function collapseItem(item: TreeItem) {
  expandedKeys.value = expandedKeys.value.filter((key) => key !== item.key);
}

function expandIfParent(item: TreeItem) {
  if (!item.children?.length) return;

  if (!expandedKeys.value.includes(item.key)) {
    expandItem(item);
    return;
  }

  moveFocusTo(item.children[0]);
}

function collapseIfParent(item: TreeItem) {
  if (item.children?.length && expandedKeys.value.includes(item.key)) {
    collapseItem(item);
    return;
  }

  moveFocusTo(parentByKey.value.get(item.key));
}

function activateItem(item: TreeItem) {
  selected.value = item.key;
  emit("click", item as T);

  if (item.to) navigateTo(item.to);
}

function onKeydown(event: KeyboardEvent) {
  const visible = visibleItems.value;
  const index = visible.findIndex((i) => i.key === focusedKey.value);
  const item = visible[index];
  if (!item) return;

  switch (event.key) {
    case "ArrowDown":
      moveFocusTo(visible[index + 1]);
      break;

    case "ArrowUp":
      moveFocusTo(visible[index - 1]);
      break;

    case "ArrowRight":
      expandIfParent(item);
      break;

    case "ArrowLeft":
      collapseIfParent(item);
      break;

    case "Home":
      moveFocusTo(visible[0]);
      break;

    case "End":
      moveFocusTo(visible.at(-1));
      break;

    case "Enter":
      activateItem(item);
      break;

    case "*":
      if (item.children?.length) {
        expandedKeys.value = toggleDeep(item, expandedKeys.value);
      }
      break;

    default:
      return;
  }

  event.preventDefault();
}
</script>

<template>
  <div>
    <div
      v-if="heading"
      class="space-y-4 border-b border-b-gray-400 px-[1.375rem] py-16"
    >
      <h2 :id="headingId" class="ris-heading3-bold">{{ heading }}</h2>
      <p v-if="subheading" class="ris-label1-regular">{{ subheading }}</p>
    </div>

    <ul
      ref="treeRef"
      role="tree"
      :aria-label="label"
      :aria-labelledby="heading ? headingId : undefined"
      @keydown="onKeydown"
    >
      <TreeViewItem
        v-for="item in items"
        :key="item.key"
        :item="item"
        :focused-key="focusedKey"
        v-model:expanded-keys="expandedKeys"
        v-model:selected="selected"
        @click="$emit('click', $event as T)"
      />
    </ul>
  </div>
</template>
