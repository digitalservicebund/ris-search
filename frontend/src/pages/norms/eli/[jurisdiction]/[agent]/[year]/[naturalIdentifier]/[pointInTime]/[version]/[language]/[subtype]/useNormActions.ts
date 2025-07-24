import { useToast } from "primevue/usetoast";
import type { LegislationWork } from "~/types";
import {
  type ActionMenuItem,
  PdfActionMenuItem,
  XmlActionMenuItem,
  LinkActionMenuItem,
  PrintActionMenuItem,
} from "~/utils/actionMenuItem";
import { getManifestationUrl } from "~/utils/normUtils";

export function useNormActions(metadata: Ref<LegislationWork | undefined>) {
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

  const toastService = useToast();

  const actions: ComputedRef<ActionMenuItem[]> = computed(() => {
    const items: ActionMenuItem[] = [
      new LinkActionMenuItem(
        toastService,
        "link",
        "Link zur jeweils gültigen Fassung",
        workUrl.value,
      ),
      new LinkActionMenuItem(
        toastService,
        "permalink",
        "Permalink zu dieser Fassung",
        window?.location.href,
      ),
      new PrintActionMenuItem(),
      new PdfActionMenuItem(),
    ];

    if (xmlUrl.value) {
      items.push(new XmlActionMenuItem(xmlUrl.value));
    }
    return items;
  });

  return { actions };
}
