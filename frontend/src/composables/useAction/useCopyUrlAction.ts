import { useToast } from "primevue/usetoast";
import type { ActionMenuItem } from "~/components/documents/actionMenu/ActionMenu.vue";
import { useCommandAction } from "~/composables/useAction/useCommandAction";
import MaterialSymbolsLink from "~icons/material-symbols/link";

export function useCopyUrlAction(
  url?: string,
  label?: string,
  icon?: Component,
): ActionMenuItem {
  const toastService = useToast();

  const command = async () => {
    if (!url) {
      return;
    }
    await navigator.clipboard.writeText(url);
    toastService.add({
      severity: "success",
      summary: "Kopiert!",
      life: 3000,
      closable: false,
    });
  };

  return useCommandAction(
    label ?? "Link kopieren",
    icon ?? MaterialSymbolsLink,
    command,
    !url,
  );
}
