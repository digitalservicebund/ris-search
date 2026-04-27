import { useToast } from "primevue/usetoast";
import type { ActionMenuItem } from "~/components/documents/actionMenu/ActionMenu.vue";
import IcBaselineCheck from "~icons/ic/baseline-check";
import IconLink from "~icons/ic/outline-link";

const COPY_MESSAGE_TIMEOUT = 3000;

/**
 * Use this function to create an action item where the action is to copy the
 * given url to the clipboard. Returns a reactive object: after copying, label
 * and iconComponent automatically switch to the confirmation state ("Link
 * kopiert" + check icon) for 3 seconds, then revert.
 *
 * @param url
 * @param label
 * @param icon
 */
export function useCopyUrlActionItem(
  url?: string,
  label?: string,
  icon?: Component,
): ActionMenuItem {
  const toastService = useToast();
  const copied = ref(false);

  const command = async () => {
    if (!url) return;

    await navigator.clipboard.writeText(url);
    toastService.add({
      severity: "success",
      summary: "Kopiert!",
      life: COPY_MESSAGE_TIMEOUT,
      closable: false,
    });

    copied.value = true;
    setTimeout(() => {
      copied.value = false;
    }, COPY_MESSAGE_TIMEOUT);
  };

  return reactive<ActionMenuItem>({
    get label() {
      return copied.value ? "Link kopiert" : (label ?? "Link kopieren");
    },

    get iconComponent() {
      return copied.value ? IcBaselineCheck : (icon ?? IconLink);
    },

    command,
    keepDrawerOpen: true,
    disabled: !url,
  });
}
