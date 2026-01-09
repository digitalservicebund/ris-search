<script setup lang="ts">
import { RisSingleAccordion } from "@digitalservicebund/ris-ui/components";
import type { Dayjs } from "dayjs";
import { Tab, TabList, Tabs } from "primevue";
import type { TreeNode } from "primevue/treenode";
import type { ComputedRef } from "vue";
import { computed } from "vue";
import { useRoute } from "#app";
import { NuxtLink } from "#components";
import type { BreadcrumbItem } from "~/components/Breadcrumbs.vue";
import DetailsList from "~/components/DetailsList.vue";
import DetailsListEntry from "~/components/DetailsListEntry.vue";
import NormActionMenu from "~/components/documents/actionMenu/NormActionMenu.vue";
import IncompleteDataMessage from "~/components/documents/IncompleteDataMessage.vue";
import LegislationContent from "~/components/documents/norms/LegislationContent.vue";
import NormHeadingGroup from "~/components/documents/norms/NormHeadingGroup.vue";
import NormTableOfContents from "~/components/documents/norms/NormTableOfContents.vue";
import NormVersionList from "~/components/documents/norms/NormVersionList.vue";
import NormVersionWarning from "~/components/documents/norms/NormVersionWarning.vue";
import VersionsTeaser from "~/components/documents/norms/VersionsTeaser.vue";
import SidebarLayout from "~/components/SidebarLayout.vue";
import { useDynamicSeo } from "~/composables/useDynamicSeo";
import { useIntersectionObserver } from "~/composables/useIntersectionObserver";
import { useFetchNormContent } from "~/composables/useNormData";
import { useNormVersions } from "~/composables/useNormVersions";
import { usePrivateFeaturesFlag } from "~/composables/usePrivateFeaturesFlag";
import { fetchTranslationListWithIdFilter } from "~/composables/useTranslationData";
import { DocumentKind, type LegislationWork } from "~/types";
import { dateFormattedDDMMYYYY } from "~/utils/dateFormatting";
import { formatDocumentKind } from "~/utils/displayValues";
import type { ValidityStatus } from "~/utils/norm";
import {
  getManifestationUrl,
  getNormBreadcrumbTitle,
  getValidityStatus,
  getValidityStatusLabel,
  temporalCoverageToValidityInterval,
} from "~/utils/norm";
import { tocItemsToTreeNodes } from "~/utils/tableOfContents";
import { truncateAtWord } from "~/utils/textFormatting";
import IcBaselineSubject from "~icons/ic/baseline-subject";
import IcOutlineInfo from "~icons/ic/outline-info";
import IcOutlineRestore from "~icons/ic/outline-settings-backup-restore";
import MaterialSymbolsDownload from "~icons/material-symbols/download";

definePageMeta({
  // note: this is an expression ELI
  alias:
    "/eli/:jurisdiction/:agent/:year/:naturalIdentifier/:pointInTime/:version/:language",
  layout: "norm",
});

const route = useRoute();
const expressionEli = Object.values(route.params).join("/");
const privateFeaturesEnabled = usePrivateFeaturesFlag();

const { data, error, status } = await useFetchNormContent(expressionEli);

const metadata: Ref<LegislationWork | undefined> = computed(() => {
  return data.value?.legislationWork;
});

const abbreviation = data.value?.legislationWork.abbreviation;

const { translations } = abbreviation
  ? await fetchTranslationListWithIdFilter(abbreviation)
  : { translations: { value: [] } };

const translationUrl = computed(() => {
  if (translations.value && translations.value.length > 0) {
    return `/translations/${abbreviation}`;
  }
  return "";
});

const html = computed(() => data.value?.html);
const htmlParts = computed(() => data.value?.htmlParts);

const zipUrl = computed(() =>
  getManifestationUrl(metadata.value, "application/zip"),
);

if (error.value) {
  showError(error.value);
}

const tableOfContents: Ref<TreeNode[]> = computed(() => {
  if (!metadata.value?.workExample.tableOfContents) return [];
  const normPath = route.path;
  return tocItemsToTreeNodes(
    metadata.value.workExample.tableOfContents,
    normPath.concat("#"),
    normPath.concat("#"),
  );
});

const validityInterval = computed(() =>
  privateFeaturesEnabled
    ? temporalCoverageToValidityInterval(
        metadata.value?.workExample.temporalCoverage,
      )
    : undefined,
);

const validityStatus = computed(() => {
  if (metadata.value?.workExample && validityInterval)
    return getValidityStatus(validityInterval.value);
  return undefined;
});

const { selectedEntry, vObserveElements } = useIntersectionObserver();

const normBreadcrumbTitle = computed(() =>
  metadata.value ? getNormBreadcrumbTitle(metadata.value) : "",
);

const { status: normVersionsStatus, sortedVersions: normVersions } =
  useNormVersions(metadata.value?.legislationIdentifier);

const breadcrumbItems: ComputedRef<BreadcrumbItem[]> = computed(() => {
  const validFrom = dateFormattedDDMMYYYY(validityInterval.value?.from);
  const validFromDisplay = validFrom ? ` vom ${validFrom}` : "";

  return [
    {
      label: formatDocumentKind(DocumentKind.Norm),
      route: `/search?category=${DocumentKind.Norm}`,
    },
    {
      route: `/norms/${metadata.value?.legislationIdentifier}`,
      label: normBreadcrumbTitle.value + validFromDisplay,
    },
  ];
});

const buildOgTitle = (
  norm: LegislationWork,
  validFrom?: Dayjs,
  status?: ValidityStatus,
) => {
  const abbreviation = norm.abbreviation?.trim();
  const shortTitle = norm.alternateName?.trim();
  const baseTitle = abbreviation || shortTitle || "";

  if (!baseTitle) return undefined;
  const parts: string[] = [];

  if (privateFeaturesEnabled) {
    const formattedValidFrom = dateFormattedDDMMYYYY(validFrom);
    if (formattedValidFrom) {
      parts.push(`Fassung vom ${formattedValidFrom}`);
    }

    const statusLabel = getValidityStatusLabel(status);
    if (statusLabel) {
      parts.push(statusLabel);
    }

    return truncateAtWord(`${baseTitle}: ${parts.join(", ")}`, 55) || undefined;
  }

  if (validFrom) {
    parts.push(`Fassung vom [Inkrafttreten]`);
  }

  if (status) {
    parts.push("[Status]");
  }
  const placeholder = parts.join(", ");
  return truncateAtWord(`${baseTitle}: ${placeholder}`, 55) || undefined;
};

const title = computed<string | undefined>(() =>
  metadata.value
    ? buildOgTitle(
        metadata.value,
        validityInterval.value?.from,
        validityStatus.value,
      )
    : undefined,
);
const metadataItems = computed(() => {
  if (privateFeaturesEnabled) {
    return getNormMetadataItems(metadata.value);
  }
  return [
    {
      label: "Abkürzung",
      value: abbreviation ?? "",
    },
    {
      label: "Status",
    },
  ];
});

const description = computed<string | undefined>(() => {
  const shortTitle = metadata.value?.alternateName?.trim();
  const longTitle = metadata.value?.name?.trim();
  const chosen = shortTitle || longTitle || "";
  return chosen ? truncateAtWord(chosen, 150) : undefined;
});

const views = [
  { path: "text", label: "Text", icon: IcBaselineSubject },
  { path: "details", label: "Details", icon: IcOutlineInfo },
  { path: "versions", label: "Fassungen", icon: IcOutlineRestore },
] as const;

const currentView = computed(
  () => route.query.view?.toString() ?? views[0].path,
);

useDynamicSeo({ title, description });
</script>

<template>
  <div v-if="status == 'pending'">Lade ...</div>
  <div v-if="!!metadata">
    <div class="container">
      <div class="flex items-center gap-8 print:hidden">
        <Breadcrumbs :items="breadcrumbItems" class="grow" />
        <client-only>
          <NormActionMenu
            :metadata="metadata"
            :translation-url="translationUrl"
        /></client-only>
      </div>
      <NormHeadingGroup :metadata="metadata" :html-parts="htmlParts" />
      <NormVersionWarning
        v-if="normVersionsStatus === 'success'"
        :versions="normVersions"
        :current-version="metadata"
      />
      <Metadata :items="metadataItems" class="mb-48" />
    </div>

    <div class="border-b border-gray-600">
      <nav class="container -mb-1">
        <Tabs :value="currentView" :show-navigators="false">
          <TabList>
            <Tab
              v-for="view in views"
              :key="view.path"
              :value="view.path"
              :as="NuxtLink"
              :to="{ query: { view: view.path } }"
              :aria-controls="undefined"
              class="flex items-center gap-8"
            >
              <component :is="view.icon" />
              {{ view.label }}
            </Tab>
          </TabList>
        </Tabs>
      </nav>
    </div>

    <div class="min-h-96 bg-white py-24 print:py-0">
      <section v-if="currentView === 'text'">
        <SidebarLayout class="container">
          <template #content>
            <h2 class="sr-only">Text</h2>
            <IncompleteDataMessage />

            <RisSingleAccordion
              v-if="htmlParts.officialToc"
              class="mt-24"
              header-expanded="Amtliches Inhaltsverzeichnis ausblenden"
              header-collapsed="Amtliches Inhaltsverzeichnis einblenden"
            >
              <div v-html="htmlParts.officialToc" />
            </RisSingleAccordion>
            <LegislationContent v-observe-elements>
              <div v-html="html" />
            </LegislationContent>
          </template>
          <template #sidebar>
            <client-only>
              <NormTableOfContents
                v-if="metadata.workExample.tableOfContents.length > 0"
                :table-of-contents="tableOfContents"
                :selected-key="selectedEntry"
              />
            </client-only>
          </template>
        </SidebarLayout>
      </section>

      <section
        v-else-if="currentView === 'details'"
        aria-labelledby="detailsTabPanelTitle"
      >
        <div class="container">
          <h2 id="detailsTabPanelTitle" class="ris-heading3-bold my-24">
            Details
          </h2>
          <IncompleteDataMessage class="my-24" />
          <DetailsList>
            <DetailsListEntry
              label="Ausfertigungsdatum:"
              :value="dateFormattedDDMMYYYY(metadata.legislationDate)"
            />
            <DetailsListEntry label="Vollzitat:" :value="htmlParts.vollzitat" />
            <DetailsListEntry
              label="Stand:"
              :value-list="htmlParts.standangaben"
            />
            <DetailsListEntry
              label="Hinweis zum Stand:"
              :value-list="htmlParts.standangabenHinweis"
            />
            <DetailsListEntry
              v-if="htmlParts.prefaceContainer"
              label="Besonderer Hinweis:"
            >
              <div v-html="htmlParts.prefaceContainer" />
            </DetailsListEntry>
            <DetailsListEntry label="Fußnoten:">
              <template v-if="htmlParts.headingNotes" #default>
                <div v-html="htmlParts.headingNotes" />
              </template>
            </DetailsListEntry>
            <DetailsListEntry label="Download:">
              <NuxtLink class="ris-link1-regular" external :to="zipUrl">
                <MaterialSymbolsDownload class="mr-2 inline" />
                {{ metadata.abbreviation ?? "Inhalte" }} als ZIP herunterladen
              </NuxtLink>
            </DetailsListEntry>
          </DetailsList>
        </div>
      </section>

      <section v-else-if="currentView === 'versions'">
        <div class="container">
          <NormVersionList
            v-if="privateFeaturesEnabled"
            :status="normVersionsStatus"
            :current-legislation-identifier="
              metadata.workExample.legislationIdentifier
            "
            :versions="normVersions"
          />
          <VersionsTeaser v-else />
        </div>
      </section>
    </div>
  </div>
</template>

<style lang="css" scoped>
@reference "~/assets/main.css";

:deep(.official-toc div) {
  @apply lg:pl-32;
  @apply ml-16;
  @apply mb-16;
  &.level-1 {
    @apply ml-0;
    @apply ris-label1-bold;
  }
  &.level-5 {
    @apply ml-8;
  }
  &.level-10 {
    @apply ml-16;
  }
}
</style>
