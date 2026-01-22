import type { ActionMenuItem } from "~/components/documents/actionMenu/ActionMenu.vue";

/**
 * Use this function to create an action item which navigates to the given url.
 * @param label
 * @param icon
 * @param url
 */
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
