import type { ActionMenuItem } from "~/components/documents/actionMenu/ActionMenu.vue";

export function useNavigateAction(
  label: string,
  icon: Component,
  url?: string,
): ActionMenuItem {
  return {
    key: useId(),
    label: label,
    iconComponent: icon,
    url: url,
    disabled: !url,
  };
}
