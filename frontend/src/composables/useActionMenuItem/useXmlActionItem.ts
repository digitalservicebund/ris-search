import type { ActionMenuItem } from "~/components/documents/actionMenu/ActionMenu.vue";
import { useNavigateActionItem } from "~/composables/useActionMenuItem/useNavigateActionItem";
import XmlIcon from "~icons/custom/xml";

export function useXmlActionItem(xmlUrl?: string): ActionMenuItem {
  return useNavigateActionItem("XML anzeigen", XmlIcon, xmlUrl);
}
