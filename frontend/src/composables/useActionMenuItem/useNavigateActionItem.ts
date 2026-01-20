import type { ActionMenuItem } from "~/components/documents/actionMenu/ActionMenu.vue";

export function useNavigateActionItem(
  label: string,
  icon: Component,
  url?: string,
): ActionMenuItem {
  return {
    label: label,
    iconComponent: icon,
    url: url,
    disabled: !url,
  };
}
