<script setup lang="ts" generic="T extends TreeItem">
import type { RouteLocationRaw } from "#vue-router";

export type TreeItem = {
  key: string;
  title?: string;
  subtitle?: string;
  to?: RouteLocationRaw;
  children?: TreeItem[];
};

const {
  items,
  heading,
  subheading,
  label,
  expandToKey,
  selectionEnabled = true,
} = defineProps<{
  /** All items in the tree */
  items: T[];
  /** Heading of the tree */
  heading?: string;
  /** Additional heading */
  subheading?: string;
  /**
   * Accessible lable of the tree. If undefined, the tree will be labelled by
   * the heading. Make sure that at least one of them is set.
   */
  label?: string;
  /**
   * When set, recursively expand the tree to make sure the item with that key
   * is visible. Any pre-existing expansions are not affected.
   */
  expandToKey?: string;
  /** Enables selected state and selection updates for tree items. */
  selectionEnabled?: boolean;
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

const focusedKey = ref<string>(selected.value ?? items[0]?.key ?? "");

async function focusCurrent() {
  await nextTick();
  treeRef.value?.querySelector<HTMLElement>('[tabindex="0"]')?.focus();
}

/**
 * Lists all visible items, i.e. all root items, as well as all items that are a
 * child of an expanded item.
 */
function getVisibleItems(allItems: TreeItem[], expanded: string[]): TreeItem[] {
  const result: TreeItem[] = [];

  allItems.forEach((i) => {
    result.push(i);
    if (i.children?.length && expanded.includes(i.key)) {
      result.push(...getVisibleItems(i.children, expanded));
    }
  });

  return result;
}

const visibleItems = computed(() => getVisibleItems(items, expandedKeys.value));

/** Creates a map that maps each item key to its parent item. */
function collectParentsByKey(
  allItems: TreeItem[],
  currentParent?: TreeItem,
  parents = new Map<string, TreeItem | undefined>(),
): Map<string, TreeItem | undefined> {
  allItems.forEach((item) => {
    parents.set(item.key, currentParent);

    if (item.children?.length) {
      collectParentsByKey(item.children, item, parents);
    }
  });

  return parents;
}

const parentByKey = computed(() => collectParentsByKey(items));

/**
 * Gets the keys of all ancestors of an item, i.e. the item's parent, the
 * parent's parent etc.
 */
function getAncestorKeys(key: string): string[] {
  const ancestors: string[] = [];
  let current = parentByKey.value.get(key);
  while (current) {
    ancestors.push(current.key);
    current = parentByKey.value.get(current.key);
  }
  return ancestors;
}

watch(
  [() => expandToKey, parentByKey],
  ([key]) => {
    if (!key) return;
    const ancestors = getAncestorKeys(key);
    if (ancestors.length) {
      expandedKeys.value = [...new Set([...expandedKeys.value, ...ancestors])];
    }
  },
  { immediate: true },
);

function getNearestVisibleKey(key?: string) {
  const visibleKeys = new Set(visibleItems.value.map((item) => item.key));
  let currentKey = key;

  while (currentKey) {
    if (visibleKeys.has(currentKey)) return currentKey;
    currentKey = parentByKey.value.get(currentKey)?.key;
  }
}

function getNextFocusableKey() {
  // The focused item must always stay in the set of visible tree items so the
  // roving tabindex keeps exactly one `treeitem` tabbable and keyboard handling
  // can still find the current index.
  //
  // When selection or collapsing hides the current focus target, walk up to the
  // nearest visible ancestor; if neither the current focus target nor the
  // selected item is visible anymore, fall back to the first visible item.

  const visible = visibleItems.value;
  if (!visible.length) return "";

  return (
    getNearestVisibleKey(focusedKey.value) ??
    getNearestVisibleKey(selected.value) ??
    visible[0]?.key ??
    ""
  );
}

watch(
  [visibleItems, () => selected.value, parentByKey],
  () => {
    focusedKey.value = getNextFocusableKey();
  },
  { immediate: true },
);

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
  if (selectionEnabled) selected.value = item.key;

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
  <nav :aria-labelledby="heading ? headingId : undefined" :aria-label="label">
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
        :selection-enabled="selectionEnabled"
        v-model:expanded-keys="expandedKeys"
        v-model:selected="selected"
        @click="$emit('click', $event as T)"
      />
    </ul>
  </nav>
</template>
