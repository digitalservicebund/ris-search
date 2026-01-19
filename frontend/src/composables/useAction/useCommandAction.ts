import type { ActionMenuItem } from "~/components/documents/actionMenu/ActionMenu.vue";

export function useCommandAction(
  label: string,
  icon: Component,
  command?: () => Promise<void>,
  disabled: boolean = false,
): ActionMenuItem {
  return {
    key: useId(),
    label: label,
    iconComponent: icon,
    command: command,
    disabled: disabled,
  };
}
