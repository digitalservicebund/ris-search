import type { ActionMenuItem } from "~/components/documents/actionMenu/ActionMenu.vue";
import { useCommandActionItem } from "~/composables/useActionMenuItem/useCommandActionItem";
import IconPrint from "~icons/ic/baseline-print";

export function usePrintActionItem(): ActionMenuItem {
  return useCommandActionItem("Drucken", IconPrint, () => globalThis?.print());
}
