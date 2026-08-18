<script setup lang="ts">
import { useTemplateRef } from "vue";
import { tw } from "../../utils/tags";

defineOptions({ inheritAttrs: false });

const list = useTemplateRef<HTMLElement>("list");

const tabs = () => [
  ...(list.value?.querySelectorAll<HTMLElement>('[role="tab"]') ?? []),
];

const focusTab = (tab: HTMLElement | undefined) => {
  tab?.focus();
  // jsdom doesn't implement scrolling
  tab?.scrollIntoView?.({ block: "nearest", inline: "nearest" });
};

/**
 * Roving tabindex: only the active tab is tabbable, the arrow keys move focus
 * between tabs. Focusing a tab does not select it — selection happens on
 * activation (click, or Enter on the focused tab), which the tabs handle
 * themselves.
 */
const onKeydown = (event: KeyboardEvent) => {
  const all = tabs();
  const current = (event.target as HTMLElement).closest('[role="tab"]');
  const index = all.indexOf(current as HTMLElement);
  if (index === -1) return;

  switch (event.key) {
    case "ArrowRight":
      focusTab(all[(index + 1) % all.length]);
      break;
    case "ArrowLeft":
      focusTab(all[(index - 1 + all.length) % all.length]);
      break;
    case "Home":
      focusTab(all[0]);
      break;
    case "End":
      focusTab(all[all.length - 1]);
      break;
    default:
      return;
  }

  event.preventDefault();
};

// Classes ------------------------------------------------

const scroller = tw`overflow-x-auto`;

const tabList = tw`flex w-max min-w-full border-b border-gray-400`;
</script>

<template>
  <div :class="scroller">
    <div
      ref="list"
      v-bind="$attrs"
      role="tablist"
      aria-orientation="horizontal"
      :class="tabList"
      @keydown="onKeydown"
    >
      <slot />
    </div>
  </div>
</template>
