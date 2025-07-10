import type { ActionMenuItem } from "~/components/ActionsMenu.vue";
import PDFIcon from "~/components/icons/PDFIcon.vue";
import XMLIcon from "~/components/icons/XMLIcon.vue";
import type { CaseLaw } from "~/types";
import { getEncodingURL } from "~/utils/caseLawUtils";
import { isPrototypeProfile } from "~/utils/config";
import MaterialSymbolsLink from "~icons/material-symbols/link";

export function useCaseLawActions(caseLaw: Ref<CaseLaw | null>) {
  const enablePdfButton = !isPrototypeProfile();
  const onPrint = () => {
    if (window) window.print();
  };
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
      {
        key: "pdf",
        label: "Drucken oder als PDF speichern",
        iconComponent: PDFIcon,
        command: onPrint,
        disabled: !enablePdfButton,
      },
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
