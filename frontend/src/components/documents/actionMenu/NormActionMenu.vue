<script setup lang="ts">
import ActionMenu from "~/components/documents/actionMenu/ActionMenu.vue";
import { useCopyUrlActionItem } from "~/composables/useActionMenuItem/useCopyUrlActionItem";
import { useNavigateActionItem } from "~/composables/useActionMenuItem/useNavigateActionItem";
import { usePdfActionItem } from "~/composables/useActionMenuItem/usePdfActionItem";
import { usePrintActionItem } from "~/composables/useActionMenuItem/usePrintActionItem";
import { useXmlActionItem } from "~/composables/useActionMenuItem/useXmlActionItem";
import type { LegislationExpression } from "~/types";
import { getManifestationUrl } from "~/utils/norm";
import EngIcon from "~icons/custom/eng";
import UpdatingLinkIcon from "~icons/custom/updatingLink";

const { metadata, translationUrl } = defineProps<{
  metadata: LegislationExpression | undefined;
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
      "Link zur jeweils gültigen Fassung kopieren",
      UpdatingLinkIcon,
    ),
    useCopyUrlActionItem(href, "Permalink zu dieser Fassung kopieren"),
    usePrintActionItem(),
    usePdfActionItem(),
    useXmlActionItem(xmlUrl),
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
