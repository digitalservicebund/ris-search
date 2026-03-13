<script setup lang="ts">
import IcBaselineExpandLess from "~icons/ic/baseline-expand-less";
import IcBaselineExpandMore from "~icons/ic/baseline-expand-more";
import IcBaselineUnfoldLess from "~icons/ic/baseline-unfold-less";
import IcBaselineUnfoldMore from "~icons/ic/baseline-unfold-more";
import type { TreeItem } from "./TreeView.vue";

const props = defineProps<{
  item: TreeItem;
}>();

const emit = defineEmits<{
  (e: "click", item: TreeItem): void;
}>();

const expandedKeys = defineModel<string[]>("expandedKeys", { required: true });

// This is the key of the selected item instead of a simple flag because it
// tracks the `selected` state of the entire tree view, including this item and
// all it's children.
const selected = defineModel<string>("selected");

const isParent = computed(() => !!props.item.children?.length);

const isExpanded = computed(() => expandedKeys.value.includes(props.item.key));

const isSelected = computed(() => props.item.key === selected.value);

function onSelect(item: TreeItem) {
  emit("click", item);
  selected.value = item.key;
}

function toggleSelf() {
  if (isExpanded.value) {
    expandedKeys.value = expandedKeys.value.filter((k) => k !== props.item.key);
  } else {
    expandedKeys.value = [...expandedKeys.value, props.item.key];
  }
}

/** Collect keys of all descendant nodes that have children. */
function getChildKeys(item: TreeItem): string[] {
  if (!item.children?.length) return [];
  return item.children.flatMap((child) =>
    child.children?.length ? [child.key, ...getChildKeys(child)] : [],
  );
}

const childKeys = computed(() => getChildKeys(props.item));

/**
 * Shorthand for the node itself and all children. Needed for "deep" toggling
 * of the node and all its children.
 */
const subtreeKeys = computed(() => [props.item.key, ...childKeys.value]);

/** True if the node and all its children are expanded. */
const isDeepExpanded = computed(() =>
  subtreeKeys.value.every((k) => expandedKeys.value.includes(k)),
);

/**
 * Toggles the expanded state of the node and all its children:
 *
 * - if all are expanded, close all
 * - if all are closed, expand all
 * - if the state is mixed, expand all
 */
function toggleDeep() {
  if (isDeepExpanded.value) {
    const toRemove = new Set(subtreeKeys.value);
    expandedKeys.value = expandedKeys.value.filter((k) => !toRemove.has(k));
  } else {
    const existing = new Set(expandedKeys.value);
    const toAdd = subtreeKeys.value.filter((k) => !existing.has(k));
    expandedKeys.value = [...expandedKeys.value, ...toAdd];
  }
}
</script>

<template>
  <li
    role="treeitem"
    :aria-expanded="isParent ? isExpanded : undefined"
    :aria-selected="isSelected"
  >
    <div class="header">
      <button
        class="tree-control-button h-24 w-24"
        v-if="isParent"
        :aria-label="isExpanded ? 'Collapse' : 'Expand'"
        @click="toggleSelf"
      >
        <IcBaselineExpandMore v-if="!isExpanded" />
        <IcBaselineExpandLess v-else />
      </button>

      <button @click="onSelect(item)" class="content">
        <span class="title">{{ item.title }}</span>
        <span v-if="item.subtitle" class="subtitle">{{ item.subtitle }}</span>
      </button>

      <button
        v-if="isParent"
        class="tree-control-button h-40 w-40"
        :aria-label="isDeepExpanded ? 'Collapse all' : 'Expand all'"
        @click="toggleDeep"
      >
        <IcBaselineUnfoldMore v-if="isDeepExpanded" />
        <IcBaselineUnfoldLess v-else />
      </button>
    </div>

    <ul v-if="isParent && isExpanded" role="group">
      <TreeViewItem
        v-for="child in item.children"
        :key="child.key"
        :item="child"
        v-model:expanded-keys="expandedKeys"
        v-model:selected="selected"
        @click="emit('click', $event)"
      />
    </ul>
  </li>
</template>

<style scoped>
@reference "~/assets/main.css";

@scope ([role=treeitem]) to ([role=treeitem]) {
  .header {
    @apply text-gray-1000 ris-label1-regular mb-2 flex gap-8 border-l-4 border-l-transparent py-8 pr-8 pl-12 hover:border-blue-500 hover:bg-blue-200 active:border-blue-800 active:bg-blue-300;
  }

  .content {
    @apply flex flex-1 cursor-pointer flex-col gap-4 text-left wrap-break-word hyphens-auto;

    &:hover .title {
      @apply underline underline-offset-2;
    }
  }

  .subtitle {
    @apply ris-label2-regular flex-1 text-gray-900;
  }

  .tree-control-button {
    @apply flex flex-none cursor-pointer items-center justify-center hover:bg-blue-800/30;
  }

  /* expanded parent node */
  &[aria-expanded="true"] {
    .header {
      @apply border-l-blue-500 bg-blue-200 hover:bg-blue-300 active:border-blue-800 active:bg-blue-300;
    }

    .title {
      @apply ris-label1-bold;
    }
  }

  /* leaf node */
  &:not([aria-expanded]) {
    .header {
      @apply ml-8 py-16 pr-16 pl-[1.125rem];
    }

    .content {
      @apply flex-row gap-8;

      &:hover {
        .title:not(:only-child) {
          @apply no-underline;
        }

        .subtitle {
          @apply underline underline-offset-2;
        }
      }
    }

    .title:not(:only-child) {
      @apply ris-label1-bold;
    }

    .subtitle {
      @apply ris-label1-regular text-gray-1000;
    }
  }

  &[aria-selected="true"] {
    .header {
      @apply border-blue-800 bg-blue-300 hover:border-blue-900 hover:bg-blue-500;
    }

    .title {
      @apply ris-label1-bold flex-none;
    }

    .subtitle {
      @apply text-gray-1000;
    }

    &:not([aria-expanded]) :is(.title, .subtitle) {
      @apply ris-label1-bold;
    }
  }

  [role="group"] {
    @apply pl-20;
  }
}
</style>
