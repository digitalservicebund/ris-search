<script setup lang="ts">
import { computed, type Component } from "vue";
import { tw } from "../../utils/tags";

const { active = false, as = "button" } = defineProps<{
  /** Whether this tab is the currently selected one. */
  active?: boolean;
  /**
   * The element or component to render as. Defaults to a native button; pass a
   * link component or `"a"` for tabs that navigate.
   */
  as?: string | Component;
}>();

const isNativeButton = computed(() => as === "button");

// Classes ------------------------------------------------

const base = tw`typo-label2-bold relative -mb-px flex h-64 shrink-0 items-center gap-8 border-x border-t border-transparent px-24 pb-4 whitespace-nowrap outline-0 -outline-offset-4 outline-blue-800 focus-visible:outline-4 has-[svg]:pl-20`;

const activeTab = tw`border-x-gray-400 border-t-gray-400 bg-white text-black`;

const inactiveTab = tw`cursor-pointer text-blue-800 after:absolute after:-inset-x-1 after:bottom-0 after:h-4 after:content-[""] hover:after:bg-blue-800`;

const rootClass = computed(() => ({
  [base]: true,
  [activeTab]: active,
  [inactiveTab]: !active,
}));
</script>

<template>
  <component
    :is="as"
    :type="isNativeButton ? 'button' : undefined"
    role="tab"
    :aria-selected="active"
    :tabindex="active ? 0 : -1"
    :class="rootClass"
  >
    <slot />
  </component>
</template>
