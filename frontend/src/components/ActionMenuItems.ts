import type { ActionMenuItem } from "~/components/ActionsMenu.vue";
import PDFIcon from "~/components/icons/PDFIcon.vue";
import MaterialSymbolsPrint from "~icons/material-symbols/print";

export const printActionMenuItem: ActionMenuItem = {
  key: "print",
  label: "Drucken",
  iconComponent: MaterialSymbolsPrint,
  command: () => {
    if (window) window.print();
  },
};

export const pdfActionMenuItem: ActionMenuItem = {
  key: "pdf",
  label: "Als PDF speichern",
  iconComponent: PDFIcon,
  command: {},
  disabled: true,
};
