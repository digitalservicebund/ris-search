<script setup lang="ts">
import ActionMenu from "~/components/documents/actionMenu/ActionMenu.vue";
import { useCommandActionItem } from "~/composables/useActionMenuItem/useCommandActionItem";
import { useCopyUrlActionItem } from "~/composables/useActionMenuItem/useCopyUrlActionItem";
import { useNavigateActionItem } from "~/composables/useActionMenuItem/useNavigateActionItem";
import type { LegislationWork } from "~/types";
import { getManifestationUrl } from "~/utils/norm";
import EngIcon from "~icons/custom/eng";
import PdfIcon from "~icons/custom/pdf";
import UpdatingLinkIcon from "~icons/custom/updatingLink";
import XmlIcon from "~icons/custom/xml";
import MaterialSymbolsPrint from "~icons/material-symbols/print";

const { metadata, translationUrl } = defineProps<{
  metadata: LegislationWork | undefined;
  translationUrl: string | undefined;
}>();

const actions = computed(() => {
  const href = useRequestURL().href;
  const workEli = metadata?.legislationIdentifier;
  const workEliLink = workEli ? href.replace(/eli.+$/, workEli) : undefined;
  const xmlUrl = useBackendUrl(
    getManifestationUrl(metadata, "application/xml"),
  );

  const actions = [
    useCopyUrlActionItem(
      workEliLink,
      "Link zur jeweils gültigen Fassung",
      UpdatingLinkIcon,
    ),
    useCopyUrlActionItem(href, "Permalink zu dieser Fassung"),
    useCommandActionItem("Drucken", MaterialSymbolsPrint, async () =>
      globalThis?.print(),
    ),
    useCommandActionItem("Als PDF speichern", PdfIcon, undefined, true),
    useNavigateActionItem("XML anzeigen", XmlIcon, xmlUrl),
  ];

  if (translationUrl) {
    actions.push(
      useNavigateActionItem(
        "Zur englischen Übersetzung",
        EngIcon,
        translationUrl,
      ),
    );
  }

  return actions;
});
</script>

<template>
  <ActionMenu :actions />
</template>
