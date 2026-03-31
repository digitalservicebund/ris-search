<script setup lang="ts">
import { NuxtLink } from "#components";
import IcBaselineExpandLess from "~icons/ic/baseline-expand-less";
import IcBaselineExpandMore from "~icons/ic/baseline-expand-more";
import IcBaselineUnfoldLess from "~icons/ic/baseline-unfold-less";
import IcBaselineUnfoldMore from "~icons/ic/baseline-unfold-more";
import type { TreeItem } from "./TreeView.vue";

const {
  item,
  focusedKey,
  level = 1,
  selectionEnabled = true,
} = defineProps<{
  item: TreeItem;
  focusedKey?: string;
  level?: number;
  selectionEnabled?: boolean;
}>();

const emit = defineEmits<{
  click: [item: TreeItem];
}>();

const expandedKeys = defineModel<string[]>("expandedKeys", { required: true });

// This is the key of the selected item instead of a simple flag because it
// tracks the `selected` state of the entire tree view, including this item and
// all its children.
const selected = defineModel<string>("selected");

const isParent = computed(() => !!item.children?.length);

const isExpanded = computed(() => expandedKeys.value.includes(item.key));

const isFocused = computed(() => item.key === focusedKey);

const contentTag = computed(() => (item.to ? NuxtLink : "button"));

const accessibleLabel = computed(() => {
  const label = [item.title, item.subtitle].filter(Boolean).join(", ");
  return label || undefined;
});

const isSelected = computed(
  () => selectionEnabled && item.key === selected.value,
);

function onSelect() {
  emit("click", item);
  if (selectionEnabled) selected.value = item.key;
}

function onHeaderClick() {
  if (isParent.value) {
    toggleSelf();
    return;
  }

  onSelect();
}

function toggleSelf() {
  if (isExpanded.value) {
    expandedKeys.value = expandedKeys.value.filter((k) => k !== item.key);
  } else {
    expandedKeys.value = [...expandedKeys.value, item.key];
  }
}

const itemIsDeepExpanded = computed(() =>
  isDeepExpanded(item, expandedKeys.value),
);

function onToggleDeep() {
  expandedKeys.value = toggleDeep(item, expandedKeys.value);
}
</script>

<template>
  <li
    role="treeitem"
    :aria-expanded="isParent ? isExpanded : undefined"
    :aria-label="accessibleLabel"
    :aria-level="level"
    :aria-selected="selectionEnabled ? isSelected : undefined"
    :tabindex="isFocused ? 0 : -1"
  >
    <!-- Allowing clicking on a non-interactive element in this case to increase
    the clickable area without invalid nesting of interactive elements. Should
    be fine because the same functionality is also exposed via actual
    interactive elements. -->
    <div class="header" @click="onHeaderClick">
      <div v-if="isParent" class="tree-control">
        <button
          :aria-label="isExpanded ? 'Ebene schließen' : 'Ebene öffnen'"
          class="h-24 w-24"
          tabindex="-1"
          type="button"
          @click.stop="toggleSelf"
        >
          <IcBaselineExpandMore v-if="!isExpanded" />
          <IcBaselineExpandLess v-else />
        </button>
      </div>

      <div class="content">
        <component
          v-if="item.title"
          :is="contentTag"
          :to="item.to"
          :type="item.to ? undefined : 'button'"
          class="title"
          tabindex="-1"
          @click.stop="onSelect()"
        >
          {{ item.title }}
        </component>

        <component
          v-if="item.subtitle"
          :is="contentTag"
          :to="item.to"
          :type="item.to ? undefined : 'button'"
          class="subtitle"
          tabindex="-1"
          @click.stop="onSelect()"
        >
          {{ item.subtitle }}
        </component>
      </div>

      <div v-if="isParent" class="tree-control">
        <button
          class="h-40 w-40"
          tabindex="-1"
          type="button"
          :aria-label="
            itemIsDeepExpanded
              ? 'Alle Ebenen zuklappen'
              : 'Alle Ebenen ausklappen'
          "
          @click.stop="onToggleDeep"
        >
          <IcBaselineUnfoldLess v-if="itemIsDeepExpanded" />
          <IcBaselineUnfoldMore v-else />
        </button>
      </div>
    </div>

    <ul v-if="isParent && isExpanded" role="group">
      <TreeViewItem
        v-for="child in item.children"
        :key="child.key"
        :focused-key="focusedKey"
        :item="child"
        :level="level + 1"
        :selection-enabled="selectionEnabled"
        v-model:expanded-keys="expandedKeys"
        v-model:selected="selected"
        @click="emit('click', $event)"
      />
    </ul>
  </li>
</template>

<style scoped>
@reference "~/assets/main.css";

/*
 * The styles for the different item states are extracted here instead of
 * applying the classes to the elements directly because of the many different
 * permutations (parent/child/expanded/collapsed/selected/hover/active/...).
 * This is really hard to specify in Tailwind due to limited support for
 * inheritance and ARIA-selectors.
 *
 * The styles below start from an unselected tree item with children as the
 * default variants. The other variants are built by selectively overriding
 * styles of the default variant.
 *
 * The @scope from treeitem to treeitem ensures that the expanded and selected
 * states of the parent don't bleed into children of the current element. For
 * some reason Vue's own scoping doesn't seem sufficient for that.
 *
 * The :after pseudo element is used throughout to increase the clickable area
 * of the controls.
 */
@scope ([role=treeitem]) to ([role=treeitem]) {
  :scope:focus-visible {
    @apply outline-4 -outline-offset-4 outline-blue-800;
  }

  * {
    @apply cursor-pointer outline-none;
  }

  .header {
    @apply text-gray-1000 ris-label1-regular mb-2 flex gap-8 border-l-4 border-l-transparent py-8 pr-8 pl-12 hover:border-blue-500 hover:bg-blue-200 active:border-blue-800 active:bg-blue-300;
  }

  .content {
    @apply flex flex-1 flex-col gap-4;

    :is(.title, .subtitle) {
      @apply self-start text-left wrap-break-word hyphens-auto;
    }

    .subtitle {
      @apply ris-label2-regular text-gray-900;
    }

    .title:hover,
    &:has(.subtitle:hover) .title {
      @apply underline underline-offset-2;
    }
  }

  .tree-control {
    @apply relative;

    &:first-child button {
      @apply after:right-0 after:-left-16;
    }

    &:last-child {
      @apply flex items-center;

      button {
        @apply after:-right-8 after:left-0;
      }
    }

    button {
      @apply flex flex-none cursor-pointer items-center justify-center after:absolute after:-inset-y-8 after:content-["_"] hover:bg-blue-800/30;
    }
  }

  /* expanded parent node */
  &[aria-expanded="true"] {
    .header {
      @apply border-l-blue-500 bg-blue-200 hover:bg-blue-300 active:border-blue-800 active:bg-blue-300;
    }

    .content .title {
      @apply ris-label1-bold;
    }
  }

  /* leaf node */
  &:not([aria-expanded]) {
    .header {
      @apply py-16 pr-16 pl-[1.625rem];
    }

    &[aria-level="1"] .header {
      @apply pl-[1.125rem];
    }

    .content {
      @apply relative flex-row gap-8;

      .title {
        @apply flex-none after:absolute after:-inset-y-16 after:-right-16 after:-left-[1.625rem] after:content-["_"];

        &:not(:only-child) {
          @apply ris-label1-bold;
        }
      }

      .subtitle {
        @apply ris-label1-regular text-gray-1000;
      }

      &:hover {
        .title {
          @apply underline;

          &:not(:only-child) {
            @apply no-underline;
          }
        }

        .subtitle {
          @apply underline underline-offset-2;
        }
      }
    }
  }

  &[aria-selected="true"] {
    .header {
      @apply border-blue-800 bg-blue-300 hover:border-blue-900 hover:bg-blue-500;
    }

    .content {
      .title {
        @apply ris-label1-bold flex-none;
      }

      .subtitle {
        @apply text-gray-1000;
      }
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
