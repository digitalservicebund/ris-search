<script setup lang="ts">
import { Tab, TabList, Tabs } from "primevue";
import Message from "primevue/message";
import { computed } from "vue";
import { NuxtLink } from "#components";
import type { BreadcrumbItem } from "~/components/Breadcrumbs.vue";
import DetailsList from "~/components/DetailsList.vue";
import DetailsListEntry from "~/components/DetailsListEntry.vue";
import NormTranslationActionMenu from "~/components/documents/actionMenu/NormTranslationActionMenu.vue";
import { useDynamicSeo } from "~/composables/useDynamicSeo";
import {
  fetchTranslationAndHTML,
  getGermanOriginal,
} from "~/composables/useTranslationData";
import { DocumentKind } from "~/types";
import { formatDocumentKind } from "~/utils/displayValues";
import { removePrefix, truncateAtWord } from "~/utils/textFormatting";
import IcBaselineSubject from "~icons/ic/baseline-subject";
import IcOutlineInfo from "~icons/ic/outline-info";
import IcOutlineWarning from "~icons/ic/outline-warning-amber";

definePageMeta({ layout: "norm" });

useHead({
  htmlAttrs: { lang: "en" },
});

const route = useRoute();
const id = route.params.id as string;

const { data, error } = await fetchTranslationAndHTML(id);
if (error.value) showError(error.value);

const { legislation } = await getGermanOriginal(id);

const currentTranslation = data.value?.content;
const html = data.value?.html;

const versionInformation = computed(() => {
  return removePrefix(currentTranslation?.about, "Version information:");
});

const translatedBy = computed(() => {
  return removePrefix(
    currentTranslation?.translator,
    "Translation provided by",
  );
});

const germanOriginalWorkEli = computed(() => {
  return legislation.value?.item?.legislationIdentifier;
});

const breadcrumbItems = computed(() => {
  const items: BreadcrumbItem[] = [
    {
      label: formatDocumentKind(DocumentKind.Norm),
      route: `/search?category=${DocumentKind.Norm}`,
    },
    {
      label: "Translations",
      route: "/translations",
    },
  ];
  if (currentTranslation) {
    items.push({
      label: currentTranslation["@id"],
      route: "/translations",
    });
  }
  return items;
});

const buildOgForTranslation = (
  name: string,
  translationOfWork: string,
): { title: string; description: string } => {
  const base = name.trim() || translationOfWork.trim();

  const title = truncateAtWord(`${base} – English Translation`, 55);

  const description = truncateAtWord(
    `This is the English translation of the ${base}, provided by the German Federal Legal Information Portal. This translation is for informational purposes only. The German version is the only legally binding text.`,
    150,
  );

  return { title, description };
};

const translationSeo = computed(() => {
  if (!currentTranslation?.name && !currentTranslation?.translationOfWork) {
    return { title: "", description: "" };
  }

  return buildOgForTranslation(
    currentTranslation.name || "",
    currentTranslation.translationOfWork || "",
  );
});

const title = computed(() => translationSeo.value.title);
const description = computed(() => translationSeo.value.description);
useDynamicSeo({ title, description });

const views = [
  {
    path: "text",
    label: "Text",
    icon: IcBaselineSubject,
    analyticsId: "translation-text-tab",
  },
  {
    path: "details",
    label: "Details",
    icon: IcOutlineInfo,
    analyticsId: "translation-metadata-tab",
  },
] as const;

const currentView = computed(
  () => route.query.view?.toString() ?? views[0].path,
);

const detailsTabPanelTitleId = useId();
</script>

<template>
  <div v-if="currentTranslation" class="container">
    <div class="flex items-center gap-8 print:hidden">
      <Breadcrumbs :items="breadcrumbItems" class="grow" />
      <NormTranslationActionMenu />
    </div>

    <hgroup class="dokumentenkopf mt-24 mb-48">
      <p
        v-if="currentTranslation?.translationOfWork"
        class="word-wrap ris-heading3-regular mb-12 hyphens-auto"
      >
        {{ currentTranslation.translationOfWork }}
      </p>

      <h1
        v-if="currentTranslation?.name"
        class="ris-heading2-bold mb-48 wrap-break-word hyphens-auto"
      >
        {{ currentTranslation.name }}
      </h1>
    </hgroup>

    <Message
      v-if="legislation"
      :closable="false"
      class="mb-48 max-w-prose space-y-24"
    >
      <template #icon>
        <IcOutlineWarning />
      </template>
      <p class="ris-body2-bold mt-2">Version Information</p>
      <p class="mt-2">
        Translations may not be updated at the same time as the German legal
        provision.
        <NuxtLink
          class="ris-link1-regular"
          :to="`/norms/${germanOriginalWorkEli}`"
          >Go to the German version</NuxtLink
        >.
      </p>
    </Message>
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

  <div class="min-h-96 bg-white py-24 print:py-0">
    <div class="container">
      <section v-if="currentView === 'text'">
        <h2 class="sr-only">Text</h2>
        <section class="max-w-prose" v-html="html" />
      </section>

      <section
        v-else-if="currentView === 'details'"
        :aria-labelledby="detailsTabPanelTitleId"
      >
        <h2 :id="detailsTabPanelTitleId" class="ris-heading3-bold my-24">
          Details
        </h2>
        <DetailsList>
          <DetailsListEntry
            label="Translation provided by:"
            :value="translatedBy"
          />
          <DetailsListEntry
            label="Version information:"
            :value="versionInformation"
          />
        </DetailsList>
      </section>
    </div>
  </div>
</template>
