<script setup lang="ts">
import type { TreeNode } from "primevue/treenode";
import { computed } from "vue";
import type { BreadcrumbItem } from "~/components/Breadcrumbs.vue";
import IncompleteDataMessage from "~/components/documents/IncompleteDataMessage.vue";
import ArticleVersionWarning from "~/components/documents/norms/ArticleVersionWarning.vue";
import NormTableOfContents from "~/components/documents/norms/NormTableOfContents.vue";
import SidebarLayout from "~/components/SidebarLayout.vue";
import { useDynamicSeo } from "~/composables/useDynamicSeo";
import { useFetchNormArticleContent } from "~/composables/useNormData";
import { useValidNormVersions } from "~/composables/useNormVersions";
import { usePrivateFeaturesFlag } from "~/composables/usePrivateFeaturesFlag";
import { type Article, DocumentKind, type LegislationWork } from "~/types";
import {
  dateFormattedDDMMYYYY,
  parseDateGermanLocalTime,
} from "~/utils/dateFormatting";
import { formatDocumentKind } from "~/utils/displayValues";
import { parseDocument } from "~/utils/htmlParser";
import { getNormBreadcrumbTitle, getNormTitle } from "~/utils/norm";
import { findNodePath, tocItemsToTreeNodes } from "~/utils/tableOfContents";
import { truncateAtWord } from "~/utils/textFormatting";
import IcBaselineArrowBack from "~icons/ic/baseline-arrow-back";
import IcBaselineArrowForward from "~icons/ic/baseline-arrow-Forward";
import MdiArrowTopLeft from "~icons/mdi/arrow-top-left?width=24&height=24";

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

const norm = computed(() => data.value?.legislationWork);
const articleHtml = computed(() => data.value?.html);

if (error.value) {
  showError(error.value);
}

const normPath: string = route.fullPath.replace(/\/[^/]*$/, "");

const tableOfContents: Ref<TreeNode[]> = computed(() => {
  if (!norm.value?.workExample.tableOfContents) return [];
  return tocItemsToTreeNodes(
    norm.value.workExample.tableOfContents,
    normPath.concat("#"),
    normPath.concat("/"),
  );
});

const article: Ref<Article | undefined> = computed(() =>
  norm.value?.workExample.hasPart.find((part) => part.eId == eId.value),
);

const previousArticleUrl: Ref<string | undefined> = computed(() =>
  getRouteForSiblingArticle(article.value, -1),
);

const nextArticleUrl: Ref<string | undefined> = computed(() =>
  getRouteForSiblingArticle(article.value, 1),
);

function getRouteForSiblingArticle(
  article: Article | undefined,
  indexDifference: number,
): string | undefined {
  if (!norm.value || !article) return undefined;
  const newIndex =
    norm.value.workExample.hasPart.findIndex(
      (item) => item.eId == article?.eId,
    ) + indexDifference;
  if (newIndex < 0 || newIndex >= norm.value.workExample.hasPart.length)
    return undefined;
  return route.fullPath.replace(
    /\/[^/]*$/,
    `/${norm.value.workExample.hasPart[newIndex]?.eId}`,
  );
}

const currentNodePath = findNodePath(tableOfContents.value, eId.value ?? "");
const normTitle = computed(() => getNormTitle(norm.value));
const normBreadcrumbTitle = computed(() => getNormBreadcrumbTitle(norm.value));
const breadcrumbItems: Ref<BreadcrumbItem[]> = computed(() => {
  const list: BreadcrumbItem[] = [
    {
      label: formatDocumentKind(DocumentKind.Norm),
      route: `/search?category=${DocumentKind.Norm}`,
    },
    {
      label: normBreadcrumbTitle.value,
      route: normPath,
    },
  ];

  if (article.value && !article.value?.isActive) {
    list.push({
      label: [article.value.entryIntoForceDate, article.value.expiryDate].join(
        "–",
      ),
      route: route.fullPath,
    } as BreadcrumbItem);
  }
  currentNodePath?.forEach((node) =>
    list.push({
      label: node.label,
      route: node.route,
    } as BreadcrumbItem),
  );
  return list;
});

const htmlTitle = computed(() => data.value?.articleHeading);
const topNormLinkText = computed(() => {
  if (norm.value) return norm.value.name || norm.value.alternateName;
  else return "";
});

const validVersions =
  norm.value?.workExample.legislationLegalForce !== "InForce"
    ? useValidNormVersions(norm.value?.legislationIdentifier)
    : undefined;

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
  return `/norms/${validVersion?.item.workExample.legislationIdentifier}`;
});

const buildOgTitleForArticle = (
  norm: LegislationWork,
  articleHeadlineHtml?: string,
): string | undefined => {
  const abbreviation = norm.abbreviation?.trim();
  const headlineText = (() => {
    if (!articleHeadlineHtml) return "";
    const doc = parseDocument(articleHeadlineHtml);
    const text = doc.body?.textContent ?? "";
    return text.replaceAll(/\s+/g, " ").trim();
  })();

  const base = abbreviation || headlineText;
  if (!base) return undefined;

  const full =
    abbreviation && headlineText ? `${abbreviation}: ${headlineText}` : base;

  return truncateAtWord(full, 55) || undefined;
};

const title = computed(() =>
  norm.value ? buildOgTitleForArticle(norm.value, htmlTitle.value) : "",
);

const metadataItems = computed(() => {
  return [
    {
      label: "Gültig ab",
      value: dateFormattedDDMMYYYY(
        parseDateGermanLocalTime(article.value?.entryIntoForceDate),
      ),
    },
    {
      label: "Gültig bis",
      value: dateFormattedDDMMYYYY(
        parseDateGermanLocalTime(article.value?.expiryDate),
      ),
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
        <NuxtLink
          :to="normPath"
          class="ris-heading3-bold! ris-link1-regular link-hover mt-24 line-clamp-2"
        >
          <h1 class="flex items-center gap-4">
            <MdiArrowTopLeft class="mt-4 flex-none self-start" />
            <span class="line-clamp-2">
              {{ topNormLinkText }}
            </span>
          </h1>
        </NuxtLink>
        <h2
          class="ris-heading2-bold my-24 mb-24 inline-block"
          v-html="htmlTitle"
        />
      </div>

      <ArticleVersionWarning
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
    <div class="bg-white">
      <SidebarLayout class="container py-24">
        <template v-if="!!articleHtml" #content>
          <IncompleteDataMessage />
          <article
            class="legislation single-article akn-act"
            v-html="articleHtml"
          />
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
          <NormTableOfContents
            v-if="norm.workExample.tableOfContents.length > 0"
            :table-of-contents="tableOfContents"
            :selected-key="eId"
          >
            <template #header>
              <NuxtLink
                v-if="normTitle"
                :to="normPath"
                class="link-hover font-bold text-blue-800"
              >
                {{ normTitle }}
              </NuxtLink>
            </template>
          </NormTableOfContents>
        </template>
      </SidebarLayout>
    </div>
  </template>
</template>

<style>
@import url("~/assets/legislation.css");

.single-article .akn-num.inline,
.single-article .akn-heading.inline {
  display: none;
}
</style>
