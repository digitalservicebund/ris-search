<script setup lang="ts">
import { Message } from "primevue";
import { computed } from "vue";
import IcBaselineSubject from "~icons/ic/baseline-subject";
import IcOutlineInfo from "~icons/ic/outline-info";
import IcOutlineWarning from "~icons/ic/outline-warning-amber";
import { NuxtLink } from "#components";
import DetailsList from "~/components/DetailsList.vue";
import DetailsListEntry from "~/components/DetailsListEntry.vue";
import NormTranslationActionMenu from "~/components/documents/actionMenu/NormTranslationActionMenu.vue";
import type { TabView } from "~/components/TabsLayout.vue";
import {
  fetchTranslationAndHTML,
  getGermanOriginal,
} from "~/composables/useTranslationData";
import { useTranslationSeo } from "~/composables/useTranslationSeo";
import { removePrefix } from "~/utils/textFormatting";

definePageMeta({
  layout: false,
  skipLinks: [
    { label: "Skip to main", to: "#main" },
    { label: "Skip to footer", to: "#footer" },
  ],
});

useHead({
  htmlAttrs: { lang: "en" },
});

const route = useRoute();
const id = route.params.id as string;

const { data, error } = await fetchTranslationAndHTML(id);
if (error.value || !data.value) {
  showError({ status: error.value?.status ?? 500 });
}

const { legislation } = await getGermanOriginal(id);

const currentTranslation = data.value.content;
const html = data.value.htmlBody;

useTranslationSeo({
  name: currentTranslation.name,
  translationOfWork: currentTranslation.translationOfWork,
});

const versionInformation = computed(() => {
  return removePrefix(currentTranslation.about, "Version information:");
});

const translatedBy = computed(() => {
  return removePrefix(currentTranslation.translator, "Translation provided by");
});

const germanOriginalWorkEli = computed(() => {
  return legislation.value?.item?.legislationIdentifier;
});

const breadcrumbItems = computed(() => {
  return [
    {
      label: "English translations",
      route: "/translations",
    },
    {
      label: currentTranslation["@id"],
      route: "/translations",
    },
  ];
});

const views: OneOrMore<TabView> = [
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

const textSectionId = useId();
const detailsTabPanelTitleId = useId();
</script>

<template>
  <NuxtLayout name="breadcrumb-page">
    <template #breadcrumb>
      <div class="flex items-center gap-8 print:hidden">
        <Breadcrumbs :items="breadcrumbItems" class="grow" />
        <NormTranslationActionMenu class="mb-auto" />
      </div>
    </template>
    <template #default>
      <div class="wrapper">
        <hgroup class="dokumentenkopf">
          <p
            v-if="currentTranslation?.translationOfWork"
            class="word-wrap typo-headline3-regular mb-8 hyphens-auto"
          >
            {{ currentTranslation.translationOfWork }}
          </p>

          <h1
            v-if="currentTranslation?.name"
            class="typo-headline1-bold my-8 wrap-break-word hyphens-auto sm:mb-16 md:mb-40"
          >
            {{ currentTranslation.name }}
          </h1>
        </hgroup>

        <Message
          v-if="legislation"
          :closable="false"
          class="my-24 max-w-prose space-y-24 sm:my-32 md:my-40"
        >
          <template #icon>
            <IcOutlineWarning />
          </template>
          <p class="typo-label2-bold mt-2">Version Information</p>
          <p class="typo-label2-regular mt-2">
            Translations may not be updated at the same time as the German legal
            provision.
            <NuxtLink
              class="ris-link2-regular 2xl:ris-link1-regular"
              :to="`/norms/${germanOriginalWorkEli}`"
              >Go to the German version</NuxtLink
            >.
          </p>
        </Message>
      </div>

      <TabsLayout :views>
        <template #text>
          <section
            class="pt-32 pb-32 md:pb-56"
            role="tabpanel"
            :aria-labelledby="textSectionId"
          >
            <h2 :id="textSectionId" class="sr-only">Text</h2>
            <section class="max-w-prose" v-html="html" />
          </section>
        </template>

        <template #details>
          <section
            class="pt-32 pb-32 md:pb-56"
            :aria-labelledby="detailsTabPanelTitleId"
          >
            <h2 :id="detailsTabPanelTitleId" class="typo-headline3-bold">
              Details
            </h2>
            <DetailsList class="mt-24">
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
        </template>
      </TabsLayout>
    </template>
  </NuxtLayout>
</template>
