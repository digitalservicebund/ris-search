<script setup lang="ts">
import Tree from "primevue/tree";
import Button from "primevue/button";
import type { TreeNode } from "primevue/treenode";
import ChevronDownIcon from "~icons/mdi/chevron-down";
import ChevronUpIcon from "~icons/mdi/chevron-up";
import IcBaselineUnfoldMore from "~icons/ic/baseline-unfold-more";
import IcBaselineUnfoldLess from "~icons/ic/baseline-unfold-less";
import IcBaselineClose from "~icons/ic/baseline-close";
import IcBaselineFormatListBulleted from "~icons/ic/baseline-format-list-bulleted";
import { findNodePath } from "@/utils/tableOfContents";

interface Props {
  tableOfContents: TreeNode[];
  selectedKey?: string;
}

const props = defineProps<Props>();

const expandedKeys = ref<Record<string, boolean>>({});
const isExpanded = ref<boolean>(false);
const isTocVisible = ref<boolean>(false);
const selectionKeys = ref<Record<string, boolean>>({});

const toggleNode = (node: TreeNode) => {
  if (expandedKeys.value[node.key]) {
    expandedKeys.value[node.key] = false;
  } else {
    expandedKeys.value[node.key] = true;
    selectionKeys.value[node.key] = true;
  }

  expandedKeys.value = { ...expandedKeys.value };
};

const expandNode = (node: TreeNode) => {
  expandedKeys.value[node.key] = true;
  if (node.children) {
    node.children.forEach(expandNode);
  }
};

// Expand all nodes
const expandAll = () => {
  props.tableOfContents.forEach(expandNode);
  expandedKeys.value = { ...expandedKeys.value };
};

// Collapse all nodes
const collapseAll = () => {
  expandedKeys.value = {};
};

// Toggle expand/collapse for all nodes
const toggleExpandCollapse = () => {
  if (isExpanded.value) {
    collapseAll();
  } else {
    expandAll();
  }
  isExpanded.value = !isExpanded.value;
};

const toggleTableOfContents = () => {
  isTocVisible.value = !isTocVisible.value;
};

const hideTableOfContents = () => {
  isTocVisible.value = false;
};

const nodes = computed(() =>
  findNodePath(props.tableOfContents, props.selectedKey),
);

const isNestedToC = computed(() =>
  props.tableOfContents.some((node) => node.children?.length),
);

watch(
  nodes,
  () => {
    selectionKeys.value = {};
    if (nodes.value) {
      nodes.value.forEach((node) => {
        expandedKeys.value[node.key] = true;
        selectionKeys.value[node.key] = true;
      });
    }
  },
  { immediate: true },
);
const responsiveStyles = `max-lg:fixed max-lg:left-0 max-lg:top-0 max-lg:h-full max-lg:w-full max-lg:bg-gray-100 max-lg:px-32 max-lg:py-16`;
</script>

<template>
  <Button
    v-if="!isTocVisible"
    class="visible w-full lg:hidden"
    data-testid="mobile-toc-button"
    severity="secondary"
    @click="toggleTableOfContents"
  >
    <IcBaselineFormatListBulleted />
    Inhaltsverzeichnis
  </Button>
  <div
    :data-selected="isTocVisible"
    class="flex h-full flex-col max-lg:data-[selected=false]:hidden max-lg:data-[selected=true]:flex lg:mt-16"
    :class="[responsiveStyles]"
    data-testid="table-of-contents"
  >
    <div class="flex flex-row items-center justify-between">
      <div v-if="props.tableOfContents.length > 0" class="ris-subhead-regular">
        Inhaltsverzeichnis
      </div>
      <div class="ml-12">
        <Button
          v-if="isNestedToC"
          id="toc-expand-collapse-button"
          class="bg-transparent hover:bg-transparent"
          @click="toggleExpandCollapse"
        >
          <IcBaselineUnfoldMore
            v-if="!isExpanded"
            class="text-gray-900 hover:text-black"
          />
          <IcBaselineUnfoldLess v-else class="text-gray-900 hover:text-black" />
        </Button>
        <Button
          id="toc-close-button"
          class="visible bg-transparent hover:bg-transparent lg:hidden"
          @click="toggleTableOfContents"
        >
          <IcBaselineClose class="text-gray-900 hover:text-black" />
        </Button>
      </div>
    </div>
    <div class="mb-16">
      <slot name="header"></slot>
    </div>
    <Tree
      v-model:expanded-keys="expandedKeys"
      v-model:selection-keys="selectionKeys"
      :value="props.tableOfContents"
      selection-mode="single"
      class="-m-12 overflow-y-auto p-12 lg:m-0 lg:p-0"
      tabindex="-1"
      aria-label="Inhaltsverzeichnis des aktuellen Gesetzes"
    >
      <template #default="{ node }">
        <router-link
          v-if="node.route"
          :to="node.route"
          class="no-underline"
          tabindex="-1"
          @click="
            toggleNode(node);
            hideTableOfContents();
          "
        >
          {{ node.label }}
        </router-link>
        <span v-else class="w-full no-underline" @click="toggleNode(node)">
          {{ node.label }}
        </span>
        <span
          v-if="node.secondaryLabel"
          class="ris-label2-regular"
          @click="toggleNode(node)"
        >
          {{ node.secondaryLabel }}
        </span>
      </template>
      <template #nodetoggleicon="{ expanded }">
        <ChevronDownIcon v-if="!expanded" />
        <ChevronUpIcon v-else />
      </template>
    </Tree>
  </div>
</template>
