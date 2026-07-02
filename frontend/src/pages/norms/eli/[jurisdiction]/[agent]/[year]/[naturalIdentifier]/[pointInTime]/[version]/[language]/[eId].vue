<script setup lang="ts">
import IcBaselineArrowBack from "~icons/ic/baseline-arrow-back";
import IcBaselineArrowForward from "~icons/ic/baseline-arrow-Forward";
import type { RouteLocationRaw } from "#vue-router";
import type { BreadcrumbItem } from "~/components/Breadcrumbs.vue";
import { useArticleSeo } from "~/composables/useArticleSeo";
import {
  type Article,
  DocumentKind,
  type LegislationExpressionPartSchema,
} from "~/types/api";

definePageMeta({
  // expression ELI + article eId
  alias:
    "/eli/:jurisdiction/:agent/:year/:naturalIdentifier/:pointInTime/:version/:language/:eId",
  layout: false,
  skipLinks: [
    { label: "Zum Gesetzestext", to: "#content" },
    { label: "Zum Fußbereich", to: "#footer" },
  ],
});

const route = useRoute();

const privateFeaturesEnabled = usePrivateFeaturesFlag();

const expressionEli = Object.values(route.params).slice(0, -1).join("/");
const eId = computed(() => {
  const eIdParam = Array.isArray(route.params.eId)
    ? route.params.eId[0]
    : route.params.eId;
  if (!eIdParam) return undefined;
  return eIdParam.endsWith(".html") ? eIdParam.slice(0, -5) : eIdParam;
});

const { data, error } = await useFetchNormArticleContent(
  expressionEli,
  eId.value,
);

if (error.value || !data.value) {
  throw createError({ status: error.value?.status ?? 500 });
}

const norm = computed(() => data.value.legislation);

const normTitle = computed(() => getNormBreadcrumbTitle(norm.value));

const articleHtml = computed(() => data.value.htmlBody);

const normPath: string = route.fullPath.replace(/\/[^/]*$/, "");

const tableOfContents = computed(() => {
  if (!norm.value?.hasPart) return [];
  return tocItemsToTreeViewItems(
    norm.value.hasPart,
    (id) => ({ path: normPath, hash: `#${id}` }),
    (id) => ({ path: `${normPath}/${id}` }),
  );
});

const singleViewParts = computed(() =>
  getPartsLeafNodes(norm.value?.hasPart ?? []),
);

const article: Ref<Article | undefined> = computed(() =>
  // The eId is taken from the router, which always automatically decodes URIs.
  // However some eIds are pre-encoded in the XML data (e.g. "art-z§§ 1 bis 3"
  // is encoded in the XML but will automatically be decoded by the router).
  // For this reason, we need to also decode the eId in the data to make them
  // comparable.
  singleViewParts.value?.find(
    (part) => decodeURIComponent(part.eId) == eId.value,
  ),
);

useArticleSeo({
  abbreviation: norm.value?.abbreviation,
  article: article.value,
  articleHeadlineHtml: data.value.articleHeading,
  articleHtml: data.value.htmlBody,
});

const previousArticleUrl: Ref<RouteLocationRaw | undefined> = computed(() =>
  getRouteForSiblingArticle(article.value, -1),
);

const nextArticleUrl: Ref<RouteLocationRaw | undefined> = computed(() =>
  getRouteForSiblingArticle(article.value, 1),
);

// Get all leaf nodes from the hasPart Tree.
function getPartsLeafNodes(
  parts: LegislationExpressionPartSchema[],
): LegislationExpressionPartSchema[] {
  const leaves: LegislationExpressionPartSchema[] = [];

  for (const part of parts) {
    if (!part.hasPart || part.hasPart.length === 0) {
      leaves.push(part);
    } else {
      leaves.push(...getPartsLeafNodes(part.hasPart));
    }
  }

  return leaves;
}

function getRouteForSiblingArticle(
  self: Article | undefined,
  offset: number,
): RouteLocationRaw | undefined {
  if (!norm.value || !self || !norm.value.hasPart) return undefined;

  const hasPart = singleViewParts.value;
  const newIndex = hasPart.findIndex((item) => item.eId === self?.eId) + offset;

  if (!hasPart[newIndex]) return undefined;

  return {
    name: route.name ?? undefined,
    query: route.query,
    // The router will automatically encode things, but some eId might already
    // be encoded in the XML data (e.g. "art-z§§ 1 bis 3"). Decode first and
    // let the router encode it again to prevent double encoding, which will
    // lead to 404s.
    params: { ...route.params, eId: decodeURIComponent(hasPart[newIndex].eId) },
  };
}

const NORM_INFO_MAX_LENGTH = 23;

const breadcrumbItems: Ref<BreadcrumbItem[]> = computed(() => {
  const currentNodePath = eId.value
    ? (findNodePath(tableOfContents.value, eId.value) ?? [])
    : [];

  const title = truncateAtWord(normTitle.value, NORM_INFO_MAX_LENGTH, true);

  const list: BreadcrumbItem[] = [
    {
      label: "Suche",
      route: `/search?documentKind=${DocumentKind.Norm}`,
    },
    {
      label: title,
      route: normPath,
      extendedLabel: normTitle.value,
    },
  ];

  currentNodePath.forEach((node) => {
    list.push({ label: node.title || node.subtitle || "", route: node.to });
  });

  return list;
});

const htmlTitle = computed(() => data.value.articleHeading);

const validVersions =
  norm.value?.legislationLegalForce === "InForce"
    ? undefined
    : useValidNormVersions(norm.value?.exampleOfWork.legislationIdentifier);

const inForceNormLink = computed(() => {
  if (
    !validVersions ||
    validVersions.status.value !== "success" ||
    !validVersions.data.value?.member ||
    validVersions.data.value.member.length === 0
  ) {
    return undefined;
  }

  const validVersion = validVersions.data.value.member[0];
  return `/norms/${validVersion?.item.legislationIdentifier}`;
});

const metadataItems = computed(() => {
  const interval = temporalCoverageToValidityInterval(
    article.value?.temporalCoverage,
  );
  return [
    {
      label: "Gültig ab",
      value: dateFormattedDDMMYYYY(interval?.from),
    },
    {
      label: "Gültig bis",
      value: dateFormattedDDMMYYYY(interval?.to),
    },
  ];
});
</script>

<template>
  <NuxtLayout name="breadcrumb-page">
    <template #breadcrumb>
      <div>
        <div class="md:hidden">
          <Breadcrumbs :items="breadcrumbItems" collapse />
        </div>
        <div class="hidden md:block">
          <Breadcrumbs :items="breadcrumbItems" />
        </div>
      </div>
    </template>

    <div
      class="content-wrapper mb-24 space-y-24 sm:mb-32 sm:space-y-32 md:mb-40 md:space-y-40"
    >
      <p class="typo-headline3-regular mb-8">
        {{ normTitle }}
      </p>
      <h1
        class="typo-headline1-bold wrap-break-word hyphens-auto"
        v-html="htmlTitle"
      />

      <DocumentsNormsArticleVersionWarning
        v-if="inForceNormLink && article"
        :in-force-version-link="inForceNormLink"
        :current-article="article"
      />

      <DocumentsMetadata v-if="privateFeaturesEnabled" :items="metadataItems" />
    </div>

    <div id="content" class="border-t border-t-gray-400 bg-white">
      <SidebarLayout class="content-wrapper">
        <template v-if="!!articleHtml">
          <DocumentsIncompleteDataMessage />
          <DocumentsNormsLegislationContent single-article>
            <div class="akn-act" v-html="articleHtml" />
          </DocumentsNormsLegislationContent>

          <nav class="flex flex-row justify-between" aria-label="Paragrafen">
            <div class="flex flex-col">
              <NuxtLink
                v-if="previousArticleUrl"
                :to="previousArticleUrl"
                class="typo-link-regular link-hover"
              >
                <div class="flex items-center space-x-8">
                  <IcBaselineArrowBack class="mt-1 shrink-0" />
                  <span>Vorheriger Paragraf</span>
                </div>
              </NuxtLink>
            </div>

            <div class="flex flex-col">
              <NuxtLink
                v-if="nextArticleUrl"
                :to="nextArticleUrl"
                class="typo-link-regular link-hover"
              >
                <div class="flex items-center space-x-8">
                  <span>Nächster Paragraf</span>
                  <IcBaselineArrowForward class="mt-1 shrink-0" />
                </div>
              </NuxtLink>
            </div>
          </nav>
        </template>

        <template #sidebar>
          <DocumentsTableOfContents
            v-if="tableOfContents.length"
            :subheading="normTitle"
            :subheading-to="normPath"
            :table-of-contents="tableOfContents"
            :selected-key="eId"
          />
        </template>
      </SidebarLayout>
    </div>
  </NuxtLayout>
</template>
