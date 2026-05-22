import XmlIcon from "~icons/custom/xml";
import type { ActionMenuItem } from "~/components/documents/actionMenu/ActionMenu.vue";
import { useNavigateActionItem } from "~/composables/useActionMenuItem/useNavigateActionItem";

export function useXmlActionItem(xmlUrl?: string): ActionMenuItem {
  return useNavigateActionItem("XML anzeigen", XmlIcon, xmlUrl);
}
