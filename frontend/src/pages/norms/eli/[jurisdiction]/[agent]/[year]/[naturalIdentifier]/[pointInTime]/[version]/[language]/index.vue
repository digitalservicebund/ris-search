<script setup lang="ts">
import type { Dayjs } from "dayjs";
import Tab from "primevue/tab";
import TabList from "primevue/tablist";
import TabPanel from "primevue/tabpanel";
import TabPanels from "primevue/tabpanels";
import Tabs from "primevue/tabs";
import Toast from "primevue/toast";
import type { TreeNode } from "primevue/treenode";
import { useRoute } from "#app";
import Accordion from "~/components/Accordion.vue";
import NormActionsMenu from "~/components/ActionMenu/NormActionsMenu.vue";
import ContentWrapper from "~/components/CustomLayouts/ContentWrapper.vue";
import TableOfContentsLayout from "~/components/CustomLayouts/SidebarLayout.vue";
import IncompleteDataMessage from "~/components/IncompleteDataMessage.vue";
import NormMetadataFields from "~/components/Norm/Metadatafields/NormMetadataFields.vue";
import NormVersionList from "~/components/Norm/NormVersionList.vue";
import NormVersionWarning from "~/components/Norm/NormVersionWarning.vue";
import VersionsTeaser from "~/components/Norm/VersionsTeaser.vue";
import NormHeadingGroup from "~/components/NormHeadingGroup.vue";
import Properties from "~/components/Properties.vue";
import PropertiesItem from "~/components/PropertiesItem.vue";
import NormTableOfContents from "~/components/Ris/NormTableOfContents.vue";
import RisBreadcrumb from "~/components/Ris/RisBreadcrumb.vue";
import type { BreadcrumbItem } from "~/components/Ris/RisBreadcrumb.vue";
import {
  tabListStyles,
  tabPanelStyles,
  tabStyles,
} from "~/components/Tabs.styles";
import { useBackendURL } from "~/composables/useBackendURL";
import { useDynamicSeo } from "~/composables/useDynamicSeo";
import { useIntersectionObserver } from "~/composables/useIntersectionObserver";
import { useFetchNormContent } from "~/composables/useNormData";
import { useNormVersions } from "~/composables/useNormVersions";
import { usePrivateFeaturesFlag } from "~/composables/usePrivateFeaturesFlag";
import { DocumentKind, type LegislationWork } from "~/types";
import { dateFormattedDDMMYYYY } from "~/utils/dateFormatting";
import { formatDocumentKind } from "~/utils/displayValues";
import {
  getNormBreadcrumbTitle,
  getValidityStatus,
  getManifestationUrl,
  temporalCoverageToValidityInterval,
  getValidityStatusLabel,
} from "~/utils/norm";
import type { ValidityStatus } from "~/utils/norm";
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
  layout: "base", // use "base" layout to allow for full-width tab backgrounds
});

const route = useRoute();
const expressionEli = Object.values(route.params).join("/");
const privateFeaturesEnabled = usePrivateFeaturesFlag();

const { data, error, status } = await useFetchNormContent(expressionEli);

const metadata: Ref<LegislationWork | undefined> = computed(() => {
  return data.value?.legislationWork;
});

const html = computed(() => data.value?.html);
const htmlParts = computed(() => data.value?.htmlParts);

const backendURL = useBackendURL();

const zipUrl = computed(() =>
  getManifestationUrl(metadata.value, backendURL, "application/zip"),
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
  const list = [
    {
      label: formatDocumentKind(DocumentKind.Norm),
      route: `/search?category=${DocumentKind.Norm}`,
    },
    {
      route: `/norms/${metadata.value?.legislationIdentifier}`,
      label: normBreadcrumbTitle.value,
    },
  ];

  const isInForce =
    metadata.value?.workExample.legislationLegalForce === "InForce";
  if (!isInForce) {
    const validityIntervalLabel = `${dateFormattedDDMMYYYY(validityInterval.value?.from) ?? ""}-${dateFormattedDDMMYYYY(validityInterval.value?.to) ?? ""}`;
    list.push({ route: route.fullPath, label: validityIntervalLabel });
  }

  return list;
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
  const parts: string[] = [baseTitle];

  if (privateFeaturesEnabled) {
    const formattedValidFrom = dateFormattedDDMMYYYY(validFrom);
    if (formattedValidFrom) {
      parts.push(`Fassung vom ${formattedValidFrom}`);
    }

    const statusLabel = getValidityStatusLabel(status);
    if (statusLabel) {
      parts.push(statusLabel);
    }

    return truncateAtWord(parts.join(", "), 55) || undefined;
  }

  if (validFrom) {
    parts.push("Fassung vom [Inkrafttreten]");
  }

  if (status) {
    parts.push("[Status]");
  }
  const placeholder = parts.join(", ");
  return truncateAtWord(placeholder, 55) || undefined;
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

const description = computed<string | undefined>(() => {
  const shortTitle = metadata.value?.alternateName?.trim();
  const longTitle = metadata.value?.name?.trim();
  const chosen = shortTitle || longTitle || "";
  return chosen ? truncateAtWord(chosen, 150) : undefined;
});

useDynamicSeo({ title, description });
</script>

<template>
  <ContentWrapper border>
    <div v-if="status == 'pending'">Lade ...</div>
    <div v-if="!!metadata">
      <div class="container">
        <div class="flex items-center gap-8 print:hidden">
          <RisBreadcrumb :items="breadcrumbItems" class="grow" />
          <client-only> <NormActionsMenu :metadata="metadata" /></client-only>
        </div>
        <NormHeadingGroup :metadata="metadata" :html-parts="htmlParts" />
        <NormVersionWarning
          v-if="normVersionsStatus === 'success'"
          :versions="normVersions"
          :current-version="metadata"
        />
        <NormMetadataFields
          :abbreviation="metadata.abbreviation"
          :status="validityStatus"
          :valid-from="validityInterval?.from"
          :valid-to="validityInterval?.to"
        />
      </div>
      <Tabs value="0">
        <TabList :pt="tabListStyles">
          <Tab
            class="flex items-center gap-8"
            :pt="tabStyles"
            value="0"
            aria-label="Gesetzestext"
          >
            <IcBaselineSubject />
            Text
          </Tab>
          <Tab
            data-attr="norm-metadata-tab"
            class="flex items-center gap-8"
            :pt="tabStyles"
            value="1"
            aria-label="Details zum Gesetz"
          >
            <IcOutlineInfo />
            Details
          </Tab>
          <Tab
            data-attr="norm-versions-tab"
            class="flex items-center gap-8"
            :pt="tabStyles"
            value="2"
          >
            <IcOutlineRestore />
            Fassungen
          </Tab>
        </TabList>
        <TabPanels>
          <TabPanel value="0" :pt="tabPanelStyles">
            <TableOfContentsLayout class="container">
              <template #content>
                <IncompleteDataMessage />
                <Accordion
                  v-if="htmlParts?.officialToc"
                  header-expanded="Amtliches Inhaltsverzeichnis ausblenden"
                  header-collapsed="Amtliches Inhaltsverzeichnis einblenden"
                >
                  <div v-html="htmlParts?.officialToc" />
                </Accordion>
                <div v-observe-elements class="norm-view" v-html="html" />
              </template>
              <template #sidebar>
                <NormTableOfContents
                  v-if="metadata.workExample.tableOfContents.length > 0"
                  :table-of-contents="tableOfContents"
                  :selected-key="selectedEntry"
                />
              </template>
            </TableOfContentsLayout>
          </TabPanel>
          <TabPanel value="1" :pt="tabPanelStyles" class="pt-24 pb-80">
            <section aria-labelledby="detailsTabPanelTitle" class="container">
              <h2 id="detailsTabPanelTitle" class="ris-heading3-bold my-24">
                Details
              </h2>
              <IncompleteDataMessage class="my-24" />
              <Properties>
                <PropertiesItem
                  label="Ausfertigungsdatum:"
                  :value="dateFormattedDDMMYYYY(metadata.legislationDate)"
                />
                <PropertiesItem
                  label="Vollzitat:"
                  :value="htmlParts?.vollzitat"
                />
                <PropertiesItem
                  label="Stand:"
                  :value-list="htmlParts?.standangaben"
                >
                </PropertiesItem>
                <PropertiesItem
                  label="Hinweis zum Stand:"
                  :value-list="htmlParts?.standangabenHinweis"
                />
                <PropertiesItem
                  v-if="htmlParts?.prefaceContainer"
                  label="Besonderer Hinweis:"
                >
                  <div v-html="htmlParts?.prefaceContainer" />
                </PropertiesItem>
                <PropertiesItem label="Fußnoten:">
                  <template v-if="htmlParts?.headingNotes" #default>
                    <div v-html="htmlParts?.headingNotes" />
                  </template>
                </PropertiesItem>
                <PropertiesItem label="Download:">
                  <NuxtLink
                    data-attr="xml-zip-view"
                    class="ris-link1-regular"
                    external
                    :href="zipUrl"
                  >
                    <MaterialSymbolsDownload class="mr-2 inline" />
                    {{ metadata.abbreviation ?? "Inhalte" }} als ZIP
                    herunterladen
                  </NuxtLink>
                </PropertiesItem>
              </Properties>
            </section>
          </TabPanel>
          <TabPanel value="2" :pt="tabPanelStyles" class="pt-24 pb-80">
            <NormVersionList
              v-if="privateFeaturesEnabled"
              class="container"
              :status="normVersionsStatus"
              :current-legislation-identifier="
                metadata.workExample.legislationIdentifier
              "
              :versions="normVersions"
            />
            <VersionsTeaser v-else />
          </TabPanel>
        </TabPanels>
      </Tabs>
    </div>
  </ContentWrapper>
  <Toast />
</template>
