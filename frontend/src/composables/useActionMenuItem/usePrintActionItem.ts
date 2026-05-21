import IconPrint from "~icons/ic/baseline-print";
import type { ActionMenuItem } from "~/components/documents/actionMenu/ActionMenu.vue";
import { useCommandActionItem } from "~/composables/useActionMenuItem/useCommandActionItem";

export function usePrintActionItem(): ActionMenuItem {
  return useCommandActionItem("Drucken", IconPrint, () => globalThis?.print());
}
