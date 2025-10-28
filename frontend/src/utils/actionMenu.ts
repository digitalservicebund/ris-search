import type {
  ActionMenuItem,
  ActionsMenuProps,
} from "~/components/ActionMenu/ActionsMenu.vue";
import ENGIcon from "~/components/icons/ENGIcon.vue";
import PDFIcon from "~/components/icons/PDFIcon.vue";
import UpdatingLinkIcon from "~/components/icons/UpdatingLinkIcon.vue";
import XMLIcon from "~/components/icons/XMLIcon.vue";
import MaterialSymbolsLink from "~icons/material-symbols/link";
import MaterialSymbolsPrint from "~icons/material-symbols/print";

export function createActionMenuItems(
  records: ActionsMenuProps,
  copyUrlCommand: (url: string) => Promise<void>,
  navigationCommand: (url: string) => Promise<void>,
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
      url: link.url,
    });
  }

  items.push(
    {
      key: "permalink",
      label: records.permalink.label,
      iconComponent: MaterialSymbolsLink,
      command: async () => await copyUrlCommand(records.permalink.url),
      url: records.permalink.url,
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
      iconComponent: PDFIcon,
      disabled: true,
    },
  );

  if (xmlUrl) {
    items.push({
      key: "xml",
      label: "XML anzeigen",
      iconComponent: XMLIcon,
      command: async () => await navigationCommand(xmlUrl),
      url: records.xmlUrl,
      dataAttribute: "xml-view",
    });
  }

  if (translationUrl) {
    items.push({
      key: "translation",
      label: "Zur englischen Übersetzung",
      iconComponent: ENGIcon,
      command: async () => await navigationCommand(translationUrl),
      url: translationUrl,
    });
  }
  return items;
}
