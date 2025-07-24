import type { ToastServiceMethods } from "primevue";
import type { MenuItem } from "primevue/menuitem";
import PDFIcon from "~/components/icons/PDFIcon.vue";
import UpdatingLinkIcon from "~/components/icons/UpdatingLinkIcon.vue";
import XMLIcon from "~/components/icons/XMLIcon.vue";
import MaterialSymbolsLink from "~icons/material-symbols/link";
import MaterialSymbolsPrint from "~icons/material-symbols/print";

export class ActionMenuItem implements MenuItem {
  key: string;
  label: string;
  iconComponent: Component;
  command: () => void | Promise<void>;
  dataAttribute?: string;
  url?: string;
  disabled?: boolean;

  constructor(
    key: string,
    label: string,
    iconComponent: Component,
    command: () => void = () => {},
    url?: string,
    disabled?: boolean,
    dataAttribute?: string,
  ) {
    this.key = key;
    this.label = label;
    this.iconComponent = iconComponent;
    this.command = command;
    this.dataAttribute = dataAttribute;
    this.url = url;
    this.disabled = disabled;
  }
}

export class PrintActionMenuItem extends ActionMenuItem {
  constructor() {
    super("print", "Drucken", MaterialSymbolsPrint, () => {
      if (window) window.print();
    });
  }
}

export class PdfActionMenuItem extends ActionMenuItem {
  constructor() {
    super("pdf", "Als PDF speichern", PDFIcon, () => {}, undefined, true);
  }
}

export class XmlActionMenuItem extends ActionMenuItem {
  constructor(xmlUrl: string) {
    super(
      "xml",
      "XML anzeigen",
      XMLIcon,
      async () => await navigateTo(xmlUrl, { external: true }),
      xmlUrl,
      false,
      "xml-view",
    );
  }
}

export class LinkActionMenuItem extends ActionMenuItem {
  constructor(
    toastService: ToastServiceMethods,
    key: "link" | "permalink",
    label: string,
    url?: string,
    disabled: boolean = false,
  ) {
    const icon = key === "link" ? UpdatingLinkIcon : MaterialSymbolsLink;

    super(
      key,
      label,
      icon,
      LinkActionMenuItem.copyUrl(toastService, url),
      url,
      disabled,
    );
  }

  private static copyUrl(toastService: ToastServiceMethods, url?: string) {
    return async () => {
      if (url) {
        await navigator.clipboard.writeText(url);
        toastService.add({
          severity: "success",
          summary: "Kopiert!",
          life: 3000,
          closable: false,
        });
      }
    };
  }
}
