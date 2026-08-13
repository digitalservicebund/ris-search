<script setup lang="ts">
import EngIcon from "~icons/custom/eng";
import UpdatingLinkIcon from "~icons/custom/updatingLink";
import { useCopyUrlActionItem } from "~/composables/useActionMenuItem/useCopyUrlActionItem";
import { useNavigateActionItem } from "~/composables/useActionMenuItem/useNavigateActionItem";
import { usePdfActionItem } from "~/composables/useActionMenuItem/usePdfActionItem";
import { usePrintActionItem } from "~/composables/useActionMenuItem/usePrintActionItem";
import { useXmlActionItem } from "~/composables/useActionMenuItem/useXmlActionItem";
import type { LegislationExpression } from "~/types/api";

const { metadata, translationUrl } = defineProps<{
  metadata: LegislationExpression;
  translationUrl: string | undefined;
}>();

function normalizeRisAbbreviation(risAbbreviation: string) {
  if (risAbbreviation && /^[a-zA-Z0-9\s]*$/.test(risAbbreviation)) {
    return risAbbreviation.toLowerCase().replace(/\s+/g, "_");
  }

  return undefined;
}

const actions = computed(() => {
  const requestUrl = useRequestURL();
  requestUrl.search = "";
  const href = requestUrl.href;

  const workEli = metadata.exampleOfWork.legislationIdentifier;
  const speakableUrlPath = normalizeRisAbbreviation(metadata.risAbbreviation);
  const dynamicExpressionLink = href.replace(
    /eli.+$/,
    speakableUrlPath ?? workEli,
  );
  const xmlUrl = useBackendUrl(
    getManifestationUrl(metadata, "application/xml"),
  );

  const actionsList = [
    useCopyUrlActionItem(
      dynamicExpressionLink,
      "Link zur jeweils gültigen Fassung kopieren",
      UpdatingLinkIcon,
    ),
    useCopyUrlActionItem(href, "Link zu dieser Fassung kopieren"),
    usePrintActionItem(),
    usePdfActionItem(),
    useXmlActionItem(xmlUrl),
  ];

  if (translationUrl) {
    actionsList.push(
      useNavigateActionItem(
        "Zur englischen Übersetzung",
        EngIcon,
        translationUrl,
      ),
    );
  }

  return actionsList;
});
</script>

<template>
  <DocumentsActionMenu :actions />
</template>
