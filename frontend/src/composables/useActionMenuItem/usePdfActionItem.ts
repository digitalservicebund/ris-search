import type { ActionMenuItem } from "~/components/documents/actionMenu/ActionMenu.vue";
import { useCommandActionItem } from "~/composables/useActionMenuItem/useCommandActionItem";
import PdfIcon from "~icons/custom/pdf";

export function usePdfActionItem(): ActionMenuItem {
  return useCommandActionItem("Als PDF speichern", PdfIcon, undefined, true);
}
