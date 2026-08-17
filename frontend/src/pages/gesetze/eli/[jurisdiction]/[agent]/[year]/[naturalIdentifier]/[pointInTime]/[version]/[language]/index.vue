<script setup lang="ts">
import IcBaselineSubject from "~icons/ic/baseline-subject";
import IcOutlineInfo from "~icons/ic/outline-info";
import IcOutlineRestore from "~icons/ic/outline-settings-backup-restore";
import { NuxtLink } from "#components";
import type { BreadcrumbItem } from "~/components/Breadcrumbs.vue";
import type { DetailsListItem } from "~/components/documents/DetailsList.vue";
import type { MetadataItem } from "~/components/documents/Metadata.vue";
import type { TabView } from "~/components/documents/TabsLayout.vue";
import { useSearchBackLink } from "~/composables/useSearchBackLink";
import { DocumentKind, type LegislationExpression } from "~/types/api";

definePageMeta({
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
const url = useRequestURL();
const expressionEli = Object.values(route.params).join("/");
const privateFeaturesEnabled = usePrivateFeaturesFlag();

const { data, error } = await useFetchNormContent(expressionEli, {
  rewriteLink: (link) => {
    if (!link) return null;
    if (link.startsWith("#")) return link;

    const from = searchParamToString(route.query.from);

    // Einzelnorm cross-links are emitted by the backend relative to the API URL
    // structure (`{subtype}/{eId}.html`). Rewrite them to the portal single
    // article route, which only carries the bare eId.
    const eId = getEinzelnormEIdFromHref(link);
    if (eId !== null) {
      const target = new URL(`${route.path}/${eId}`, url);
      if (from) target.searchParams.set("from", from);
      return target.pathname + target.search + target.hash;
    }

    const newLink = new URL(link, url);
    const isSameOrigin = newLink.origin === url.origin;
    if (!isSameOrigin) return link;

    if (from) newLink.searchParams.set("from", from);

    return newLink.pathname + newLink.search + newLink.hash;
  },
});

if (error.value || !data.value) {
  throw createError({ status: error.value?.status ?? 500 });
}

const metadata: Ref<LegislationExpression> = computed(() => {
  return data.value.legislation;
});

const abbreviation = data.value.legislation.abbreviation;

/**
 * Resolves the link to the English translation of this norm, if one exists.
 * Returns the finished URL rather than the request state, since it can no
 * longer change once the page has loaded.
 */
async function fetchTranslationUrl() {
  if (!abbreviation) return "";
  const { translations } = await fetchTranslationListWithIdFilter(abbreviation);
  return translations.value?.length ? `/translations/${abbreviation}` : "";
}

const [
  translationUrl,
  { sortedVersions: normVersions, error: normVersionsError },
] = await Promise.all([
  fetchTranslationUrl(),
  useNormVersions(data.value.legislation.exampleOfWork.legislationIdentifier),
]);

if (normVersionsError.value) {
  throw createError(normVersionsError.value);
}

const htmlParts = computed(() => data.value.htmlParts);

const zipUrl = computed(() =>
  getManifestationUrl(metadata.value, "application/zip"),
);

const detailItems = computed<DetailsListItem[]>(() => [
  {
    type: "text",
    label: "Ausfertigungsdatum:",
    value: dateFormattedDDMMYYYY(metadata.value.exampleOfWork.legislationDate),
  },
  {
    type: "text",
    label: "Vollzitat:",
    value: htmlParts.value.vollzitat,
  },
  {
    type: "list",
    label: "Stand:",
    values: htmlParts.value.standangaben ?? [],
  },
  {
    type: "list",
    label: "Hinweis zum Stand:",
    values: htmlParts.value.standangabenHinweis ?? [],
  },
  {
    type: "html",
    label: "Besonderer Hinweis:",
    html: htmlParts.value.prefaceContainer,
  },
  {
    type: "html",
    label: "Fußnoten:",
    html: htmlParts.value.headingNotes,
    htmlClass: "footnotes",
  },
  {
    type: "link",
    label: "Download:",
    url: zipUrl.value,
    text: `${metadata.value.abbreviation ?? "Inhalte"} als ZIP herunterladen`,
    dataAttr: "xml-zip-view",
  },
]);

const tableOfContents = computed(() => {
  if (!metadata.value.hasPart) return [];
  const normPath = route.path;

  return tocItemsToTreeViewItems(
    metadata.value.hasPart,
    (id) => ({
      path: normPath,
      hash: `#${id}`,
      query: { from: route.query.from },
    }),
    (id) => ({
      path: normPath,
      hash: `#${id}`,
      query: { from: route.query.from },
    }),
  );
});

const validityInterval = computed(() =>
  privateFeaturesEnabled
    ? temporalCoverageToValidityInterval(metadata.value.temporalCoverage)
    : undefined,
);

const validityStatus = computed(() =>
  metadata.value && validityInterval
    ? getValidityStatus(validityInterval.value)
    : undefined,
);

useNormSeo({
  norm: data.value.legislation,
  validityInterval: validityInterval.value,
  validityStatus: validityStatus.value,
});

const normBreadcrumbTitle = computed(() =>
  getNormBreadcrumbTitle(metadata.value),
);

const searchBackLink = useSearchBackLink(DocumentKind.Norm);

const breadcrumbItems: ComputedRef<BreadcrumbItem[]> = computed(() => {
  return [
    {
      label: searchBackLink.value.label,
      route: searchBackLink.value.route,
    },
    {
      route: `/gesetze/${metadata.value.legislationIdentifier}`,
      label: normBreadcrumbTitle.value,
    },
  ];
});

const metadataItems = computed<MetadataItem[]>(() => {
  if (privateFeaturesEnabled) {
    return getNormMetadataItems(metadata.value);
  }
  return [
    {
      type: "text",
      label: "Abkürzung",
      value: abbreviation ?? "",
    },
    {
      type: "text",
      label: "Status",
    },
  ];
});

const views = computed<OneOrMore<TabView>>(() => {
  const baseViews: OneOrMore<TabView> = [
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

  const textTab = {
    path: "text",
    label: "Text",
    icon: IcBaselineSubject,
    analyticsId: "norm-text-tab",
  } as const;

  return data.value.hasEmptyBody ? baseViews : [textTab, ...baseViews];
});

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

    <div
      class="content-wrapper mb-24 space-y-24 sm:mb-32 sm:space-y-32 md:mb-40 md:space-y-40"
    >
      <DocumentsNormsHeadingGroup
        :metadata="metadata"
        :html-parts="htmlParts"
      />

      <DocumentsNormsVersionWarning
        :versions="normVersions"
        :current-version="metadata"
      />

      <DocumentsMetadata :items="metadataItems" />
    </div>

    <DocumentsTabsLayout :views>
      <template #text>
        <section role="tabpanel" :aria-labelledby="textTabPanelTitleId">
          <SidebarLayout>
            <h2 :id="textTabPanelTitleId" class="sr-only">Text</h2>
            <DocumentsIncompleteDataMessage />
            <DocumentsNormsLegislationContent
              :official-toc="htmlParts.officialToc"
            >
              <div v-html="htmlParts.body" />
            </DocumentsNormsLegislationContent>

            <template #sidebar v-if="tableOfContents.length">
              <client-only>
                <DocumentsTableOfContents
                  :subheading="normBreadcrumbTitle"
                  :table-of-contents="tableOfContents"
                />
              </client-only>
            </template>
          </SidebarLayout>
        </section>
      </template>

      <template #details>
        <section role="tabpanel" :aria-labelledby="detailsTabPanelTitleId">
          <div class="pt-32 pb-32 md:pb-56">
            <h2 :id="detailsTabPanelTitleId" class="typo-headline3-bold">
              Details
            </h2>

            <DocumentsIncompleteDataMessage class="my-24" />

            <DocumentsDetailsList :items="detailItems" />
          </div>
        </section>
      </template>

      <template #versions>
        <section role="tabpanel" :aria-labelledby="fassungenTabPanelTitleId">
          <div class="pt-32 pb-32 md:pb-56">
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
                <UiDateInput
                  v-model="fassungenDateFilterValue"
                  class="mb-16 max-w-240 md:mb-24"
                  :id="fassungenDateFilterInputId"
                />
              </div>
              <DocumentsNormsVersionList
                :current-legislation-identifier="metadata.legislationIdentifier"
                :versions="filteredNormVersions"
              />
            </template>

            <div class="content-grid" v-else>
              <div class="content-grid-textblock">
                <h2
                  :id="fassungenTabPanelTitleId"
                  class="typo-headline3-bold mb-24"
                >
                  Fassungen sind noch nicht verfügbar
                </h2>
                <p>
                  Mit dem Livegang des neuen Rechtsinformationsportals werden
                  auch außer Kraft getretene und zukünftig in Kraft tretende
                  Fassungen der Gesetze und Verordnungen zur Verfügung gestellt.
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

                <UiButton
                  :as="NuxtLink"
                  class="mt-16"
                  :to="{ name: 'nutzungstests' }"
                >
                  Mehr über Nutzungstest erfahren
                </UiButton>
              </div>
            </div>
          </div>
        </section>
      </template>
    </DocumentsTabsLayout>
  </NuxtLayout>
</template>

<style scoped>
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

:deep(.footnotes .nichtamtliche-fussnoten) {
  @apply list-none p-0;
}

:deep(.footnotes .nichtamtliche-fussnoten .fussnote) {
  @apply mt-8 first:mt-0;
}

:deep(.footnotes .nichtamtliche-fussnoten .fussnote pre) {
  @apply typo-mono overflow-auto border border-gray-400 p-8;
}

:deep(.footnotes .nichtamtliche-fussnoten .fussnote:first-child pre) {
  @apply md:-mt-8;
}
</style>
