import type { ActionMenuItem } from "~/components/documents/actionMenu/ActionMenu.vue";

/**
 * Use this function to construct a action menu item where the action is a
 * simple command (e.g. print, ...).
 * @param label
 * @param icon
 * @param command
 * @param disabled
 */
export function useCommandActionItem(
  label: string,
  icon: Component,
  command?: () => Promise<void>,
  disabled: boolean = false,
): ActionMenuItem {
  return {
    label: label,
    iconComponent: icon,
    command: command,
    disabled: disabled,
  };
}
