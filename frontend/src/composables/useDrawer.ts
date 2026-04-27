import type { ComponentPublicInstance } from "vue";

/**
 * Utilities to use with PrimeVue's Drawer component to enable additional
 * behaviors:
 *
 * - Returns `visible` ref to bind to the drawer's open state
 * - Place focus on an element when the drawer is closed. This should be set
 *   to the element that originally opened the drawer (accessibility requirement)
 */
export function useDrawer() {
  const visible = ref(false);
  const triggerRef = ref<HTMLElement | ComponentPublicInstance | null>(null);

  watch(visible, async (isVisible) => {
    if (!isVisible) {
      // Return focus to the trigger element. nextTick because a v-if trigger
      // re-enters the DOM on the same tick the drawer closes.
      await nextTick();

      const target = triggerRef.value;
      if (!target) return;

      // triggerRef may point to a native element or a Vue component instance
      const el = "$el" in target ? (target.$el as HTMLElement) : target;
      if (typeof el?.focus === "function") el?.focus();
    }
  });

  return { visible, triggerRef };
}
