import type { ActionMenuItem } from "~/components/documents/actionMenu/ActionMenu.vue";
import { useCommandActionItem } from "~/composables/useActionMenuItem/useCommandActionItem";
import MaterialSymbolsPrint from "~icons/material-symbols/print";

export function usePrintActionItem(): ActionMenuItem {
  return useCommandActionItem("Drucken", MaterialSymbolsPrint, () =>
    globalThis?.print(),
  );
}
