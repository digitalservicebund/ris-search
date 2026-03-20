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

/**
 * Recursively walks the tree from top to bottom to find the parent that
 * contains the child with the specified key as a direct descendant, then
 * returns the parent.
 */
function findParent(
  items: TreeItem[],
  childKey: string,
  currentParent?: TreeItem,
): TreeItem | undefined {
  for (const item of items) {
    if (item.key === childKey) return currentParent;
    if (item.children?.length) {
      const found = findParent(item.children, childKey, item);
      if (found) return found;
    }
  }
}

function onKeydown(event: KeyboardEvent) {
  const visible = visibleItems.value;
  const index = visible.findIndex((i) => i.key === focusedKey.value);
  const item = visible[index];
  if (!item) return;

  const isParent = !!item.children?.length;
  const isExpanded = expandedKeys.value.includes(item.key);

  switch (event.key) {
    // Focus next item
    case "ArrowDown": {
      const next = visible[index + 1];
      if (next) {
        focusedKey.value = next.key;
        focusCurrent();
      }
      break;
    }

    // Focus previous item
    case "ArrowUp": {
      const prev = visible[index - 1];
      if (prev) {
        focusedKey.value = prev.key;
        focusCurrent();
      }
      break;
    }

    // On a parent node: expand if collapsed, move focus to first child if
    // expanded
    case "ArrowRight": {
      if (isParent && !isExpanded) {
        expandedKeys.value = [...expandedKeys.value, item.key];
      } else if (isParent && isExpanded && item.children?.[0]) {
        focusedKey.value = item.children[0].key;
        focusCurrent();
      }
      break;
    }

    // On a parent node: collapse if expanded. Move focus to parent if collapsed
    // or on a child node
    case "ArrowLeft": {
      if (isParent && isExpanded) {
        expandedKeys.value = expandedKeys.value.filter((k) => k !== item.key);
      } else {
        const parent = findParent(props.items, item.key);
        if (parent) {
          focusedKey.value = parent.key;
          focusCurrent();
        }
      }
      break;
    }

    // Move focus to first node
    case "Home":
      if (visible[0]) {
        focusedKey.value = visible[0].key;
        focusCurrent();
      }
      break;

    // Move focus to last node
    case "End":
      const last = visible.at(-1);
      if (last) {
        focusedKey.value = last.key;
        focusCurrent();
      }
      break;

    // Activate node = select, emit click + trigger navigation if applicable
    case "Enter": {
      selected.value = item.key;
      emit("click", item as T);
      if (item.to) navigateTo(item.to);
      break;
    }

    // On a parent node with nested children: toggle deep expand/collapse
    case "*": {
      if (!isParent) break;
      expandedKeys.value = toggleDeep(item, expandedKeys.value);
      break;
    }

    // No match = cancel custom keyboard handling
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
