<script setup lang="ts">
import type { MenuItem } from "primevue/menuitem";
import { useToast } from "primevue/usetoast";
import LargeActionsMenu from "~/components/ActionMenu/LargeActionsMenu.vue";
import SmallActionsMenu from "~/components/ActionMenu/SmallActionsMenu.vue";
import PDFIcon from "~/components/icons/PDFIcon.vue";
import UpdatingLinkIcon from "~/components/icons/UpdatingLinkIcon.vue";
import XMLIcon from "~/components/icons/XMLIcon.vue";
import MaterialSymbolsLink from "~icons/material-symbols/link";
import MaterialSymbolsPrint from "~icons/material-symbols/print";

export type ActionsMenuProps = {
  link?: {
    url: string;
    label: string;
  };
  permalink: {
    url?: string;
    label: string;
    disabled?: boolean;
  };
  xmlUrl?: string;
};

export type ActionMenuItem = Omit<MenuItem, "icon"> & {
  iconComponent: Component;
};

const props = defineProps<ActionsMenuProps>();
const toastService = useToast();

const actions: ComputedRef<ActionMenuItem[]> = computed(() => {
  const items: ActionMenuItem[] = [];

  if (props.link) {
    items.push({
      key: "link",
      label: props.link.label ?? "Link kopieren",
      iconComponent: UpdatingLinkIcon,
      command: createCopyUrlCommand(props.link.url),
      url: props.link.url,
    });
  }

  items.push(
    ...[
      {
        key: "permalink",
        label: props.permalink.label,
        iconComponent: MaterialSymbolsLink,
        command: createCopyUrlCommand(props.permalink.url),
        url: props.permalink.url,
        disabled: props.permalink.disabled,
      },
      {
        key: "print",
        label: "Drucken",
        iconComponent: MaterialSymbolsPrint,
        command: () => {
          if (window) window.print();
        },
      },
      {
        key: "pdf",
        label: "Als PDF speichern",
        iconComponent: PDFIcon,
        disabled: true,
      },
    ],
  );

  if (props.xmlUrl) {
    items.push({
      key: "xml",
      label: "XML anzeigen",
      iconComponent: XMLIcon,
      command: async () => await navigateTo(props.xmlUrl, { external: true }),
      url: props.xmlUrl,
      dataAttribute: "xml-view",
    });
  }
  return items;
});

function createCopyUrlCommand(url?: string) {
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
</script>

<template>
  <div class="sm:hidden">
    <SmallActionsMenu :actions="actions" />
  </div>
  <div class="hidden sm:flex">
    <LargeActionsMenu :actions="actions" />
  </div>
</template>
