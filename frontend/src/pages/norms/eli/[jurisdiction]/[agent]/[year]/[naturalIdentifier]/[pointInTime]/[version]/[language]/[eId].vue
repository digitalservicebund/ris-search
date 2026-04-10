<script setup lang="ts">
import { computed } from "vue";
import type { BreadcrumbItem } from "~/components/Breadcrumbs.vue";
import { type Article, DocumentKind } from "~/types/api";
import IcBaselineArrowBack from "~icons/ic/baseline-arrow-back";
import IcBaselineArrowForward from "~icons/ic/baseline-arrow-Forward";

definePageMeta({
  // note: this is an expression ELI plus the article eId
  alias:
    "/eli/:jurisdiction/:agent/:year/:naturalIdentifier/:pointInTime/:version/:language/:eId",
  layout: "norm",
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

const { data, error, status } = await useFetchNormArticleContent(
  expressionEli,
  eId.value,
);

const norm = computed(() => data.value?.legislation);
const articleHtml = computed(() => data.value?.htmlBody);

if (error.value) {
  showError(error.value);
}

const normPath: string = route.fullPath.replace(/\/[^/]*$/, "");

const tableOfContents = computed(() => {
  if (!norm.value?.tableOfContents) return [];
  return tocItemsToTreeViewItems(
    norm.value.tableOfContents,
    (id) => ({ path: normPath, hash: `#${id}` }),
    (id) => ({ path: `${normPath}/${id}` }),
  );
});

const expressionValidityInterval = computed(() =>
  privateFeaturesEnabled
    ? temporalCoverageToValidityInterval(norm?.value?.temporalCoverage)
    : undefined,
);

const article: Ref<Article | undefined> = computed(() =>
  norm.value?.hasPart?.find((part) => part.eId == eId.value),
);

const previousArticleUrl: Ref<string | undefined> = computed(() =>
  getRouteForSiblingArticle(article.value, -1),
);

const nextArticleUrl: Ref<string | undefined> = computed(() =>
  getRouteForSiblingArticle(article.value, 1),
);

function getRouteForSiblingArticle(
  baseArticle: Article | undefined,
  indexDifference: number,
): string | undefined {
  if (!norm.value || !baseArticle || !norm.value.hasPart) return undefined;
  const hasPart = norm.value.hasPart;
  const newIndex =
    hasPart.findIndex((item) => item.eId == baseArticle?.eId) + indexDifference;
  if (newIndex < 0 || newIndex >= hasPart.length) return undefined;
  return route.fullPath.replace(/\/[^/]*$/, `/${hasPart[newIndex]?.eId}`);
}

const currentNodePath = computed(() =>
  findNodePath(tableOfContents.value, eId.value ?? ""),
);
const normBreadcrumbTitle = computed(() => getNormBreadcrumbTitle(norm.value));
const breadcrumbItems: Ref<BreadcrumbItem[]> = computed(() => {
  const validFrom = dateFormattedDDMMYYYY(
    expressionValidityInterval.value?.from,
  );
  const validFromDisplay = validFrom ? ` vom ${validFrom}` : "";

  const list: BreadcrumbItem[] = [
    {
      label: formatDocumentKind(DocumentKind.Norm),
      route: `/search?documentKind=${DocumentKind.Norm}`,
    },
    {
      label: normBreadcrumbTitle.value + validFromDisplay,
      route: normPath,
    },
  ];

  currentNodePath.value?.forEach((node) =>
    list.push({
      label: [node.title, node.subtitle].filter(Boolean).join(" "),
      route: typeof node.to === "string" ? node.to : undefined,
    } as BreadcrumbItem),
  );
  return list;
});

const htmlTitle = computed(() => data.value?.articleHeading);

const validVersions =
  norm.value?.legislationLegalForce === "InForce"
    ? undefined
    : useValidNormVersions(norm.value?.legislationIdentifier);

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

const buildOgTitleForArticle = (
  normAbbreviation?: string,
  articleHeadlineHtml?: string,
): string | undefined => {
  const headlineText = (() => {
    if (!articleHeadlineHtml) return "";
    const doc = parseDocument(articleHeadlineHtml);
    const text = doc.body?.textContent ?? "";
    return text.replaceAll(/\s+/g, " ").trim();
  })();

  const base = normAbbreviation || headlineText;
  if (!base) return undefined;

  const full =
    normAbbreviation && headlineText
      ? `${normAbbreviation}: ${headlineText}`
      : base;

  return truncateAtWord(full, 55) || undefined;
};

const title = computed(() =>
  norm.value
    ? buildOgTitleForArticle(norm.value.abbreviation?.trim(), htmlTitle.value)
    : "",
);

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

const description = computed<string | undefined>(() => {
  if (!articleHtml.value) return undefined;
  const doc = parseDocument(articleHtml.value);
  const firstParagraph = doc.querySelector("p");
  const text = firstParagraph?.textContent?.trim();
  return text ? truncateAtWord(text, 150) : undefined;
});

useDynamicSeo({ title, description });
</script>

<template>
  <div v-if="status == 'pending'" class="container">Lade ...</div>

  <template v-if="!!norm">
    <div class="container">
      <Breadcrumbs :items="breadcrumbItems" />
      <div>
        <h1
          class="ris-heading2-bold my-24 mb-24 inline-block"
          v-html="htmlTitle"
        />
      </div>

      <DocumentsNormsArticleVersionWarning
        v-if="inForceNormLink && article"
        :in-force-version-link="inForceNormLink"
        :current-article="article"
      />
    </div>

    <div
      v-if="privateFeaturesEnabled"
      class="container mb-24 flex flex-col space-y-16 space-x-0 md:space-y-0 lg:flex-row lg:space-x-24"
      data-testid="metadata"
    >
      <Metadata :items="metadataItems" />
    </div>

    <div class="border-t border-t-gray-400 bg-white">
      <SidebarLayout class="container">
        <template v-if="!!articleHtml" #content>
          <DocumentsIncompleteDataMessage />
          <DocumentsNormsLegislationContent single-article>
            <article class="akn-act" v-html="articleHtml" />
          </DocumentsNormsLegislationContent>
          <div class="flex flex-row justify-between">
            <div class="flex flex-col">
              <NuxtLink
                v-if="previousArticleUrl"
                :to="previousArticleUrl"
                class="ris-link1-regular link-hover"
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
                class="ris-link1-regular link-hover"
              >
                <div class="flex items-center space-x-8">
                  <span>Nächster Paragraf</span>
                  <IcBaselineArrowForward class="mt-1 shrink-0" />
                </div>
              </NuxtLink>
            </div>
          </div>
        </template>
        <template #sidebar>
          <DocumentsNormsNormTableOfContents
            v-if="norm.tableOfContents?.length"
            :subheading="normBreadcrumbTitle"
            :subheading-to="normPath"
            :table-of-contents="tableOfContents"
            :selected-key="eId"
          />
        </template>
      </SidebarLayout>
    </div>
  </template>
</template>
