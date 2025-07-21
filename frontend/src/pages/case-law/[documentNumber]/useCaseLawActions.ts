import {
  pdfActionMenuItem,
  printActionMenuItem,
} from "~/components/ActionMenuItems";
import type { ActionMenuItem } from "~/components/ActionsMenu.vue";
import XMLIcon from "~/components/icons/XMLIcon.vue";
import type { CaseLaw } from "~/types";
import { getEncodingURL } from "~/utils/caseLawUtils";
import MaterialSymbolsLink from "~icons/material-symbols/link";

export function useCaseLawActions(caseLaw: Ref<CaseLaw | null>) {
  const backendURL = useBackendURL();
  const xmlUrl = computed(() =>
    getEncodingURL(caseLaw.value, backendURL, "application/xml"),
  );

  const actions: ComputedRef<ActionMenuItem[]> = computed(() => {
    const items: ActionMenuItem[] = [
      {
        key: "link",
        label: "Link kopieren",
        iconComponent: MaterialSymbolsLink,
        disabled: true,
      },
      printActionMenuItem,
      pdfActionMenuItem,
    ];

    if (xmlUrl.value) {
      items.push({
        key: "xml",
        label: "XML anzeigen",
        iconComponent: XMLIcon,
        dataAttribute: "xml-view",
        command: async () => await navigateTo(xmlUrl.value, { external: true }),
        url: xmlUrl.value,
      });
    }
    return items;
  });

  return { actions };
}
