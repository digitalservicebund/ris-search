<script setup lang="ts">
import { NuxtLink } from "#components";
import type { Dayjs } from "dayjs";
import { Tab, TabList, Tabs } from "primevue";
import type { ComputedRef } from "vue";
import type { BreadcrumbItem } from "~/components/Breadcrumbs.vue";
import { DocumentKind, type LegislationWork } from "~/types/api";
import type { ValidityStatus } from "~/utils/norm";
import IcBaselineSubject from "~icons/ic/baseline-subject";
import IconFileDownload from "~icons/ic/outline-file-download";
import IcOutlineInfo from "~icons/ic/outline-info";
import IcOutlineRestore from "~icons/ic/outline-settings-backup-restore";

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

const htmlParts = computed(() => data.value?.htmlParts);

const zipUrl = computed(() =>
  getManifestationUrl(metadata.value, "application/zip"),
);

if (error.value) {
  showError(error.value);
}

const tableOfContents = computed(() => {
  if (!metadata.value?.workExample?.tableOfContents) return [];
  const normPath = route.path;
  return tocItemsToTreeViewItems(
    metadata.value.workExample.tableOfContents,
    (id) => ({ path: normPath, hash: `#${id}` }),
    (id) => ({ path: normPath, hash: `#${id}` }),
  );
});

const validityInterval = computed(() =>
  privateFeaturesEnabled
    ? temporalCoverageToValidityInterval(
        metadata.value?.workExample?.temporalCoverage,
      )
    : undefined,
);

const validityStatus = computed(() => {
  if (metadata.value?.workExample && validityInterval)
    return getValidityStatus(validityInterval.value);
  return undefined;
});

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
      route: `/search?documentKind=${DocumentKind.Norm}`,
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
  normValidityStatus?: ValidityStatus,
) => {
  const shortTitle = norm.alternateName?.trim();
  const baseTitle = norm.abbreviation?.trim() || shortTitle || "";

  if (!baseTitle) return undefined;
  const parts: string[] = [];

  if (privateFeaturesEnabled) {
    const formattedValidFrom = dateFormattedDDMMYYYY(validFrom);
    if (formattedValidFrom) parts.push(`Fassung vom ${formattedValidFrom}`);

    const statusLabel = getValidityStatusLabel(normValidityStatus);
    if (statusLabel) parts.push(statusLabel);

    return truncateAtWord(`${baseTitle}: ${parts.join(", ")}`, 55) || undefined;
  } else {
    if (validFrom) parts.push(`Fassung vom [Inkrafttreten]`);
    if (normValidityStatus) parts.push("[Status]");
  }

  let result = baseTitle;
  if (parts.length) result += `: ${parts.join(", ")}`;
  return truncateAtWord(result, 55) || undefined;
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

useDynamicSeo({ title, description });

const detailsTabPanelTitleId = useId();
const fassungenTabPanelTitleId = useId();
</script>

<template>
  <div v-if="status == 'pending'">Lade ...</div>

  <div v-if="!!metadata">
    <div class="container">
      <div class="flex items-center gap-8 print:hidden">
        <Breadcrumbs :items="breadcrumbItems" class="grow" />
        <DocumentsActionMenuNormActionMenu :metadata :translation-url />
      </div>
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

    <div class="min-h-96 bg-white print:py-0">
      <section v-if="currentView === 'text'">
        <SidebarLayout class="container">
          <template #content>
            <h2 class="sr-only">Text</h2>
            <DocumentsIncompleteDataMessage />
            <DocumentsNormsLegislationContent
              :official-toc="htmlParts.officialToc"
            >
              <div v-html="htmlParts.body" />
            </DocumentsNormsLegislationContent>
          </template>

          <template #sidebar>
            <client-only>
              <DocumentsNormsNormTableOfContents
                v-if="metadata.workExample?.tableOfContents?.length"
                :subheading="normBreadcrumbTitle"
                :table-of-contents="tableOfContents"
              />
            </client-only>
          </template>
        </SidebarLayout>
      </section>

      <section
        v-else-if="currentView === 'details'"
        :aria-labelledby="detailsTabPanelTitleId"
      >
        <div class="container pt-32 pb-32 lg:pb-56">
          <h2 :id="detailsTabPanelTitleId" class="ris-heading3-bold">
            Details
          </h2>
          <DocumentsIncompleteDataMessage class="my-24" />
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
              <template v-if="htmlParts.headingNotes">
                <div class="footnotes" v-html="htmlParts.headingNotes" />
              </template>
            </DetailsListEntry>
            <DetailsListEntry label="Download:">
              <NuxtLink
                data-attr="xml-zip-view"
                class="ris-link1-regular"
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
        :aria-labelledby="detailsTabPanelTitleId"
      >
        <div class="container pt-32 pb-32 lg:pb-56">
          <template v-if="privateFeaturesEnabled">
            <h2 :id="fassungenTabPanelTitleId" class="ris-heading3-bold">
              Fassungen
            </h2>
            <DocumentsIncompleteDataMessage class="my-24" />
            <DocumentsNormsNormVersionList
              v-if="privateFeaturesEnabled"
              :status="normVersionsStatus"
              :current-legislation-identifier="
                metadata.workExample?.legislationIdentifier ?? ''
              "
              :versions="normVersions"
            />
          </template>

          <DocumentsNormsVersionsTeaser v-else />
        </div>
      </section>
    </div>
  </div>
</template>

<style lang="css" scoped>
@reference "~/assets/main.css";

:deep(.official-toc div) {
  @apply mb-16 ml-16 lg:pl-32;

  &.level-1 {
    @apply ris-label1-bold ml-0;
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
  @apply mt-16 first:mt-0;
}
</style>
