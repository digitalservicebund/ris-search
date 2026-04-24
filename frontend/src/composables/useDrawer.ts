import { matchedRouteKey } from "#vue-router";

/**
 * Utilities to use with PrimeVue's Drawer component to enable additional
 * behaviors:
 *
 * - Place focus on an element when the drawer is closed. This should be set
 *   to the element that originally opened the drawer (accessibility requirement)
 * - Allow dismissing the drawer using the browser's back button without
 *   polluting the history.
 */
export function useDrawer() {
  const visible = ref(false);

  let pushedHistoryState = false;

  const triggerRef = ref<HTMLElement | null>(null);

  watch(visible, async (isVisible) => {
    if (isVisible) {
      if (import.meta.client) {
        history.pushState({ drawerOpen: true }, "", location.href);
        pushedHistoryState = true;
      }
    } else {
      await nextTick();
      triggerRef.value?.focus();

      if (import.meta.client && pushedHistoryState) {
        pushedHistoryState = false;
        history.back();
      }
    }
  });

  function handlePopstate() {
    if (visible.value) {
      pushedHistoryState = false;
      visible.value = false;
    }
  }

  onMounted(() => addEventListener("popstate", handlePopstate));
  onUnmounted(() => removeEventListener("popstate", handlePopstate));

  // injecting matchedRouteKey as a workaround to prevent failures and warnings
  // in tests when used outside of a Vue router context
  if (inject(matchedRouteKey, null)) {
    onBeforeRouteLeave(() => {
      if (pushedHistoryState) {
        pushedHistoryState = false;
        history.back();
      }
    });
  }

  return { visible, triggerRef };
}
