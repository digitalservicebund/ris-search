<script setup lang="ts">
import Message from "primevue/message";
import { computed } from "vue";
import ActionsMenu from "~/components/ActionMenu/ActionsMenu.vue";
import DetailsList from "~/components/DetailsList.vue";
import DetailsListEntry from "~/components/DetailsListEntry.vue";
import type { BreadcrumbItem } from "~/components/Ris/RisBreadcrumb.vue";
import RisTabs from "~/components/Ris/RisTabs.vue";
import { useDynamicSeo } from "~/composables/useDynamicSeo";
import {
  fetchTranslationAndHTML,
  getGermanOriginal,
} from "~/composables/useTranslationData";
import { DocumentKind } from "~/types";
import { formatDocumentKind } from "~/utils/displayValues";
import { tabPanelClass } from "~/utils/tabsStyles";
import { removePrefix, truncateAtWord } from "~/utils/textFormatting";
import IcBaselineSubject from "~icons/ic/baseline-subject";
import IcOutlineInfo from "~icons/ic/outline-info";
import IcOutlineWarning from "~icons/material-symbols/warning-outline";

definePageMeta({ layout: "document" });

useHead({
  htmlAttrs: { lang: "en" },
});

const route = useRoute();
const id = route.params.id as string;

const { data } = await fetchTranslationAndHTML(id);
const { data: germanOriginal } = await getGermanOriginal(id);

const currentTranslation = data.value?.content;
const html = data.value?.html;

const permalink = {
  url: globalThis?.location?.href,
  label: "Link to translation",
};

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
  return germanOriginal.value?.item?.legislationIdentifier;
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

const tabs = computed(() => [
  {
    id: "text",
    href: "#text",
    label: "Text",
    icon: IcBaselineSubject,
  },
  {
    id: "details",
    href: "#details",
    label: "Details",
    icon: IcOutlineInfo,
  },
]);
</script>

<template>
  <div v-if="currentTranslation" class="container">
    <div class="flex items-center gap-8 print:hidden">
      <RisBreadcrumb :items="breadcrumbItems" class="grow" />
      <client-only>
        <ActionsMenu :permalink />
      </client-only>
    </div>

    <div class="dokumentenkopf mt-24 mb-48 max-w-prose">
      <hgroup>
        <p
          v-if="currentTranslation?.translationOfWork"
          class="word-wrap ris-heading3-regular mb-12 hyphens-auto"
        >
          {{ currentTranslation.translationOfWork }}
        </p>

        <h1
          v-if="currentTranslation?.name"
          class="ris-heading2-bold max-w-title mb-48 text-balance wrap-break-word hyphens-auto"
        >
          {{ currentTranslation.name }}
        </h1>
      </hgroup>
    </div>
    <Message
      v-if="germanOriginal"
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

  <RisTabs :tabs="tabs">
    <template #default="{ activeTab, isClient }">
      <section
        id="text"
        :class="tabPanelClass"
        :hidden="isClient && activeTab !== 'text'"
      >
        <div class="container">
          <h2 class="sr-only">Text</h2>
          <section class="max-w-prose" v-html="html" />
        </div>
      </section>

      <section
        id="details"
        :class="tabPanelClass"
        :hidden="isClient && activeTab !== 'details'"
        aria-labelledby="detailsTabPanelTitle"
      >
        <div class="container">
          <h2 id="detailsTabPanelTitle" class="ris-heading3-bold my-24">
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
        </div>
      </section>
    </template>
  </RisTabs>
</template>
