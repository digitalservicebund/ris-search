import type { ActionMenuItem } from "~/components/documents/actionMenu/ActionMenu.vue";
import type { ActionMenuProps } from "~/components/documents/actionMenu/ActionMenuWrapper.vue";
import EngIcon from "~icons/custom/eng";
import PdfIcon from "~icons/custom/pdf";
import UpdatingLinkIcon from "~icons/custom/updatingLink";
import XmlIcon from "~icons/custom/xml";
import MaterialSymbolsLink from "~icons/material-symbols/link";
import MaterialSymbolsPrint from "~icons/material-symbols/print";

export function createActionMenuItems(
  records: ActionMenuProps,
  copyUrlCommand: (url: string) => Promise<void>,
): ActionMenuItem[] {
  const items: ActionMenuItem[] = [];
  const link = records.link;
  const xmlUrl = records.xmlUrl;
  const translationUrl = records.translationUrl;

  if (link) {
    items.push({
      key: "link",
      label: link.label ?? "Link kopieren",
      iconComponent: UpdatingLinkIcon,
      command: async () => await copyUrlCommand(link.url),
    });
  }

  items.push(
    {
      key: "permalink",
      label: records.permalink.label,
      iconComponent: MaterialSymbolsLink,
      command: async () => await copyUrlCommand(records.permalink.url),
      disabled: records.permalink.disabled,
    },
    {
      key: "print",
      label: "Drucken",
      iconComponent: MaterialSymbolsPrint,
      command: () => {
        if (globalThis) globalThis.print();
      },
    },
    {
      key: "pdf",
      label: "Als PDF speichern",
      iconComponent: PdfIcon,
      disabled: true,
    },
  );

  if (xmlUrl) {
    items.push({
      key: "xml",
      label: "XML anzeigen",
      iconComponent: XmlIcon,
      url: records.xmlUrl,
      analyticsId: "xml-view",
    });
  }

  if (translationUrl) {
    items.push({
      key: "translation",
      label: "Zur englischen Übersetzung",
      iconComponent: EngIcon,
      url: translationUrl,
    });
  }
  return items;
}
