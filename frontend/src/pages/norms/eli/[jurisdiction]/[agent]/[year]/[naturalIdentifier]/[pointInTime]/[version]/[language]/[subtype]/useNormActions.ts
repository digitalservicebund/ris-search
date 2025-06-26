import type { LegislationWork } from "~/types";
import { isPrototypeProfile } from "~/utils/config";
import { useToast } from "primevue/usetoast";
import UpdatingLinkIcon from "~/components/icons/UpdatingLinkIcon.vue";
import MaterialSymbolsLink from "~icons/material-symbols/link";
import PDFIcon from "~/components/icons/PDFIcon.vue";
import XMLIcon from "~/components/icons/XMLIcon.vue";
import type { ActionMenuItem } from "~/components/ActionsMenu.vue";
import { getManifestationUrl } from "~/utils/normsUtils";

export function useNormActions(metadata: Ref<LegislationWork | undefined>) {
  const enablePdfButton = !isPrototypeProfile();
  const onPrint = () => {
    if (window) window.print();
  };
  const backendURL = useBackendURL();
  const xmlUrl = computed(() =>
    getManifestationUrl(metadata.value, backendURL, "application/xml"),
  );

  const workUrl = computed(() => {
    if (!import.meta.client || !metadata.value) return undefined;
    const href = window.location.href;
    const workEli = metadata.value?.legislationIdentifier;
    return href.replace(/eli.+$/, workEli);
  });

  const toast = useToast();

  function showSuccessMessage() {
    toast.add({
      severity: "success",
      summary: "Kopiert!",
      life: 3000,
      closable: false,
    });
  }

  const copyWorkUrl = async () => {
    if (workUrl.value) {
      await navigator.clipboard.writeText(workUrl.value);
    }
    showSuccessMessage();
  };
  const copyCurrentUrl = async () => {
    await navigator.clipboard.writeText(window.location.href);
    showSuccessMessage();
  };

  const actions: ComputedRef<ActionMenuItem[]> = computed(() => {
    const items: ActionMenuItem[] = [
      {
        key: "link",
        label: "Link zur jeweils gültigen Fassung",
        iconComponent: UpdatingLinkIcon,
        command: copyWorkUrl,
        url: workUrl.value,
      },
      {
        key: "permalink",
        label: "Permalink zu dieser Fassung",
        iconComponent: MaterialSymbolsLink,
        command: copyCurrentUrl,
        url: window?.location.href,
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
