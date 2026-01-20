import { useToast } from "primevue/usetoast";
import type { ActionMenuItem } from "~/components/documents/actionMenu/ActionMenu.vue";
import { useCommandActionItem } from "~/composables/useActionMenuItem/useCommandActionItem";
import MaterialSymbolsLink from "~icons/material-symbols/link";

export function useCopyUrlActionItem(
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

  return useCommandActionItem(
    label ?? "Link kopieren",
    icon ?? MaterialSymbolsLink,
    command,
    !url,
  );
}
