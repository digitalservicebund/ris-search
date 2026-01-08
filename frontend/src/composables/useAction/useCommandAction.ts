import type { ActionMenuItem1 } from "~/components/documents/actionMenu/ActionMenu1.vue";

export function useCommandAction(
  label: string,
  icon: Component,
  command?: () => Promise<void>,
  disabled: boolean = false,
): ActionMenuItem1 {
  return {
    key: useId(),
    label: label,
    iconComponent: icon,
    command: command,
    disabled: disabled,
  };
}
