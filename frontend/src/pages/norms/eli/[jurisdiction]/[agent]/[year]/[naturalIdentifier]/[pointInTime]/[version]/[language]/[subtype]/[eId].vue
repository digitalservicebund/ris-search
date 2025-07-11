<script setup lang="ts">
import type { TreeNode } from "primevue/treenode";
import { getNormBreadcrumbTitle, getNormTitle } from "./titles";
import { useFetchNormArticleContent } from "./useNormData";
import ContentWrapper from "~/components/CustomLayouts/ContentWrapper.vue";
import TableOfContentsLayout from "~/components/CustomLayouts/SidebarLayout.vue";
import IncompleteDataMessage from "~/components/IncompleteDataMessage.vue";
import MetadataField from "~/components/MetadataField.vue";
import ArticleVersionWarning from "~/components/Norm/ArticleVersionWarning.vue";
import NormTableOfContents from "~/components/Ris/NormTableOfContents.vue";
import type { BreadcrumbItem } from "~/components/Ris/RisBreadcrumb.vue";
import RisBreadcrumb from "~/components/Ris/RisBreadcrumb.vue";
import { useValidNormVersions } from "~/composables/useNormVersions";
import type { Article, LegislationWork } from "~/types";
import { featureFlags } from "~/utils/config";
import { tocItemsToTreeNodes } from "~/utils/tableOfContents";
import IcBaselineArrowBack from "~icons/ic/baseline-arrow-back";
import IcBaselineArrowForward from "~icons/ic/baseline-arrow-Forward";
import MdiArrowTopLeft from "~icons/mdi/arrow-top-left?width=24&height=24";

definePageMeta({
  // note: this is an expression ELI that additionally specifies the subtype component of a manifestation ELI,
  // plus the article eId
  alias:
    "/eli/:jurisdiction/:agent/:year/:naturalIdentifier/:pointInTime/:version/:language/:subtype/:eId",
  layout: "base", // use "base" layout to allow for full-width text background
});

const route = useRoute();

const expressionEli = Object.values(route.params).slice(0, -1).join("/");
const eId = computed(() => {
  const eIdParam: string = Array.isArray(route.params.eId)
    ? route.params.eId[0]
    : route.params.eId;
  if (!eIdParam) return undefined;
  return eIdParam.endsWith(".html") ? eIdParam.slice(0, -5) : eIdParam;
});

const { data, error, status } = await useFetchNormArticleContent(
  expressionEli,
  eId.value,
);

const norm: Ref<LegislationWork> = computed(() => data.value?.legislationWork);
const articleHtml: Ref<string> = computed(() => data.value?.html);

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
    `/${norm.value.workExample.hasPart[newIndex].eId}`,
  );
}

const currentNodePath = findNodePath(tableOfContents.value, eId.value ?? "");
const normTitle = computed(() => getNormTitle(norm.value));
const normBreadcrumbTitle = computed(() => getNormBreadcrumbTitle(norm.value));
const items: Ref<BreadcrumbItem[]> = computed(() => {
  const list: BreadcrumbItem[] = [];
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
const htmlTitle = computed(() => data.value.articleHeading);
const topNormLinkText = computed(() => {
  if (!norm.value) return "";
  const baseText = norm.value.name || norm.value.alternateName;
  const validityInterval = temporalCoverageToValidityInterval(
    norm.value.workExample.temporalCoverage,
  );
  if (!article.value?.isActive && validityInterval?.from) {
    return `${baseText} vom ${validityInterval.from}`;
  }
  return baseText;
});

const validVersions =
  norm.value.workExample.legislationLegalForce !== "InForce"
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
  return `/norms/${validVersion.item.workExample.legislationIdentifier}`;
});

const showNormArticleStatus = computed(() =>
  featureFlags.showNormArticleStatus(),
);
useHead({ title: article.value?.name });
</script>

<template>
  <ContentWrapper border>
    <div v-if="status == 'pending'" class="container">Lade ...</div>
    <template v-if="!!norm">
      <div class="container">
        <RisBreadcrumb
          type="norm"
          :items="items"
          :title="normBreadcrumbTitle"
          :base-path="normPath"
        />
        <div class="max-w-prose">
          <NuxtLink
            class="ris-heading3-bold link-hover mt-24 line-clamp-2 items-center text-blue-800"
            :to="normPath"
          >
            <MdiArrowTopLeft class="inline-block" />
            {{ topNormLinkText }}
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

      <!-- Metadata -->
      <div
        v-if="!!article && showNormArticleStatus"
        class="container mb-24 flex flex-col space-y-16 space-x-0 md:space-y-0 lg:flex-row lg:space-x-24"
        data-testid="metadata"
      >
        <MetadataField
          v-if="article.entryIntoForceDate"
          id="article_entry_into_force_date"
          label="Gültig von"
          :value="formattedDate(article.entryIntoForceDate)"
        />
        <MetadataField
          v-if="article.expiryDate"
          id="article_expiry_date"
          label="Gültig bis"
          :value="formattedDate(article.expiryDate)"
        />
      </div>
      <div class="bg-white">
        <TableOfContentsLayout class="container py-24">
          <template v-if="!!articleHtml" #content>
            <IncompleteDataMessage />
            <main class="single-article akn-act" v-html="articleHtml" />
            <div class="flex flex-row justify-between">
              <div class="flex flex-col">
                <router-link
                  v-if="previousArticleUrl"
                  :to="previousArticleUrl"
                  class="text-blue-800 hover:underline"
                >
                  <div class="flex items-center space-x-8">
                    <IcBaselineArrowBack class="mt-1 shrink-0" />
                    <span>Vorheriger Paragraf</span>
                  </div>
                </router-link>
              </div>
              <div class="flex flex-col">
                <router-link
                  v-if="nextArticleUrl"
                  :to="nextArticleUrl"
                  class="text-blue-800 hover:underline"
                >
                  <div class="flex items-center space-x-8">
                    <span>Nächster Paragraf</span>
                    <IcBaselineArrowForward class="mt-1 shrink-0" />
                  </div>
                </router-link>
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
                <router-link
                  v-if="normTitle"
                  :to="normPath"
                  class="link-hover font-bold text-blue-800"
                >
                  {{ normTitle }}
                </router-link>
              </template>
            </NormTableOfContents>
          </template>
        </TableOfContentsLayout>
      </div>
    </template>
  </ContentWrapper>
</template>

<style>
@import url("~/assets/legislation.css");

.single-article .akn-num.inline,
.single-article .akn-heading.inline {
  display: none;
}
</style>
