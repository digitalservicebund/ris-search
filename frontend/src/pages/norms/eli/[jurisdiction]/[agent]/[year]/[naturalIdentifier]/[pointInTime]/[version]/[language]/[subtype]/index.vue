<script setup lang="ts">
import NormTableOfContents from "@/components/Ris/NormTableOfContents.vue";
import type { TreeNode } from "primevue/treenode";
import { tocItemsToTreeNodes } from "@/utils/tableOfContents";
import type { LegislationWork } from "@/types";
import { useFetchNormContent } from "./useNormData";
import { useRoute } from "#app";
import { useIntersectionObserver } from "@/composables/useIntersectionObserver";
import MetadataField from "@/components/MetadataField.vue";
import {
  splitTemporalCoverage,
  translateLegalForce,
} from "@/utils/dateFormatting";
import RisBreadcrumb from "@/components/Ris/RisBreadcrumb.vue";
import Accordion from "@/components/Accordion.vue";
import { getNormBreadcrumbTitle } from "./titles";
import NormHeadingGroup from "./NormHeadingGroup.vue";
import TableOfContentsLayout from "~/components/CustomLayouts/SidebarLayout.vue";
import IncompleteDataMessage from "@/components/IncompleteDataMessage.vue";
import Tabs from "primevue/tabs";
import TabList from "primevue/tablist";
import Tab from "primevue/tab";
import TabPanels from "primevue/tabpanels";
import TabPanel from "primevue/tabpanel";
import IcBaselineSubject from "~icons/ic/baseline-subject";
import IcOutlineInfo from "~icons/ic/outline-info";
import IcOutlineRestore from "~icons/ic/outline-settings-backup-restore";
import MaterialSymbolsDownload from "~icons/material-symbols/download";
import PropertiesItem from "~/components/PropertiesItem.vue";
import Properties from "~/components/Properties.vue";
import {
  tabListStyles,
  tabPanelStyles,
  tabStyles,
} from "~/components/Tabs.styles";
import { isPrototypeProfile } from "~/utils/config";
import ContentWrapper from "~/components/CustomLayouts/ContentWrapper.vue";
import NormVersionList from "~/components/Norm/NormVersionList.vue";
import VersionWarningMessage from "~/components/Norm/VersionWarningMessage.vue";
import type { BreadcrumbItem } from "~/components/Ris/RisBreadcrumb.vue";

definePageMeta({
  // note: this is an expression ELI that additionally specifies the subtype component of a manifestation ELI
  alias:
    "/eli/:jurisdiction/:agent/:year/:naturalIdentifier/:pointInTime/:version/:language/:subtype",
  layout: "base", // use "base" layout to allow for full-width tab backgrounds
});

const route = useRoute();
const expressionEli = Object.values(route.params).join("/");

const { data, error, status } = await useFetchNormContent(expressionEli);

const metadata: Ref<LegislationWork | undefined> = computed(() => {
  return data.value?.legislationWork;
});

const title = computed(() => {
  return [metadata.value?.abbreviation, metadata.value?.name]
    .filter((titlePart) => !isStringEmpty(titlePart))
    .join(" – ");
});
useHead({ title: title.value });

const html: Ref<string> = computed(() => data.value.html);
const htmlParts = computed(() => data.value.htmlParts);

const backendURL = useBackendURL();

function getManifestationUrl(format: string) {
  const encoding = metadata.value?.workExample?.encoding.find(
    (e) => e.encodingFormat === format,
  );
  return encoding?.contentUrl ? backendURL + encoding.contentUrl : undefined;
}

const xmlUrl = computed(() => getManifestationUrl("application/xml"));
const zipUrl = computed(() => getManifestationUrl("application/zip"));

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

const translatedLegalForce = computed(() =>
  translateLegalForce(metadata.value?.workExample.legislationLegalForce),
);
const temporalCoverage = computed(() =>
  isPrototypeProfile()
    ? []
    : splitTemporalCoverage(metadata.value?.workExample.temporalCoverage),
);

const { selectedEntry, vObserveElements } = useIntersectionObserver();

const normBreadcrumbTitle = computed(() =>
  metadata.value ? getNormBreadcrumbTitle(metadata.value) : "",
);

const { status: normVersionsStatus, sortedVersions: normVersions } =
  useNormVersions(metadata.value?.legislationIdentifier);

const breadcrumbItems: ComputedRef<BreadcrumbItem[]> = computed(() => {
  const list = [
    {
      route: `/norms/${metadata.value?.legislationIdentifier}`,
      label: normBreadcrumbTitle.value,
    },
  ];

  const isInForce =
    metadata.value?.workExample.legislationLegalForce === "InForce";
  if (!isInForce) {
    const temporalCoverageLabel = temporalCoverage.value?.join("–");
    list.push({ route: route.fullPath, label: temporalCoverageLabel });
  }

  return list;
});
</script>

<template>
  <ContentWrapper border>
    <div v-if="status == 'pending'">Lade ...</div>
    <div v-if="!!metadata">
      <div class="container">
        <div class="flex items-center gap-8 print:hidden">
          <RisBreadcrumb type="norm" :items="breadcrumbItems" class="grow" />
          <FileActionsMenu :xml-url="xmlUrl" />
        </div>
        <NormHeadingGroup :metadata="metadata" :html-parts="htmlParts" />

        <VersionWarningMessage
          v-if="normVersionsStatus === 'success'"
          :versions="normVersions"
          :current-expression="metadata.workExample.legislationIdentifier"
        />
        <div class="mt-8 mb-48 flex flex-wrap items-end gap-24">
          <MetadataField
            v-if="metadata.abbreviation"
            id="abbreviation"
            label="Abkürzung"
            :value="metadata.abbreviation"
          />
          <MetadataField
            id="status"
            label="Status"
            :value="translatedLegalForce"
          />
          <MetadataField
            v-if="temporalCoverage[0]"
            id="validFrom"
            label="Fassung gültig seit"
            :value="temporalCoverage[0]"
          />
          <MetadataField
            v-if="temporalCoverage[1]"
            id="validTo"
            label="Fassung gültig bis"
            :value="temporalCoverage[1]"
          />
        </div>
      </div>
      <Tabs value="0" lazy>
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
                  v-if="htmlParts.officialToc"
                  header-expanded="Amtliches Inhaltsverzeichnis ausblenden"
                  header-collapsed="Amtliches Inhaltsverzeichnis einblenden"
                >
                  <div v-html="htmlParts.officialToc" />
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
                  :value="formattedDate(metadata.legislationDate)"
                />
                <PropertiesItem
                  label="Vollzitat:"
                  :value="htmlParts.vollzitat"
                />
                <PropertiesItem
                  label="Stand:"
                  :value-list="htmlParts.standangaben"
                >
                </PropertiesItem>
                <PropertiesItem
                  label="Hinweis zum Stand:"
                  :value-list="htmlParts.standangabenHinweis"
                />
                <PropertiesItem
                  v-if="htmlParts.prefaceContainer"
                  label="Besonderer Hinweis:"
                >
                  <div v-html="htmlParts.prefaceContainer" />
                </PropertiesItem>
                <PropertiesItem label="Fußnoten:">
                  <template v-if="htmlParts.headingNotes" #default>
                    <div v-html="htmlParts.headingNotes" />
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
              v-if="!isPrototypeProfile()"
              class="container"
              :status="normVersionsStatus"
              :versions="normVersions"
            />
            <div v-else class="max-w-prose">
              <h3 class="ris-heading3-bold my-24">
                Fassungen sind noch nicht verfügbar
              </h3>
              <p class="ris-body1-regular">
                Mit dem Livegang des neuen Rechtsinformationsportals werden auch
                außer Kraft getretene und zukünftig in Kraft tretende Fassungen
                der Gesetze und Verordnungen zur Verfügung gestellt.
              </p>
              <h3 class="ris-heading3-bold mt-48 mb-24">
                Unterstützen Sie uns bei der Entwicklung dieser Funktion
              </h3>
              <p class="ris-body1-regular">
                Unser Ziel ist es, Rechtsinformationen für Bürgerinnen und
                Bürger leichter zugänglich zu machen. Deshalb suchen wir
                Menschen, die ihre Erfahrungen mit uns teilen und unseren
                Service testen.
              </p>
              <ButtonLink class="mt-16" variant="primary" href="/nutzungstests">
                Mehr über Nutzungstest erfahren
              </ButtonLink>
            </div>
          </TabPanel>
        </TabPanels>
      </Tabs>
    </div>
  </ContentWrapper>
</template>
