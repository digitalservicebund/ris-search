import { useToast } from "primevue/usetoast";
import type { CaseLaw } from "~/types";
import {
  type ActionMenuItem,
  LinkActionMenuItem,
  PdfActionMenuItem,
  PrintActionMenuItem,
  XmlActionMenuItem,
} from "~/utils/actionMenuItem";
import { getEncodingURL } from "~/utils/caseLawUtils";

export function useCaseLawActions(caseLaw: Ref<CaseLaw | null>) {
  const backendURL = useBackendURL();
  const xmlUrl = computed(() =>
    getEncodingURL(caseLaw.value, backendURL, "application/xml"),
  );

  const toastService = useToast();

  const actions: ComputedRef<ActionMenuItem[]> = computed(() => {
    const items: ActionMenuItem[] = [
      new LinkActionMenuItem(
        toastService,
        "permalink",
        "Link kopieren",
        undefined,
        true,
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
