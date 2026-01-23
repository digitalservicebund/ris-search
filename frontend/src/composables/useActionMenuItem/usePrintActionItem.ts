import type { ActionMenuItem } from "~/components/documents/actionMenu/ActionMenu.vue";
import { useCommandActionItem } from "~/composables/useActionMenuItem/useCommandActionItem";
import MaterialSymbolsPrint from "~icons/ic/baseline-print";

export function usePrintActionItem(): ActionMenuItem {
  return useCommandActionItem("Drucken", MaterialSymbolsPrint, () =>
    globalThis?.print(),
  );
}
