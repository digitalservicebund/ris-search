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

const base = tw`typo-label1-regular relative -mb-px flex h-48 shrink-0 items-center whitespace-nowrap outline-0 after:absolute after:inset-x-0 after:bottom-0 after:h-4 after:content-[""]`;

// Pseudo element so we can give it some horizontal distance from the text while
// keeping the vertical inset
const focusRing = tw`before:pointer-events-none before:absolute before:-inset-x-12 before:inset-y-0 before:z-10 before:hidden before:border-4 before:border-blue-800 before:content-[""] focus-visible:before:block`;

const activeTab = tw`text-gray-1000 after:bg-gray-1000`;

const inactiveTab = tw`cursor-pointer text-blue-800 hover:after:bg-blue-800`;

const rootClass = computed(() => ({
  [base]: true,
  [focusRing]: true,
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
