import type { ActionMenuItem1 } from "~/components/documents/actionMenu/ActionMenu1.vue";

export function useNavigateAction(
  label: string,
  icon: Component,
  url?: string,
): ActionMenuItem1 {
  return {
    key: useId(),
    label: label,
    iconComponent: icon,
    url: url,
    disabled: !url,
  };
}
