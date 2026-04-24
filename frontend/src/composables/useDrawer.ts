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
  const triggerRef = ref<HTMLElement | null>(null);

  watch(visible, async (isVisible) => {
    if (!isVisible) {
      // Return focus to the trigger element. nextTick because a v-if trigger
      // re-enters the DOM on the same tick the drawer closes.
      await nextTick();
      triggerRef.value?.focus();
    }
  });

  return { visible, triggerRef };
}
