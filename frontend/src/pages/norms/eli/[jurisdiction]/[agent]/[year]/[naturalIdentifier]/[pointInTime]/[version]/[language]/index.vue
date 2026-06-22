<script setup lang="ts">
import { Button, Tab, TabList, Tabs } from "primevue";
import IcBaselineSubject from "~icons/ic/baseline-subject";
import IconFileDownload from "~icons/ic/outline-file-download";
import IcOutlineInfo from "~icons/ic/outline-info";
import IcOutlineRestore from "~icons/ic/outline-settings-backup-restore";
import { NuxtLink } from "#components";
import type { BreadcrumbItem } from "~/components/Breadcrumbs.vue";
import DateInput from "~/components/DateInput.vue";
import { useNormSeo } from "~/composables/useNormSeo";
import { DocumentKind, type LegislationExpression } from "~/types/api";

definePageMeta({
  // note: this is an expression ELI
  alias:
    "/eli/:jurisdiction/:agent/:year/:naturalIdentifier/:pointInTime/:version/:language",
  layout: false,
  skipLinks: [
    { label: "Zum Inhalt", to: "#main" },
    { label: "Zum Gesetzestext", to: "#content" },
    { label: "Zum Fußbereich", to: "#footer" },
  ],
});

const route = useRoute();
const expressionEli = Object.values(route.params).join("/");
const privateFeaturesEnabled = usePrivateFeaturesFlag();

const { data, error } = await useFetchNormContent(expressionEli);

if (error.value || !data.value) {
  showError({ status: error.value?.status ?? 500 });
}

const metadata: Ref<LegislationExpression> = computed(() => {
  return data.value.legislation;
});

const abbreviation = data.value.legislation.abbreviation;

const { translations } = abbreviation
  ? await fetchTranslationListWithIdFilter(abbreviation)
  : { translations: { value: [] } };

const translationUrl = computed(() => {
  if (translations.value && translations.value.length > 0) {
    return `/translations/${abbreviation}`;
  }
  return "";
});

const htmlParts = computed(() => data.value.htmlParts);

const zipUrl = computed(() =>
  getManifestationUrl(metadata.value, "application/zip"),
);

const tableOfContents = computed(() => {
  if (!metadata.value.hasPart) return [];
  const normPath = route.path;
  return tocItemsToTreeViewItems(
    metadata.value.hasPart,
    (id) => ({ path: normPath, hash: `#${id}` }),
    (id) => ({ path: normPath, hash: `#${id}` }),
  );
});

const validityInterval = computed(() =>
  privateFeaturesEnabled
    ? temporalCoverageToValidityInterval(metadata.value.temporalCoverage)
    : undefined,
);

const validityStatus = computed(() => {
  if (metadata.value && validityInterval)
    return getValidityStatus(validityInterval.value);
  return undefined;
});

useNormSeo({
  norm: data.value.legislation,
  validityInterval: validityInterval.value,
  validityStatus: validityStatus.value,
});

const normBreadcrumbTitle = computed(() =>
  getNormBreadcrumbTitle(metadata.value),
);

const { status: normVersionsStatus, sortedVersions: normVersions } =
  useNormVersions(metadata.value.exampleOfWork?.legislationIdentifier ?? "");

const breadcrumbItems: ComputedRef<BreadcrumbItem[]> = computed(() => {
  return [
    {
      label: "Suche",
      route: `/search?documentKind=${DocumentKind.Norm}`,
    },
    {
      route: `/norms/${metadata.value.legislationIdentifier}`,
      label: normBreadcrumbTitle.value,
    },
  ];
});

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

const views = [
  {
    path: "text",
    label: "Text",
    icon: IcBaselineSubject,
    analyticsId: "norm-text-tab",
  },
  {
    path: "details",
    label: "Details",
    icon: IcOutlineInfo,
    analyticsId: "norm-metadata-tab",
  },
  {
    path: "versions",
    label: "Fassungen",
    icon: IcOutlineRestore,
    analyticsId: "norm-versions-tab",
  },
] as const;

const currentView = computed(
  () => route.query.view?.toString() ?? views[0].path,
);

const { dateFilterValue: fassungenDateFilterValue, filteredNormVersions } =
  useNormVersionFilter(normVersions);

const textTabPanelTitleId = useId();
const detailsTabPanelTitleId = useId();
const fassungenTabPanelTitleId = useId();
const fassungenDateFilterInputId = useId();
</script>

<template>
  <NuxtLayout name="breadcrumb-page">
    <template #breadcrumb>
      <div class="flex items-center gap-4 md:gap-16 print:hidden">
        <Breadcrumbs :items="breadcrumbItems" class="grow" />
        <DocumentsActionMenuNormActionMenu
          :metadata
          :translation-url
          class="mb-auto"
        />
      </div>
    </template>
    <template #default>
      <div class="wrapper">
        <DocumentsNormsNormHeadingGroup
          :metadata="metadata"
          :html-parts="htmlParts"
        />
        <DocumentsNormsNormVersionWarning
          v-if="normVersionsStatus === 'success'"
          :versions="normVersions"
          :current-version="metadata"
        />
        <Metadata :items="metadataItems" class="mb-48" />
      </div>

      <div class="border-b border-gray-400">
        <nav class="wrapper -mb-1 overflow-x-auto pt-1" aria-label="Tab">
          <Tabs :value="currentView" :show-navigators="false">
            <TabList>
              <Tab
                v-for="view in views"
                :key="view.path"
                :value="view.path"
                :as="NuxtLink"
                :to="{ query: { view: view.path } }"
                :aria-controls="undefined"
                :data-attr="view.analyticsId"
                class="flex items-center gap-8"
              >
                <component :is="view.icon" />
                {{ view.label }}
              </Tab>
            </TabList>
          </Tabs>
        </nav>
      </div>

      <div id="content" class="min-h-96 bg-white print:py-0">
        <section
          v-if="currentView === 'text'"
          role="tabpanel"
          :aria-labelledby="textTabPanelTitleId"
        >
          <SidebarLayout class="wrapper">
            <template #content>
              <h2 :id="textTabPanelTitleId" class="sr-only">Text</h2>
              <DocumentsIncompleteDataMessage />
              <DocumentsNormsLegislationContent
                :official-toc="htmlParts.officialToc"
              >
                <div v-html="htmlParts.body" />
              </DocumentsNormsLegislationContent>
            </template>

            <template #sidebar v-if="tableOfContents?.length">
              <client-only>
                <DocumentsTableOfContents
                  :subheading="normBreadcrumbTitle"
                  :table-of-contents="tableOfContents"
                />
              </client-only>
            </template>
          </SidebarLayout>
        </section>

        <section
          v-else-if="currentView === 'details'"
          role="tabpanel"
          :aria-labelledby="detailsTabPanelTitleId"
        >
          <div class="wrapper pt-32 pb-32 md:pb-56">
            <h2 :id="detailsTabPanelTitleId" class="typo-headline3-bold">
              Details
            </h2>
            <DocumentsIncompleteDataMessage class="my-24" />
            <DetailsList>
              <DetailsListEntry
                label="Ausfertigungsdatum:"
                :value="
                  dateFormattedDDMMYYYY(metadata.exampleOfWork.legislationDate)
                "
              />
              <DetailsListEntry
                label="Vollzitat:"
                :value="htmlParts.vollzitat"
              />
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
                <template v-if="htmlParts.headingNotes">
                  <div class="footnotes" v-html="htmlParts.headingNotes" />
                </template>
              </DetailsListEntry>
              <DetailsListEntry label="Download:">
                <NuxtLink
                  data-attr="xml-zip-view"
                  class="typo-link-regular"
                  external
                  :to="zipUrl"
                >
                  <IconFileDownload class="mr-2 inline" />
                  {{ metadata.abbreviation ?? "Inhalte" }} als ZIP herunterladen
                </NuxtLink>
              </DetailsListEntry>
            </DetailsList>
          </div>
        </section>

        <section
          v-else-if="currentView === 'versions'"
          role="tabpanel"
          :aria-labelledby="fassungenTabPanelTitleId"
        >
          <div class="wrapper pt-32 pb-32 md:pb-56">
            <template v-if="privateFeaturesEnabled">
              <h2 :id="fassungenTabPanelTitleId" class="typo-headline3-bold">
                Fassungen
              </h2>

              <DocumentsIncompleteDataMessage class="my-24" />
              <div class="my-16 md:my-24">
                <label
                  :for="fassungenDateFilterInputId"
                  class="typo-label2-regular"
                  >Gültig am</label
                >
                <DateInput
                  v-model="fassungenDateFilterValue"
                  class="mb-16 max-w-240 md:mb-24"
                  :id="fassungenDateFilterInputId"
                  :showClear="true"
                />
              </div>
              <DocumentsNormsNormVersionList
                :status="normVersionsStatus"
                :current-legislation-identifier="
                  metadata.legislationIdentifier ?? ''
                "
                :versions="filteredNormVersions"
              />
            </template>

            <div class="max-w-prose" v-else>
              <h2
                :id="fassungenTabPanelTitleId"
                class="typo-headline3-bold mb-24"
              >
                Fassungen sind noch nicht verfügbar
              </h2>
              <p>
                Mit dem Livegang des neuen Rechtsinformationsportals werden auch
                außer Kraft getretene und zukünftig in Kraft tretende Fassungen
                der Gesetze und Verordnungen zur Verfügung gestellt.
              </p>

              <h3 class="typo-headline3-bold mt-48 mb-24">
                Unterstützen Sie uns bei der Entwicklung dieser Funktion
              </h3>

              <p>
                Unser Ziel ist es, Rechtsinformationen für Bürgerinnen und
                Bürger leichter zugänglich zu machen. Deshalb suchen wir
                Menschen, die ihre Erfahrungen mit uns teilen und unseren
                Service testen.
              </p>

              <Button
                :as="NuxtLink"
                class="mt-16"
                :to="{ name: 'usage-tests' }"
              >
                Mehr über Nutzungstest erfahren
              </Button>
            </div>
          </div>
        </section>
      </div>
    </template>
  </NuxtLayout>
</template>

<style lang="css" scoped>
@reference "~/assets/main.css";

:deep(.official-toc div) {
  @apply mb-16 ml-16 lg:pl-32;

  &.level-1 {
    @apply typo-label1-bold ml-0;
  }

  &.level-5 {
    @apply ml-8;
  }

  &.level-10 {
    @apply ml-16;
  }
}

.footnotes :deep(.nichtamtliche-fussnoten) {
  @apply list-none p-0;
}

.footnotes :deep(.nichtamtliche-fussnoten .fussnote) {
  @apply mt-8 first:mt-0;
}

.footnotes :deep(.nichtamtliche-fussnoten .fussnote pre) {
  @apply typo-mono overflow-auto border border-gray-400 p-8;
}

.footnotes :deep(.nichtamtliche-fussnoten .fussnote:first-child pre) {
  @apply -mt-8;
}
</style>
